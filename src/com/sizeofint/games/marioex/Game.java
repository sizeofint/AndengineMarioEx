package com.sizeofint.games.marioex;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.view.KeyEvent;

import com.sizeofint.games.marioex.constants.GameConstants;
import com.sizeofint.games.marioex.manager.ResourcesManager;
import com.sizeofint.games.marioex.manager.SceneManager;

public class Game extends SimpleBaseGameActivity {
	

	private BoundCamera mBoundChaseCamera;

	@Override
	public EngineOptions onCreateEngineOptions() {

		this.mBoundChaseCamera = new BoundCamera(0, 0,
				GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
				this.mBoundChaseCamera);
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

		ResourcesManager.prepareManager(mEngine, this, mBoundChaseCamera,
				getVertexBufferObjectManager());

	}

	@Override
	public Scene onCreateScene() {
		
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
		{
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.getInstance().createMenuScene();
            }
		}));
		 
		return SceneManager.getInstance().createSplashScene();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	    	SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}

	

}
