package com.sizeofint.games.marioex.scene;

import org.andengine.engine.camera.hud.HUD;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;

import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.sizeofint.games.marioex.base.BaseScene;
import com.sizeofint.games.marioex.constants.Action;
import com.sizeofint.games.marioex.constants.GameConstants;
import com.sizeofint.games.marioex.dynamics.Bullet;
import com.sizeofint.games.marioex.dynamics.Player;
import com.sizeofint.games.marioex.manager.ResourcesManager;
import com.sizeofint.games.marioex.manager.SceneManager;
import com.sizeofint.games.marioex.manager.SceneManager.SceneType;

public class GameScene extends BaseScene {

	private Player player;
	private TMXTiledMap mTMXTiledMap;
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;

	@Override
	public void createScene() {
		createBackground();
		createHUD();
		createPhysics();
		loadLevel(1);

	}

	private void loadLevel(int j) {
		try {
			TMXLoader tmxLoader = new TMXLoader(
					ResourcesManager.getInstance().activity.getAssets(),
					engine.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA, vbom,
					new ITMXTilePropertiesListener() {
						@Override
						public void onTMXTileWithPropertiesCreated(
								final TMXTiledMap pTMXTiledMap,
								final TMXLayer pTMXLayer,
								final TMXTile pTMXTile,
								final TMXProperties<TMXTileProperty> pTMXTileProperties) {

						}
					});

			this.mTMXTiledMap = tmxLoader.loadFromAsset("worlds/world" + j
					+ ".tmx");

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		for (int i = 0; i < this.mTMXTiledMap.getTMXLayers().size(); i++) {
			TMXLayer layer = this.mTMXTiledMap.getTMXLayers().get(i);
			this.attachChild(layer);
		}

		for (final TMXObjectGroup group : this.mTMXTiledMap
				.getTMXObjectGroups()) {
			for (final TMXObject object : group.getTMXObjects()) {

				if (group.getName().equals("Unwalkable")) {
					Rectangle rect = new Rectangle(object.getX(),
							object.getY(), object.getWidth(),
							object.getHeight(), vbom);

					FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(
							0.0f, 0.0f, 1f, false,
							GameConstants.CATEGORYBIT_WALL,
							GameConstants.MASKBITS_WALL, (short) 0);

					PhysicsFactory.createBoxBody(physicsWorld, rect,
							BodyType.StaticBody, boxFixtureDef);

					rect.setVisible(false);

					final PhysicsHandler physicsHandler = new PhysicsHandler(
							rect);
					rect.registerUpdateHandler(physicsHandler);

					attachChild(rect);
				} else if (group.getName().equals("Enemies")) {

				} else if (group.getName().equals("enemyunwalkable")) {

					Rectangle rect2 = new Rectangle(object.getX(),
							object.getY(), object.getWidth(),
							object.getHeight(), vbom);

					FixtureDef boxFixtureDef2 = PhysicsFactory
							.createFixtureDef(0.0f, 0.0f, 1f, false,
									GameConstants.CATEGORYBIT_ENEMYWALL,
									GameConstants.MASKBITS_ENEMYWALL, (short) 0);

					Body enemyWall = PhysicsFactory.createBoxBody(physicsWorld,
							rect2, BodyType.StaticBody, boxFixtureDef2);

					enemyWall.setUserData("enemyunwalkable");

					rect2.setVisible(false);

					final PhysicsHandler physicsHandler2 = new PhysicsHandler(
							rect2);
					rect2.registerUpdateHandler(physicsHandler2);

					attachChild(rect2);
				}
			}
		}

		player = new Player(200, 400, vbom, camera, physicsWorld) {
			@Override
			public void onDie() {

			}

			@Override
			public void shoot() {
				Bullet bullet = new Bullet(
						((lastdirection == Action.MOVELEFT) ? (this.getX() - 8)
								: (this.getX() + 18)), this.getY() + 5, vbom,
						camera, physicsWorld, lastdirection) {

					@Override
					public void onDestroy() {

						final Bullet bullet = this;

						((BaseGameActivity) activity)
								.runOnUpdateThread(new Runnable() {

									@Override
									public void run() {
										physicsWorld
												.unregisterPhysicsConnector(physicsWorld
														.getPhysicsConnectorManager()
														.findPhysicsConnectorByShape(
																bullet));
										bullet.getBody().setActive(false);
										physicsWorld.destroyBody(bullet
												.getBody());
										GameScene.this.detachChild(bullet);
									}

								});

					}

				};

				GameScene.this.attachChild(bullet);

			}
		};

		attachChild(player);

	}

	private void createPhysics() {
		physicsWorld = new PhysicsWorld(new Vector2(0f,
				GameConstants.GRAVITY_EARTH), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}

	private void createHUD() {

		gameHUD = new HUD();

		gameHUD.setTouchAreaBindingOnActionDownEnabled(true);
		gameHUD.setTouchAreaBindingOnActionMoveEnabled(true);

		final Sprite jumpButton = new Sprite(GameConstants.CAMERA_WIDTH - 50
				- ResourcesManager.getInstance().tiledTextureJump.getWidth(),
				GameConstants.CAMERA_HEIGHT
						- ResourcesManager.getInstance().tiledTextureJump
								.getHeight() - 30,
				ResourcesManager.getInstance().tiledTextureJump, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				if (pSceneTouchEvent.isActionDown()) {

					player.jump();

				} else if (pSceneTouchEvent.isActionUp()) {

				}

				return true;
			};
		};

		final Sprite shootButton = new Sprite(GameConstants.CAMERA_WIDTH - 50
				- ResourcesManager.getInstance().tiledTextureshoot.getWidth(),
				GameConstants.CAMERA_HEIGHT
						- ResourcesManager.getInstance().tiledTextureJump
								.getHeight()
						- ResourcesManager.getInstance().tiledTextureshoot
								.getHeight() - 60,
				ResourcesManager.getInstance().tiledTextureshoot, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				if (pSceneTouchEvent.isActionDown()) {

					player.shoot();

				} else if (pSceneTouchEvent.isActionUp()) {

				}

				return true;
			};
		};

		final Sprite leftArrowButton = new Sprite(15,
				GameConstants.CAMERA_HEIGHT
						- ResourcesManager.getInstance().tiledTextureleftarrow
								.getHeight() - 30,
				ResourcesManager.getInstance().tiledTextureleftarrow, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				if (pSceneTouchEvent.isActionDown()) {

					player.move(Action.MOVELEFT);

				} else if (pSceneTouchEvent.isActionUp()) {

					player.move(Action.STOP);

				}

				return true;
			};
		};
		

