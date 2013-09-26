package com.febi.games;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
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
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
//import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;


//import org.andengine.examples.EaseFunctionExample;


public class Game extends SimpleBaseGameActivity implements ContactListener {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 750;

	// ===========================================================
	// Fields
	// ===========================================================
	AnimatedSprite player;
	Boolean isPlayerJuming = false;
	private BoundCamera mBoundChaseCamera;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;

	/* Hop Button */
	private BitmapTextureAtlas hopButtonAtlas;
	private TextureRegion tiledTextureHop;
	/* Hop Button */
	

	private ArrayList<Rectangle> walls = new ArrayList<Rectangle>();

	private ArrayList<Body> BombsToDelete = new ArrayList<Body>();

	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	Scene scene;
	private PhysicsWorld mPhysicsWorld;

	private Body mPlayerBody;
	private static final int PLAYER_VELOCITY = 1;
	Sprite aircraf;

	public enum PlayerDirection {
		LEFT, RIGHT, DOWN, UP, NONE;
	}

	PlayerDirection lastdirection = PlayerDirection.UP;

	private BitmapTextureAtlas leftarrowButtonAtlas;
	private TextureRegion tiledTextureleftarrow;
	private BitmapTextureAtlas rightarrowButtonAtlas;
	private TextureRegion tiledTexturerightarrow;
	private Music mMusic;

	@Override
	public EngineOptions onCreateEngineOptions() {

		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH,
				CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		if (MultiTouch.isSupported(this)) {
			if (MultiTouch.isSupportedDistinct(this)) {
				Toast.makeText(
						this,
						"MultiTouch detected --> Both controls will work properly!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						this,
						"MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(
					this,
					"Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.",
					Toast.LENGTH_LONG).show();
		}

		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		// BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("mmap/");

		/* mario */
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 150, 66, TextureOptions.DEFAULT);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"mario.png", 0, 0, 10, 2);

		this.mBitmapTextureAtlas.load();

