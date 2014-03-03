package com.sizeofint.games.marioex.dynamics;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sizeofint.games.marioex.constants.Action;
import com.sizeofint.games.marioex.constants.GameConstants;
import com.sizeofint.games.marioex.manager.ResourcesManager;

public abstract class Bullet extends AnimatedSprite {

	private Body body;

	public Bullet(float pX, float pY, VertexBufferObjectManager vbo,
			Camera camera, PhysicsWorld physicsWorld, Action to) {
		super(pX, pY, ResourcesManager.getInstance().bullet_region, vbo);

		setBody(PhysicsFactory.createBoxBody(physicsWorld, this,
				BodyType.DynamicBody, PhysicsFactory.createFixtureDef(.1f, .0f,
						.7f, false, GameConstants.CATEGORYBIT_PLAYERBULLET,
						GameConstants.MASKBITS_PLAYERBULLET, (short) 0)));
		getBody().setBullet(true);
		getBody().setFixedRotation(false);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this,
				getBody(), true, false));

		this.animate(100);
		this.getBody()
				.setLinearVelocity((to == Action.MOVERIGHT) ? 25 : -25, 0);
		this.getBody().setUserData(this);
	}

	public abstract void onDestroy();

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
}