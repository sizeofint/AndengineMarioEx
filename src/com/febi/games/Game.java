package com.febi.games;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
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
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
<<<<<<< HEAD
//import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;

//import org.andengine.examples.EaseFunctionExample;
=======
>>>>>>> World, sprite & sound changes

public class Game extends SimpleBaseGameActivity implements ContactListener {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 750;
	private static final int PLAYER_VELOCITY = 1;

	// ===========================================================
	// Fields
	// ===========================================================
	AnimatedSprite player;
	Boolean isPlayerJuming = false;
	private BoundCamera mBoundChaseCamera;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;
<<<<<<< HEAD

	/* Hop Button */
	private BitmapTextureAtlas hopButtonAtlas;
	private TextureRegion tiledTextureHop;
	/* Hop Button */

	private ArrayList<Rectangle> walls = new ArrayList<Rectangle>();

	private ArrayList<Body> BombsToDelete = new ArrayList<Body>();

=======
>>>>>>> World, sprite & sound changes
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	Scene scene;
	private PhysicsWorld mPhysicsWorld;

	private Body mPlayerBody;
	Sprite aircraf;

	public enum PlayerDirection {
		LEFT, RIGHT, DOWN, UP, NONE;
	}
	private Boolean enableJumping=true;

	PlayerDirection lastdirection = PlayerDirection.UP;