		/* leftarrow button */
		this.leftarrowButtonAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 50, 50, TextureOptions.BILINEAR);
		this.tiledTextureleftarrow = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.leftarrowButtonAtlas, this,
						"leftarrow.png", 0, 0);

		this.leftarrowButtonAtlas.load();
		/* leftarrow button */

		/* rightarrow button */
		this.rightarrowButtonAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 50, 50, TextureOptions.BILINEAR);
		this.tiledTexturerightarrow = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.rightarrowButtonAtlas, this,
						"rightarrow.png", 0, 0);

		this.rightarrowButtonAtlas.load();
		/* rightarrow button */

		/* Hop button */
		this.hopButtonAtlas = new BitmapTextureAtlas(this.getTextureManager(),
				96, 96, TextureOptions.BILINEAR);
		this.tiledTextureHop = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.hopButtonAtlas, this, "hopbutton.png", 0,
						0);

		this.hopButtonAtlas.load();
		/* Hop button */

		try {
			this.mMusic = MusicFactory.createMusicFromAsset(
					this.mEngine.getMusicManager(), this, "track1.ogg");
			this.mMusic.setLooping(true);
		} catch (final IOException e) {
			Debug.e(e);
		}

	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene();
		
		scene.setBackground(new Background(0.18f, 0.74f, 0.98f));
		scene.setBackgroundEnabled(true);

		final HUD hud = new HUD();

		hud.setTouchAreaBindingOnActionDownEnabled(true);
		hud.setTouchAreaBindingOnActionMoveEnabled(true);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0f, 9.8f), false);
		this.mPhysicsWorld.setContactListener(this);
		mPhysicsWorld.setAutoClearForces(true);

		scene.registerUpdateHandler(this.mPhysicsWorld);
		
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(),
					this.mEngine.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA,
					this.getVertexBufferObjectManager(),
					new ITMXTilePropertiesListener() {
						@Override
						public void onTMXTileWithPropertiesCreated(
								final TMXTiledMap pTMXTiledMap,
								final TMXLayer pTMXLayer,
								final TMXTile pTMXTile,
								final TMXProperties<TMXTileProperty> pTMXTileProperties) {

						}
					});

			this.mTMXTiledMap = tmxLoader.loadFromAsset("marioWorld.tmx");

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		for (int i = 0; i < this.mTMXTiledMap.getTMXLayers().size(); i++) {
			TMXLayer layer = this.mTMXTiledMap.getTMXLayers().get(i);
			scene.attachChild(layer);
		}

		this.createUnwalkableObjects(mTMXTiledMap);

		player = new AnimatedSprite(200, 400, this.mPlayerTextureRegion,
				this.getVertexBufferObjectManager());
		this.mBoundChaseCamera.setChaseEntity(player);
		
		this.mMusic.play();

		final PhysicsHandler physicsHandler = new PhysicsHandler(player);

		player.registerUpdateHandler(physicsHandler);

		final Sprite hopButton = new Sprite(CAMERA_WIDTH - 50
				- this.tiledTextureHop.getWidth(), CAMERA_HEIGHT
				- this.tiledTextureHop.getHeight() - 30, this.tiledTextureHop,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				Log.v("FEBI", "HOP TUCH!!!!");
				if (pSceneTouchEvent.isActionDown()) {

					Game.this.makeJump();
				} else if (pSceneTouchEvent.isActionUp()) {

					mPlayerBody.setLinearVelocity(0, 0);
				}
				return true;
			};
		};

		final Sprite leftArrowButton = new Sprite(0, CAMERA_HEIGHT
				- this.tiledTextureleftarrow.getHeight() - 30,
				this.tiledTextureleftarrow, this.getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				if (pSceneTouchEvent.isActionDown()) {

					lastdirection = PlayerDirection.LEFT;

					if (!player.isAnimationRunning())
						player.animate(new long[] { 100, 100, 100, 100, 100,
								100, 100, 100, 100, 100 }, 10, 19, true);
					// lastdirection = direction;

					mPlayerBody.setLinearVelocity(-1 * PLAYER_VELOCITY, 0);

					// player.stopAnimation();

				} else if (pSceneTouchEvent.isActionUp()) {

					player.stopAnimation();
					mPlayerBody.setLinearVelocity(0, 0);

				}

				return true;
			};
		};

		final Sprite rightArrowButton = new Sprite(
				this.tiledTextureleftarrow.getWidth() + 30, CAMERA_HEIGHT
						- this.tiledTextureleftarrow.getHeight() - 30,
				this.tiledTexturerightarrow,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				if (pSceneTouchEvent.isActionDown()) {

					lastdirection = PlayerDirection.RIGHT;

					if (!player.isAnimationRunning())
						player.animate(new long[] { 100, 100, 100, 100, 100,
								100, 100, 100, 100, 100 }, 0, 9, true);
					// lastdirection = direction;

					mPlayerBody.setLinearVelocity(1 * PLAYER_VELOCITY, 0);

					// player.stopAnimation();

				} else if (pSceneTouchEvent.isActionUp()) {

					player.stopAnimation();
					mPlayerBody.setLinearVelocity(0, 0);

				}

				return true;
			};
		};

		hud.attachChild(hopButton);
		hud.attachChild(leftArrowButton);
		hud.attachChild(rightArrowButton);

		hud.registerTouchArea(hopButton);
		hud.registerTouchArea(leftArrowButton);
		hud.registerTouchArea(rightArrowButton);

		this.mBoundChaseCamera.setHUD(hud);

		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(
				0f, 0f, 0f);
		mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, player,
				BodyType.DynamicBody, playerFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				player, mPlayerBody, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				// mBoundChaseCamera.updateChaseEntity();
			}
		});

		scene.attachChild(player);

		// Log.v("FEBI","mTMXTiledMap H:"+(mTMXTiledMap.getTileHeight()*mTMXTiledMap.getTileRows()));
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				mPhysicsWorld.onUpdate(pSecondsElapsed);

				for (int i = 0; i < BombsToDelete.size(); i++) {
					Body g = BombsToDelete.get(i);
					BombsToDelete.remove(i);
					Game.this.destroyBody(g);
				}

				Entity e = new Entity();

				// Log.v("FEBI","aircraft Y:"+aircraf.getY());
				int tilemapheight = mTMXTiledMap.getTileHeight()
						* mTMXTiledMap.getTileRows();

				float y = (player.getY() > (tilemapheight - (CAMERA_HEIGHT / 2))) ? (tilemapheight - (CAMERA_HEIGHT / 2))
						: ((player.getY() < (CAMERA_HEIGHT / 2)) ? (CAMERA_HEIGHT / 2)
								: player.getY());

				y = (tilemapheight - (CAMERA_HEIGHT / 2));

				e.setPosition(player.getX(), y);
				mBoundChaseCamera.setChaseEntity(e);

				final MoveModifier modifier = new MoveModifier(30, e.getX(),
						player.getX(), e.getY(), y) {
					@Override
					protected void onModifierFinished(IEntity pItem) {
						super.onModifierFinished(pItem);
						mBoundChaseCamera.setChaseEntity(null);
					}
				};

				e.registerEntityModifier(modifier);

			}
		});

		return scene;
	}

	private void destroyBody(Body b) {

		if (b != null) {

			mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld
					.getPhysicsConnectorManager().findPhysicsConnectorByShape(
							(Sprite) b.getUserData()));
			Game.this.mPhysicsWorld.destroyBody(b);
			scene.detachChild((Sprite) b.getUserData());
			b = null;
			// System.gc();

		}
	}

	private void createUnwalkableObjects(TMXTiledMap map) {
		// Loop through the object groups
		for (final TMXObjectGroup group : this.mTMXTiledMap
				.getTMXObjectGroups()) {
			for (final TMXObject object : group.getTMXObjects()) {

				final Rectangle rect = new Rectangle(object.getX(),
						object.getY(), object.getWidth(), object.getHeight(),
						this.getVertexBufferObjectManager());

				final FixtureDef boxFixtureDef = PhysicsFactory
						.createFixtureDef(0.0f, 0.0f, 0.0f);
				PhysicsFactory.createBoxBody(this.mPhysicsWorld, rect,
						BodyType.StaticBody, boxFixtureDef);

				rect.setVisible(false);

				// rect.setColor(1f, 0, 0, 1.0f);

				final PhysicsHandler physicsHandler = new PhysicsHandler(rect);
				rect.registerUpdateHandler(physicsHandler);

				walls.add(rect);
				scene.attachChild(rect);

			}
		}

	}

	Boolean isCollision() {

		for (Rectangle rect : walls) {

			if (rect.collidesWith(player))
				return true;
		}
		return false;
	}

	private void makeJump() {
		Log.v("FEBI", "JUMPING!!!!");
		// Log.v("FEBI", String.valueOf(isPlayerJuming));
		if (true) {
			if (lastdirection == PlayerDirection.RIGHT)
				mPlayerBody.setLinearVelocity(new Vector2(mPlayerBody
						.getLinearVelocity().x + 5, -10));
			else if (lastdirection == PlayerDirection.LEFT)
				mPlayerBody.setLinearVelocity(new Vector2(mPlayerBody
						.getLinearVelocity().x - 5, -10));
		}

	}

	@Override
	public void beginContact(final Contact contact) {
		// TODO Auto-generated method stub

		//Log.v("FEBI", "beginContact()");

		Sprite a, b;
		if ((a = (Sprite) contact.getFixtureA().getBody().getUserData()) != null)
			if (a.getUserData() != null)
				if (a.getUserData().equals("BOMBA")
						&& !BombsToDelete.contains(contact.getFixtureA()
								.getBody())) {
					BombsToDelete.add(contact.getFixtureA().getBody());
					Log.v("FEBI", "Object A added to remove sheduler!");
				}
		if ((b = (Sprite) contact.getFixtureB().getBody().getUserData()) != null)
			if (b.getUserData() != null)
				if (b.getUserData().equals("BOMBA")
						&& !BombsToDelete.contains(contact.getFixtureB()
								.getBody())) {
					BombsToDelete.add(contact.getFixtureB().getBody());
					Log.v("FEBI", "Object B added to remove sheduler!");
				}

		// System.gc();

	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		// Log.v("FEBI", "endContact()");

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		// Log.v("FEBI", "preSolve()");

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
