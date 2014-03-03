package com.sizeofint.games.marioex.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.sizeofint.games.marioex.base.BaseScene;
import com.sizeofint.games.marioex.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene {

	private Sprite splash;

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}

	@Override
	public void createScene() {
		splash = new Sprite(0, 0, resourcesManager.splash_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		//splash.setScale(1.5f);
		splash.setPosition(310, 150);
		attachChild(splash);

	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}
}