	private BitmapTextureAtlas leftarrowButtonAtlas;
	private TextureRegion tiledTextureleftarrow;
	private BitmapTextureAtlas rightarrowButtonAtlas;
	private TextureRegion tiledTexturerightarrow;
	private Music mMusic;
<<<<<<< HEAD
	private int numFootContacts=0;
	
=======
	private int numFootContacts = 0;
	private Boolean isMoveing = false;
	private Boolean isJumping = false;
	private BitmapTextureAtlas jumpButtonAtlas;
	private TextureRegion tiledTextureJump;
	private Sound mJumpSound;

>>>>>>> World, sprite & sound changes
	@Override
	public EngineOptions onCreateEngineOptions() {

		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH,
				CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		engineOptions.getAudioOptions().setNeedsSound(true);

		return engineOptions;
	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {

		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	@Override
	public void onCreateResources() {
<<<<<<< HEAD
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
				.createFromAsset(this.hopButtonAtlas, this, "jumpbutton.png",
						0, 0);

		this.hopButtonAtlas.load();
		/* Hop button */
=======

		try {
			/* mario */
			this.mBitmapTextureAtlas = new BitmapTextureAtlas(
					this.getTextureManager(), 108, 68, TextureOptions.DEFAULT);
			this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(this.mBitmapTextureAtlas, this,
							"mario2.png", 0, 0, 6, 2);
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

			/* jump button */
			this.jumpButtonAtlas = new BitmapTextureAtlas(
					this.getTextureManager(), 96, 96, TextureOptions.BILINEAR);
			this.tiledTextureJump = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(this.jumpButtonAtlas, this,
							"jumpbutton.png", 0, 0);

			this.jumpButtonAtlas.load();
			/* jump button */
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
>>>>>>> World, sprite & sound changes

		try {
			this.mMusic = MusicFactory.createMusicFromAsset(
					this.mEngine.getMusicManager(), this, "track1.ogg");
			this.mJumpSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "smb_jump-super.ogg");
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

			this.mTMXTiledMap = tmxLoader.loadFromAsset("world1.tmx");

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
<<<<<<< HEAD
		
=======
>>>>>>> World, sprite & sound changes

		this.mMusic.play();

		final PhysicsHandler physicsHandler = new PhysicsHandler(player);

		player.registerUpdateHandler(physicsHandler);

		final Sprite jumpButton = new Sprite(CAMERA_WIDTH - 50
				- this.tiledTextureJump.getWidth(), CAMERA_HEIGHT
				- this.tiledTextureJump.getHeight() - 30,
				this.tiledTextureJump, this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
<<<<<<< HEAD

				if (pSceneTouchEvent.isActionDown()) {
					Log.v("FEBI", "HOP DOWN!!!!");
					if(Game.this.numFootContacts>0)
						Game.this.makeJump();
				} else if (pSceneTouchEvent.isActionUp()) {
					Log.v("FEBI", "HOP UP!!!!");
					//mPlayerBody.setLinearVelocity(0, 0);
					//Game.this.playerIsjumping=false;
=======

				if (pSceneTouchEvent.isActionDown()) {

					if (Game.this.numFootContacts > 0)
						Game.this.jump();
				} else if (pSceneTouchEvent.isActionUp()) {

>>>>>>> World, sprite & sound changes
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

					Game.this.isMoveing = true;
					lastdirection = PlayerDirection.LEFT;

					if (!player.isAnimationRunning())
<<<<<<< HEAD
						player.animate(new long[] { 100, 100, 100, 100, 100,
								100, 100, 100, 100, 100 }, 10, 19, true);
					// lastdirection = direction;
					
					
					
=======
						player.animate(new long[] { 200, 200, 200 }, 7, 9, true);

>>>>>>> World, sprite & sound changes
					mPlayerBody.setLinearVelocity(-1 * PLAYER_VELOCITY, 0);

				} else if (pSceneTouchEvent.isActionUp()) {
					if (Game.this.isJumping)
						player.stopAnimation(11);
					else
						player.stopAnimation(6);
					mPlayerBody.setLinearVelocity(0, 0);
					Game.this.isMoveing = false;
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
					Game.this.isMoveing = true;
					lastdirection = PlayerDirection.RIGHT;

					if (!player.isAnimationRunning())
<<<<<<< HEAD
						player.animate(new long[] { 100, 100, 100, 100, 100,
								100, 100, 100, 100, 100 }, 0, 9, true);
					// lastdirection = direction;
					
=======
						player.animate(new long[] { 200, 200, 200 }, 1, 3, true);

>>>>>>> World, sprite & sound changes
					mPlayerBody.setLinearVelocity(1 * PLAYER_VELOCITY, 0);

				} else if (pSceneTouchEvent.isActionUp()) {
					Game.this.isMoveing = false;
					if (Game.this.isJumping)
						player.stopAnimation(5);
					else
						player.stopAnimation(0);
					mPlayerBody.setLinearVelocity(0, 0);

				}

				return true;
			};
		};

		hud.attachChild(jumpButton);
		hud.attachChild(leftArrowButton);
		hud.attachChild(rightArrowButton);

		hud.registerTouchArea(jumpButton);
		hud.registerTouchArea(leftArrowButton);
		hud.registerTouchArea(rightArrowButton);

		this.mBoundChaseCamera.setHUD(hud);

		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(0f,
				0f, 0f);
<<<<<<< HEAD
		
		
		
		
		mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, player,
				BodyType.DynamicBody, playerFixtureDef);
	
		//mPlayerBody.setUserData("PlayerBody");
		
		final PolygonShape mPoly = new PolygonShape();
		mPoly.setAsBox(.1f,.1f, new Vector2(0,.5f),0);
		final FixtureDef pFixtureDef = PhysicsFactory.createFixtureDef(0f, 0f, 0f,true); //not physic feet
		pFixtureDef.shape = mPoly;
		Fixture mFeet = mPlayerBody.createFixture(pFixtureDef);
		mFeet.setUserData("PlayerFeet");
		
		mPoly.dispose();
		
		
=======

		mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, player,
				BodyType.DynamicBody, playerFixtureDef);

		final PolygonShape mPoly = new PolygonShape();
		mPoly.setAsBox(.1f, .1f, new Vector2(0, .5f), 0);
		final FixtureDef pFixtureDef = PhysicsFactory.createFixtureDef(0f, 0f,
				0f, true);
		pFixtureDef.shape = mPoly;
		Fixture mFeet = mPlayerBody.createFixture(pFixtureDef);
		mFeet.setUserData("PlayerFeet");

		mPoly.dispose();

>>>>>>> World, sprite & sound changes
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				player, mPlayerBody, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				// mBoundChaseCamera.updateChaseEntity();
			}
		});

		scene.attachChild(player);

		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				mPhysicsWorld.onUpdate(pSecondsElapsed);

				Entity e = new Entity();

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
		//scene.attachChild(new DebugRenderer(mPhysicsWorld, getVertexBufferObjectManager()));

		return scene;
	}

	private void createUnwalkableObjects(TMXTiledMap map) {
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

				final PhysicsHandler physicsHandler = new PhysicsHandler(rect);
				rect.registerUpdateHandler(physicsHandler);

				scene.attachChild(rect);

			}
		}

	}

	private void jump() {
		this.mJumpSound.play();
		if (lastdirection == PlayerDirection.RIGHT) {
			player.animate(new long[] { 100 }, new int[] { 5 });
			mPlayerBody.setLinearVelocity(new Vector2(
					((this.isMoveing) ? (PLAYER_VELOCITY + 1) : mPlayerBody
							.getLinearVelocity().x), -9));
			// player.stopAnimation(5);
		} else if (lastdirection == PlayerDirection.LEFT) {
			player.animate(new long[] { 100 }, new int[] { 11 });
			mPlayerBody.setLinearVelocity(new Vector2(
					((this.isMoveing) ? (-PLAYER_VELOCITY - 1) : mPlayerBody
							.getLinearVelocity().x), -9));
			// player.stopAnimation(11);
		}
	}

