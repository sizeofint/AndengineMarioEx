package com.sizeofint.games.marioex.manager;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.sizeofint.games.marioex.Game;

import android.graphics.Color;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class ResourcesManager {
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	private static final ResourcesManager INSTANCE = new ResourcesManager();

	public Engine engine;
	public Game activity;
	public BoundCamera camera;
	public VertexBufferObjectManager vbom;

	public Font font;

	// ---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	// ---------------------------------------------

	public ITextureRegion splash_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion options_region;

	// Game Texture
	public BuildableBitmapTextureAtlas gameTextureAtlas;

	// Game Texture Regions
	public ITextureRegion platform1_region;
	public ITextureRegion platform2_region;
	public ITextureRegion platform3_region;
	public ITextureRegion coin_region;
	public ITiledTextureRegion player_region;

	private BitmapTextureAtlas splashTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;

	// Level Complete Window
	public ITextureRegion complete_window_region;
	public ITiledTextureRegion complete_stars_region;

	public TextureRegion tiledTextureleftarrow;

	public TextureRegion tiledTexturerightarrow;

	public TextureRegion tiledTextureJump;

	public TextureRegion tiledTextureshoot;

	public TiledTextureRegion bullet_region;

	public TiledTextureRegion enemy_region;

	public Music music;

	public Sound jumpSound;

	

	

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}

	public void loadGameResources() {
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(), 2000, 2000,
				TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(menuTextureAtlas, activity, "mariomenubg.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "play.png");

		try {
			this.menuTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadMenuAudio() {

	}

	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(
				activity.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		font = FontFactory.createStrokeFromAsset(activity.getFontManager(),
				mainFontTexture, activity.getAssets(), "font.ttf", 50, true,
				Color.WHITE, 2, Color.BLACK);
		font.load();
	}

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/graphics/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(), 1024, 1024,
				TextureOptions.BILINEAR);
		/* leftarrow button */
	
		this.tiledTextureleftarrow = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas, activity,
						"_leftarrow.png");

		
		/* rightarrow button */
		
		this.tiledTexturerightarrow = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas, activity,
						"_rightarrow.png");

		

		/* jump button */
		
		this.tiledTextureJump = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas, activity,
						"jumpbutton.png");


		/* shoot button */
		
		this.tiledTextureshoot = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas, activity,
						"shootbutton.png");

		
		
		player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "mario2.png", 6, 2);
		
		
		bullet_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "fireball.png", 8, 1);
		
		enemy_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "mario_dog_sprite.png", 3, 1);
		
		
		
		
		try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			this.gameTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadGameFonts() {

	}

	private void loadGameAudio() {
		MusicFactory.setAssetBasePath("audio/");
		SoundFactory.setAssetBasePath("audio/");
		
		try {
			this.music = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), activity, "track1.ogg");
			this.jumpSound = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "smb_jump-super.ogg");
			this.music.setLooping(true);
		} catch (final IOException e) {
			Debug.e(e);
		}


	}

	public void unloadGameTextures() {
		gameTextureAtlas.unload();
	}

	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 180, 198, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				splashTextureAtlas, activity, "graphics/sizeofint-logo.png", 0,
				0);
		splashTextureAtlas.load();
	}

	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}

	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}

	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}

	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br>
	 * <br>
	 *            We use this method at beginning of game loading, to prepare
	 *            Resources Manager properly, setting all needed parameters, so
	 *            we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, Game activity,
			BoundCamera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}

	// ---------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------

	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
}