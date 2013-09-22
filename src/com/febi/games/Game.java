package com.febi.games;

import java.util.ArrayList;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.opengl.GLES20;
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

	/* BOMB Button */
	private BitmapTextureAtlas bombButtonAtlas;
	private TextureRegion tiledTextureBomb;
	/* BOMB Button */

	/* aircraft */
	private BitmapTextureAtlas aircraftAtlas;
	private TextureRegion tiledTextureaircraft;
	/* aircraft */

	private ArrayList<Rectangle> walls = new ArrayList<Rectangle>();

	private ArrayList<Body> BombsToDelete = new ArrayList<Body>();

	private BitmapTextureAtlas mOnScreenControlTexture;

	private TextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	Scene scene;
	private PhysicsWorld mPhysicsWorld;

	private Body mPlayerBody, maircraftBody;
	// private DigitalOnScreenControl mDigitalOnScreenControl;
	private static final int PLAYER_VELOCITY =1;
	Sprite aircraf;

	public enum PlayerDirection {
		LEFT, RIGHT, DOWN, UP, NONE;
	}

	PlayerDirection lastdirection = PlayerDirection.UP;
	private BitmapTextureAtlas rocketAtlas;
	private TextureRegion tiledTexturerocket;
	private BitmapTextureAtlas bombAtlas;
	private TextureRegion tiledTexturebombi;
	private BitmapTextureAtlas explosionAtlas;
	private TiledTextureRegion tiledTextureexplosion;
	private Sprite BOMBI;

	private Boolean bombDroped = false;
	private BitmapTextureAtlas leftarrowButtonAtlas;
	private TextureRegion tiledTextureleftarrow;
	private BitmapTextureAtlas rightarrowButtonAtlas;
	private TextureRegion tiledTexturerightarrow;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this,
				"The tile the player is walking on will be highlighted.",
				Toast.LENGTH_LONG).show();

		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH,
				CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);

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
		this.leftarrowButtonAtlas = new BitmapTextureAtlas(this.getTextureManager(),
				50, 50, TextureOptions.BILINEAR);
		this.tiledTextureleftarrow = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.leftarrowButtonAtlas, this, "leftarrow.png", 0,
						0);

		this.leftarrowButtonAtlas.load();
		/* leftarrow button */
		
		
		
		/* rightarrow button */
		this.rightarrowButtonAtlas = new BitmapTextureAtlas(this.getTextureManager(),
				50, 50, TextureOptions.BILINEAR);
		this.tiledTexturerightarrow = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.rightarrowButtonAtlas, this, "rightarrow.png", 0,
						0);

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

		/* BOMB Button */
		this.bombButtonAtlas = new BitmapTextureAtlas(this.getTextureManager(),
				100, 100, TextureOptions.BILINEAR);
		this.tiledTextureBomb = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.bombButtonAtlas, this, "bombButton.png",
						0, 0);

		this.bombButtonAtlas.load();
		/* BOMB Button */

		/* rocket */
		this.rocketAtlas = new BitmapTextureAtlas(this.getTextureManager(), 49,
				15, TextureOptions.BILINEAR);
		this.tiledTexturerocket = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.rocketAtlas, this, "rocketanimated.png",
						0, 0);

		this.rocketAtlas.load();

		/* rocket */

		/* Bomb */
		this.bombAtlas = new BitmapTextureAtlas(this.getTextureManager(), 96,
				51, TextureOptions.BILINEAR);
		this.tiledTexturebombi = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.bombAtlas, this, "bomb.png", 0, 0);

		this.bombAtlas.load();

		/* Bomb */

		/* explosion */
		this.explosionAtlas = new BitmapTextureAtlas(this.getTextureManager(),
				640, 128, TextureOptions.DEFAULT);
		this.tiledTextureexplosion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.explosionAtlas, this, "expl.png", 0,
						0, 5, 1);

		this.explosionAtlas.load();

		/* explosion */

		/* aricraft */
		this.aircraftAtlas = new BitmapTextureAtlas(this.getTextureManager(),
				300, 88, TextureOptions.BILINEAR);
		this.tiledTextureaircraft = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.aircraftAtlas, this, "aircraft.png", 0, 0);

		this.aircraftAtlas.load();

		/* aricraft */

		this.mOnScreenControlTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene();

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
							/*
							 * We are going to count the tiles that have the
							 * property "cactus=true" set.
							 */
							// if (pTMXTileProperties.containsTMXProperty(
							// "cactus", "true")) {
							// Game.this.mCactusCount++;
							// }
						}
					});

			this.mTMXTiledMap = tmxLoader.loadFromAsset("marioWorld.tmx");

			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							Game.this,
							"Cactus count in this TMXTiledMap: "
									+ Game.this.mCactusCount, Toast.LENGTH_LONG)
							.show();
				}
			});

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		// final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);

		for (int i = 0; i < this.mTMXTiledMap.getTMXLayers().size(); i++) {
			TMXLayer layer = this.mTMXTiledMap.getTMXLayers().get(i);
			scene.attachChild(layer);
		}

		// scene.attachChild(tmxLayer);

		this.createUnwalkableObjects(mTMXTiledMap);

		/* Make the camera not exceed the bounds of the TMXEntity. */
		// this.mBoundChaseCamera.setBounds(0, 0, tmxLayer.getHeight(),
		// tmxLayer.getWidth());
		// this.mBoundChaseCamera.setBoundsEnabled(true);

		/*
		 * Calculate the coordinates for the face, so its centered on the
		 * camera.
		 */
		final float centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion
				.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion
				.getHeight()) / 2;

		/* Create the sprite and add it to the scene. */
		player = new AnimatedSprite(200, 400,
				this.mPlayerTextureRegion, this.getVertexBufferObjectManager());
		this.mBoundChaseCamera.setChaseEntity(player);

		final PhysicsHandler physicsHandler = new PhysicsHandler(player);

		player.registerUpdateHandler(physicsHandler);

		/*
		 * 
		 * 
		 * 
		 * 
		 * AnimatedSprite expl = new AnimatedSprite(100, 1000,
		 * this.tiledTextureexplosion, this.getVertexBufferObjectManager());
		 * 
		 * final PhysicsHandler physicsHandler2 = new PhysicsHandler(expl);
		 * 
		 * expl.registerUpdateHandler(physicsHandler2); //
		 * scene.detachChild(BOMBI); scene.attachChild(expl);
		 * 
		 * expl.animate(100,true);
		 */

		aircraf = new Sprite(400, 400, this.tiledTextureaircraft,
				this.getVertexBufferObjectManager());

		final Sprite hopButton = new Sprite(CAMERA_WIDTH - 50
				- this.tiledTextureHop.getWidth(), CAMERA_HEIGHT
				- this.tiledTextureHop.getHeight() - 30, this.tiledTextureHop,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {

//					Game.this.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							Toast.makeText(Game.this, "TEST", Toast.LENGTH_LONG)
//									.show();
//						}
//					});

					Game.this.makeJump();
				} else if(pSceneTouchEvent.isActionUp()){
					
					mPlayerBody.setLinearVelocity(0,0);
				}
				return true;
			};
		};
		
		
		
		
		
		
		
		
		
		final Sprite leftArrowButton = new Sprite(0, CAMERA_HEIGHT
				- this.tiledTextureleftarrow.getHeight() - 30, this.tiledTextureleftarrow,
				this.getVertexBufferObjectManager()) {
			
			
			
			

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				
				
				if (pSceneTouchEvent.isActionDown()) {
					
					lastdirection=PlayerDirection.LEFT;
					
					
					if (!player.isAnimationRunning())
						player.animate(new long[] { 100, 100, 100,
								100, 100, 100, 100, 100, 100, 100 }, 10,
								19, true);
					//lastdirection = direction;
					
					mPlayerBody.setLinearVelocity(-1
							* PLAYER_VELOCITY, 0);

					//player.stopAnimation();

					
				} else if(pSceneTouchEvent.isActionUp()) {
					
					player.stopAnimation();
					mPlayerBody.setLinearVelocity(0, 0);
					
				}
				
				return true;
			};
		};
		
		
		final Sprite rightArrowButton = new Sprite(this.tiledTextureleftarrow.getWidth()+30, CAMERA_HEIGHT
				- this.tiledTextureleftarrow.getHeight() - 30, this.tiledTexturerightarrow,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				

				
				if (pSceneTouchEvent.isActionDown()) {
					
					lastdirection=PlayerDirection.RIGHT;
					
					
					if (!player.isAnimationRunning())
						player.animate(new long[] { 100, 100, 100, 100,
								100, 100, 100, 100, 100, 100 }, 0, 9, true);
					//lastdirection = direction;
					
					mPlayerBody.setLinearVelocity(1
							* PLAYER_VELOCITY, 0);

					//player.stopAnimation();

					
				} else if(pSceneTouchEvent.isActionUp()) {
					
					player.stopAnimation();
					mPlayerBody.setLinearVelocity(0, 0);
					
				}
				
				return true;
			};
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		final Sprite bombButton = new Sprite(CAMERA_WIDTH - 50
				- this.tiledTextureHop.getWidth(), CAMERA_HEIGHT
				- this.tiledTextureHop.getHeight() - 200,
				this.tiledTextureBomb, this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {

					Game.this.dropBomb();
				}
				return true;
			};
		};

		hud.attachChild(bombButton);
		hud.attachChild(hopButton);
		hud.attachChild(leftArrowButton);
		hud.attachChild(rightArrowButton);
		// hud.attachChild(previousSprite);

		hud.registerTouchArea(hopButton);
		hud.registerTouchArea(leftArrowButton);
		hud.registerTouchArea(rightArrowButton);
		hud.registerTouchArea(bombButton);
		// hud.registerTouchArea(previousSprite);

		this.mBoundChaseCamera.setHUD(hud);

		// final PhysicsHandler physicsHandler = new PhysicsHandler(player);
		// player.registerUpdateHandler(physicsHandler);

		/*
		 * Now we are going to create a rectangle that will always highlight the
		 * tile below the feet of the pEntity.
		 */
		// final Rectangle currentTileRectangle = new Rectangle(0, 0,
		// this.mTMXTiledMap.getTileWidth(),
		// this.mTMXTiledMap.getTileHeight(),
		// this.getVertexBufferObjectManager());
		// currentTileRectangle.setColor(1, 0, 0, 0.25f);
		// scene.attachChild(currentTileRectangle);

		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(
				10f, 0f, 0f);
		mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld,player,BodyType.DynamicBody, playerFixtureDef);
		maircraftBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld,aircraf, BodyType.KinematicBody, playerFixtureDef);

		// maircraftBody.getWorld().setGravity(new Vector2(0f,0f));

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				player, mPlayerBody, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				// mBoundChaseCamera.updateChaseEntity();
			}
		});

		scene.attachChild(aircraf);

		scene.attachChild(player);
		
		/*

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(
				0, CAMERA_HEIGHT
						- this.mOnScreenControlBaseTextureRegion.getHeight(),
				this.mBoundChaseCamera, this.mOnScreenControlBaseTextureRegion,
				this.mOnScreenControlKnobTextureRegion, 0.1f, 200,
				this.getVertexBufferObjectManager(),
				new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							float pValueX, float pValueY) {

						// Log.v("FEBI", "X: " + String.valueOf(pValueX) +
						// " Y: "
						// + String.valueOf(pValueY));

						PlayerDirection direction = this.getPlayerDirection(
								pValueX, pValueY);
						if (direction != lastdirection
								&& player.isAnimationRunning())
							player.stopAnimation();
						if (!player.isAnimationRunning())
							switch (direction) {
							case DOWN:

								//player.animate(new long[] { 100, 100, 100, 100,
								//		100, 100, 100, 100, 100 }, 18, 26, true);
								//lastdirection = direction;
								break;
							case RIGHT:
								player.animate(new long[] { 100, 100, 100, 100,
										100, 100, 100, 100, 100, 100 }, 0, 9, true);
								lastdirection = direction;
								break;
							case UP:
								//player.animate(new long[] { 100, 100, 100, 100,
								//		100, 100, 100, 100, 100 }, 0, 8, true);
								//lastdirection = direction;
								break;
							case LEFT:
								if (!player.isAnimationRunning())
									player.animate(new long[] { 100, 100, 100,
											100, 100, 100, 100, 100, 100, 100 }, 10,
											19, true);
								lastdirection = direction;
								break;
							default:
								player.stopAnimation();
							}

						// pValueX = (((float) pValueX != 0) ? (float) pValueX
						// : (float) 0.76);
						// pValueY = (((float) pValueY != 0) ? (float) pValueY
						// : (float) 0.46);

						// pValueY = (((float) pValueY != 0) ? (float)
						// (pValueY-30): (float) 0);

						//aircraf.setRotation((float) -this.getAngle(pValueX,
						//		pValueY));

						mPlayerBody.setLinearVelocity(pValueX
								* PLAYER_VELOCITY, pValueY * PLAYER_VELOCITY);

						// Log.v("FEBI", "aircraft position X: " +
						// aircraf.getX()
						// + " Y: " + aircraf.getY());
					}

					private PlayerDirection getPlayerDirection(
							final float pValueX, final float pValueY) {
						if (pValueX == 0 && pValueY == 0)
							return PlayerDirection.NONE;
						double angle = getAngle(pValueX, pValueY); //
						Log.d("FEBI", "Angle: " + angle);
						if (isBetween(68, 113, angle)) {
							return PlayerDirection.UP;
						}
						if (isBetween(248, 293, angle)) {
							return PlayerDirection.DOWN;
						}
						if (isBetween(158, 203, angle)) {
							return PlayerDirection.LEFT;
						}
						if (angle < 23 || angle > 338) {
							return PlayerDirection.RIGHT;
						}
						return PlayerDirection.NONE;
					}

					// Return true if c is between a and b.

					public boolean isBetween(int a, int b, double c) {
						return b > a ? c > a && c < b : c > b && c < a;
					}

					
					private double getAngle(float x, float y) {

						double inRads = Math.atan2(y, x);

						// We need to map to coord system when 0 degree is at 3
						// O'clock, 270 at 12 O'clock
						if (inRads < 0)
							inRads = Math.abs(inRads);
						else
							inRads = 2 * Math.PI - inRads;

						return Math.toDegrees(inRads);
					}

					@Override
					public void onControlClick(
							final AnalogOnScreenControl pAnalogOnScreenControl) {

					}

				});

		analogOnScreenControl.getControlBase().setBlendFunction(
				GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();
		analogOnScreenControl.setTouchAreaBindingOnActionDownEnabled(true);
		analogOnScreenControl.setTouchAreaBindingOnActionMoveEnabled(true);
		scene.setChildScene(analogOnScreenControl);
		
		*/

		// Log.v("FEBI","mTMXTiledMap H:"+(mTMXTiledMap.getTileHeight()*mTMXTiledMap.getTileRows()));
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				mPhysicsWorld.onUpdate(pSecondsElapsed);

				// for (Body Bombd : BombsToDelete) {
				//
				//
				// mPhysicsWorld.unregisterPhysicsConnector(mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape((Sprite)
				// Bombd.getUserData()));
				// Game.this.mPhysicsWorld.destroyBody(Bombd);
				// scene.detachChild((Sprite) Bombd.getUserData());
				//
				// synchronized(BombsToDelete) {
				// BombsToDelete.remove(Bombd);
				// }
				//
				// }

				for (int i = 0; i < BombsToDelete.size(); i++) {
					Body g = BombsToDelete.get(i);
					BombsToDelete.remove(i);
					Game.this.destroyBody(g);
				}

				// for (Body c : BombsToDelete) {

				// BombsToDelete.remove(c);

				// Game.this.destroyBody(c);

				// }

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
			bombDroped = false;

		}
	}

	private void createUnwalkableObjects(TMXTiledMap map) {
		// Loop through the object groups
		for (final TMXObjectGroup group : this.mTMXTiledMap
				.getTMXObjectGroups()) {
			for (final TMXObject object : group.getTMXObjects()) {

				// Log.v("FEBI",
				// String.valueOf(object.getX()) + " "
				// + String.valueOf(object.getY()) + " "
				// + String.valueOf(object.getWidth()) + " "
				// + String.valueOf(object.getHeight()));

				// int
				// objHeight=object.getHeight()-this.mTMXTiledMap.getTileHeight();
				// int
				// objwidth=object.getWidth()-2*this.mTMXTiledMap.getTileWidth();

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

	private void dropBomb() {
		if (bombDroped)
			return;
		bombDroped = true;
		BOMBI = new Sprite((aircraf.getX() + 10), (aircraf.getY() + 120),
				this.tiledTexturebombi, this.getVertexBufferObjectManager());

		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(
				10.0f, 0f, 0f);
		Body mBombBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
				BOMBI, BodyType.DynamicBody, playerFixtureDef);
		BOMBI.setUserData("BOMBA");
		mBombBody.setUserData(BOMBI);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(BOMBI,
				mBombBody, true, true));

		scene.attachChild(BOMBI);

	}

	Boolean isCollision() {

		for (Rectangle rect : walls) {

			if (rect.collidesWith(player))
				return true;
		}
		return false;
	}

	private void makeJump() {
		// Log.v("FEBI", String.valueOf(isPlayerJuming));
		if (true) {
			if (lastdirection == PlayerDirection.RIGHT)
				mPlayerBody.setLinearVelocity(new Vector2(mPlayerBody.getLinearVelocity().x + 5, -5));
			else if (lastdirection == PlayerDirection.LEFT)
				mPlayerBody.setLinearVelocity(new Vector2(mPlayerBody.getLinearVelocity().x - 5, -5));
		}

	}

	@Override
	public void beginContact(final Contact contact) {
		// TODO Auto-generated method stub

		Log.v("FEBI", "beginContact()");

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

		/*
		 * if (getFixtureAObjectData.equals("BOMBA") ||
		 * getFixtureBObjectData.equals("BOMBA")) {
		 * 
		 * //if (a != null) { //
		 * this.mPhysicsWorld.destroyBody(contact.getFixtureA().getBody()); //
		 * scene.detachChild(a); //} //if (b != null) { //
		 * this.mPhysicsWorld.destroyBody(contact.getFixtureB().getBody()); //
		 * scene.detachChild(b); //} AnimatedSprite expl = new
		 * AnimatedSprite(100, 1000, this.tiledTextureexplosion,
		 * this.getVertexBufferObjectManager()); // scene.detachChild(BOMBI);
		 * scene.attachChild(expl);
		 * 
		 * expl.animate(100, false, new IAnimationListener() {
		 * 
		 * @Override public void onAnimationStarted(AnimatedSprite
		 * pAnimatedSprite, int pInitialLoopCount) { // TODO Auto-generated
		 * method stub
		 * 
		 * //Log.v("FEBI", "Animation started!");
		 * 
		 * }
		 * 
		 * @Override public void onAnimationFrameChanged( AnimatedSprite
		 * pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) { // TODO
		 * Auto-generated method stub
		 * 
		 * //Log.v("FEBI", "Animation frame changed!");
		 * 
		 * }
		 * 
		 * @Override public void onAnimationLoopFinished( AnimatedSprite
		 * pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) { //
		 * TODO Auto-generated method stub
		 * 
		 * //Log.v("FEBI", "Animation loop finished!");
		 * //pAnimatedSprite.stopAnimation();
		 * //scene.detachChild(pAnimatedSprite);
		 * 
		 * }
		 * 
		 * @Override public void onAnimationFinished(AnimatedSprite
		 * pAnimatedSprite) { // TODO Auto-generated method stub
		 * 
		 * Log.v("FEBI", "Animation finished!");
		 * 
		 * //scene.detachChild(pAnimatedSprite); // if(a!=null)
		 * scene.detachChild(a); // if(b!=null) scene.detachChild(b); }
		 * 
		 * });
		 * 
		 * 
		 * }
		 */
		// Log.v("FEBI","getFixtureA y:"+String.valueOf(contact.getFixtureA().getBody().));
		// Log.v("FEBI","getFixtureB y:"+String.valueOf(contact.getFixtureB().getBody().localPoint2.y));
		// Log.v("FEBI","getFixtureA y: "+String.valueOf(contact.getFixtureA().getBody().getPosition().y)+"getFixtureB y: "+String.valueOf(contact.getFixtureB().getBody().getPosition().y));

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
		// TODO Auto-generated method stub
		// Log.v("FEBI", "postSolve()");
		/*
		 * Sprite a, b; if ((a = (Sprite)
		 * contact.getFixtureA().getBody().getUserData()) != null) if
		 * (a.getUserData() != null) if(a.getUserData().equals("BOMBA"))
		 * BombsToDelete.add(contact.getFixtureA().getBody()); if ((b = (Sprite)
		 * contact.getFixtureB().getBody().getUserData()) != null) if
		 * (b.getUserData() != null) if(b.getUserData().equals("BOMBA"))
		 * BombsToDelete.add(contact.getFixtureB().getBody());
		 */
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