		final Sprite rightArrowButton = new Sprite(
				ResourcesManager.getInstance().tiledTextureleftarrow.getWidth()
						+ (60 + 15),
				GameConstants.CAMERA_HEIGHT
						- ResourcesManager.getInstance().tiledTextureleftarrow
								.getHeight() - 30,
				ResourcesManager.getInstance().tiledTexturerightarrow, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				if (pSceneTouchEvent.isActionDown()) {

					player.move(Action.MOVERIGHT);

				} else if (pSceneTouchEvent.isActionUp()) {

					player.move(Action.STOP);

				}

				return true;
			};
		};
		gameHUD.attachChild(jumpButton);
		gameHUD.attachChild(shootButton);
		gameHUD.attachChild(leftArrowButton);
		gameHUD.attachChild(rightArrowButton);

		gameHUD.registerTouchArea(jumpButton);
		gameHUD.registerTouchArea(shootButton);
		gameHUD.registerTouchArea(leftArrowButton);
		gameHUD.registerTouchArea(rightArrowButton);

		camera.setHUD(gameHUD);
	}

	private void createBackground() {
		setBackground(new Background(0.18f, 0.74f, 0.98f));
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setChaseEntity(null); // TODO
		camera.setCenter(400, 240);
	}

	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			public void beginContact(Contact contact) {

				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x2.getUserData() != null) {

					if (x2.getUserData().toString() == "playerFeet") {
						player.increaseFootContacts();
					}
				}

				if (x1.getUserData() != null) {

					if (x1.getUserData().toString() == "playerFeet") {
						player.increaseFootContacts();
					}
				}

				if (x1.getBody().getUserData() instanceof Bullet) {
					Bullet bu = (Bullet) x1.getBody().getUserData();
					bu.getBody().setUserData(null);
					bu.onDestroy();
				}
				if (x2.getBody().getUserData() instanceof Bullet) {
					Bullet bu = (Bullet) x2.getBody().getUserData();
					bu.getBody().setUserData(null);
					bu.onDestroy();
				}

			}

			public void endContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x2.getUserData() != null) {
					if (x2.getUserData().toString() == "playerFeet")
						player.decreaseFootContacts();
				}
				if (x1.getUserData() != null) {
					if (x1.getUserData().toString() == "playerFeet")
						player.decreaseFootContacts();
				}
			}

			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		};
		return contactListener;
	}

	@Override
	public void onManagedUpdate(final float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		physicsWorld.onUpdate(pSecondsElapsed);
		Entity e = new Entity();

		int tilemapheight = this.mTMXTiledMap.getTileHeight()
				* this.mTMXTiledMap.getTileRows(), mapW = this.mTMXTiledMap
				.getTileWidth() * this.mTMXTiledMap.getTileColumns();

		float y = (tilemapheight - (GameConstants.CAMERA_HEIGHT / 2));

		if (player.getX() > (GameConstants.CAMERA_WIDTH / 2)
				&& player.getX() < (mapW - GameConstants.CAMERA_WIDTH / 2))
			e.setPosition(player.getX(), y);
		else if (player.getX() < (GameConstants.CAMERA_WIDTH / 2))
			e.setPosition((GameConstants.CAMERA_WIDTH / 2), y);
		else if (player.getX() > (mapW - GameConstants.CAMERA_WIDTH / 2))
			e.setPosition((mapW - GameConstants.CAMERA_WIDTH / 2), y);

		camera.setChaseEntity(e);

		final MoveModifier modifier = new MoveModifier(30, e.getX(),
				player.getX(), e.getY(), y) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				camera.setChaseEntity(null);
			}
		};

		e.registerEntityModifier(modifier);
	}

}