<<<<<<< HEAD
	private void makeJump() {
		Log.v("FEBI", "JUMPING!!!!");
		// Log.v("FEBI", String.valueOf(isPlayerJuming));
		if (this.enableJumping) {
			if (lastdirection == PlayerDirection.RIGHT)
				mPlayerBody.setLinearVelocity(new Vector2(mPlayerBody
						.getLinearVelocity().x, -10));
			else if (lastdirection == PlayerDirection.LEFT)
				mPlayerBody.setLinearVelocity(new Vector2(mPlayerBody
						.getLinearVelocity().x, -10));
		}
=======
	public void jumpingStart() {
		this.isJumping = true;
	}
>>>>>>> World, sprite & sound changes

	public void jumpingEnd() {
		this.isJumping = false;
		if (this.isMoveing) {
			if (lastdirection == PlayerDirection.LEFT) {
				player.animate(new long[] { 200, 200, 200 }, 7, 9, true);
				mPlayerBody.setLinearVelocity(new Vector2(-PLAYER_VELOCITY, 0));
			} else if (lastdirection == PlayerDirection.RIGHT) {
				player.animate(new long[] { 200, 200, 200 }, 1, 3, true);
				mPlayerBody.setLinearVelocity(new Vector2(PLAYER_VELOCITY, 0));
			}
		} else {
			if (lastdirection == PlayerDirection.LEFT) {
				player.stopAnimation(6);
			} else if (lastdirection == PlayerDirection.RIGHT) {
				player.stopAnimation(0);
			}
		}
	}

	@Override
	public void beginContact(final Contact contact) {
		// TODO Auto-generated method stub

		Log.v("FEBI", "beginContact()");
<<<<<<< HEAD
		
		//if(contact.getFixtureA().getBody().getUserData()!=null)
		// Log.v("FEBI", contact.getFixtureA().getBody().getUserData().toString());
		
		//if(contact.getFixtureB().getBody().getUserData()!=null)
		//Log.v("FEBI", contact.getFixtureB().getBody().getUserData().toString());
		
		
		if(contact.getFixtureB().getUserData()!=null) {
			Log.v("FEBI", contact.getFixtureB().getUserData().toString());
			if(contact.getFixtureB().getUserData().toString()=="PlayerFeet")
				this.numFootContacts++;
		}
		if(contact.getFixtureA().getUserData()!=null) {
			Log.v("FEBI", contact.getFixtureA().getUserData().toString());
			if(contact.getFixtureA().getUserData().toString()=="PlayerFeet")
				this.numFootContacts++;
		}
		/*
		int numPoints = contact.getWorldManifold().getNumberOfContactPoints();
		Vector2[] vec = contact.getWorldManifold().getPoints();
		float maxYF=0;
		for (int i = 0; i < (numPoints); i++) {
			maxYF=(vec[i].y>maxYF)?vec[i].y:maxYF;
		}
		if(contact.getFixtureA().getBody().getTransform().getPosition().y>=maxYF)  {
			this.enableJumping=true;
			this.contactToNotGround=false;
			Log.v("FEBI", "GROUND!!!  "+contact.getFixtureA().getBody().getTransform().getPosition().y+"  "+maxYF);
		} else 
			this.contactToNotGround=true;
		*/
		// Log.v("FEBI", "beginContact()");
	/*

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
*/
		// System.gc();
=======

		if (contact.getFixtureB().getUserData() != null) {
			Log.v("FEBI", contact.getFixtureB().getUserData().toString());
			if (contact.getFixtureB().getUserData().toString() == "PlayerFeet")
				this.numFootContacts++;
		}
		if (contact.getFixtureA().getUserData() != null) {
			Log.v("FEBI", contact.getFixtureA().getUserData().toString());
			if (contact.getFixtureA().getUserData().toString() == "PlayerFeet")
				this.numFootContacts++;
		}
		if (this.numFootContacts > 0)
			this.jumpingEnd();
>>>>>>> World, sprite & sound changes

	}

	@Override
	public void endContact(Contact contact) {
<<<<<<< HEAD
		// TODO Auto-generated method stub
		Log.v("FEBI", "endContact()");
		
		if(contact.getFixtureB().getUserData()!=null) {
			Log.v("FEBI", contact.getFixtureB().getUserData().toString());
			if(contact.getFixtureB().getUserData().toString()=="PlayerFeet")
				this.numFootContacts--;
		}
		if(contact.getFixtureA().getUserData()!=null) {
			Log.v("FEBI", contact.getFixtureA().getUserData().toString());
			if(contact.getFixtureA().getUserData().toString()=="PlayerFeet")
				this.numFootContacts--;
		}
		
		
		//int numPoints = contact.getWorldManifold().getNumberOfContactPoints();
		//if(!this.contactToNotGround)
		//	this.enableJumping=false;
		/*
		Vector2[] vec = contact.getWorldManifold().getPoints();
		float maxYF=0;
		for (int i = 0; i < (numPoints); i++) {
			Log.v("FEBI",vec[i].x+"  "+vec[i].y);
			maxYF=(vec[i].y>maxYF)?vec[i].y:maxYF;
		}
		if(contact.getFixtureA().getBody().getTransform().getPosition().y>=maxYF)  {
			//this.enableJumping=false;
			Log.v("FEBI", "GROUND!!!  "+contact.getFixtureA().getBody().getTransform().getPosition().y+"  "+maxYF);
		} else {
			
			//this.enableJumping=true;
		}
		*/
=======
		Log.v("FEBI", "endContact()");

		if (contact.getFixtureB().getUserData() != null) {
			Log.v("FEBI", contact.getFixtureB().getUserData().toString());
			if (contact.getFixtureB().getUserData().toString() == "PlayerFeet")
				this.numFootContacts--;
		}
		if (contact.getFixtureA().getUserData() != null) {
			Log.v("FEBI", contact.getFixtureA().getUserData().toString());
			if (contact.getFixtureA().getUserData().toString() == "PlayerFeet")
				this.numFootContacts--;
		}
		if (this.numFootContacts < 1)
			this.jumpingStart();

>>>>>>> World, sprite & sound changes
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
