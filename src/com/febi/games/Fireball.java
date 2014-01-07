package com.febi.games;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.febi.games.Game.PlayerDirection;

public class Fireball extends AnimatedSprite {

	private Body mfireballBody;
	private Game game;

	public Body getMfireballBody() {
		return mfireballBody;
	}

	public Fireball(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, Game game) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.game = game;
		
		mfireballBody = PhysicsFactory.createBoxBody(this.game.getmPhysicsWorld(),
				this, BodyType.DynamicBody,
				PhysicsFactory.createFixtureDef(.5f, .5f, .7f));
		mfireballBody.setFixedRotation(false);
		this.game.getmPhysicsWorld().registerPhysicsConnector(
				new PhysicsConnector(this, mfireballBody, true, false));

		
		this.game.scene.attachChild(this);
		this.animate(100);
		this.mfireballBody.setLinearVelocity(
				(this.game.lastdirection == PlayerDirection.RIGHT) ? 10
						: -10, -1);
		this.mfireballBody.setUserData(this);
	}
	public void destroy(){
		this.game.getmPhysicsWorld().unregisterPhysicsConnector(this.game.getmPhysicsWorld().getPhysicsConnectorManager().findPhysicsConnectorByShape(this));
		this.mfireballBody.setActive(false);
		this.game.getmPhysicsWorld().destroyBody(this.mfireballBody);
		this.game.scene.detachChild(this);
		System.gc();
	}

}
