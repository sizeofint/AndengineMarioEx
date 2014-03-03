package com.sizeofint.games.marioex.dynamics;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sizeofint.games.marioex.constants.Action;
import com.sizeofint.games.marioex.constants.EnemySensors;
import com.sizeofint.games.marioex.constants.GameConstants;
import com.sizeofint.games.marioex.manager.ResourcesManager;

public abstract class Enemy extends AnimatedSprite {
	private Body body;
	private boolean isMoving = false;

	public Enemy(float pX, float pY, VertexBufferObjectManager vbo,
			Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().enemy_region, vbo);
		createPhysics(camera, physicsWorld);
	}

	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		setBody(PhysicsFactory.createBoxBody(physicsWorld, this,
				BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0f, 0f,
						0f, false, GameConstants.CATEGORYBIT_ENEMY,
						GameConstants.MASKBITS_ENEMY, (short) 0)));
		getBody().setFixedRotation(true);

		final PhysicsHandler physicsHandler = new PhysicsHandler(this);
		this.registerUpdateHandler(physicsHandler);

		FixtureDef pFixtureDef = PhysicsFactory.createFixtureDef(0f, 0f, 0f,true, GameConstants.CATEGORYBIT_ENEMYBODY,GameConstants.MASKBITS_ENEMYBODY, (short) 0);

		PolygonShape mPolyleft = new PolygonShape();
		mPolyleft.setAsBox(.1f, .1f, new Vector2(-.3f, -.04f), 0);
		pFixtureDef.shape = mPolyleft;
		Fixture leftSensor = getBody().createFixture(pFixtureDef);
		leftSensor.setUserData(EnemySensors.LEFT);
		mPolyleft.dispose();

		PolygonShape mPolyright = new PolygonShape();
		mPolyright.setAsBox(.1f, .1f, new Vector2(.3f, -.04f), 0);
		pFixtureDef.shape = mPolyright;
		Fixture rightSensor = getBody().createFixture(pFixtureDef);
		rightSensor.setUserData(EnemySensors.RIGHT);
		mPolyright.dispose();

		PolygonShape mPolybottom = new PolygonShape();
		mPolybottom.setAsBox(.1f, .1f, new Vector2(0, .3f), 0);
		pFixtureDef.shape = mPolybottom;
		Fixture bottomSensor = getBody().createFixture(pFixtureDef);
		bottomSensor.setUserData(EnemySensors.BOTTOM);
		mPolybottom.dispose();

		PolygonShape mPolytop = new PolygonShape();
		mPolytop.setAsBox(.1f, .1f, new Vector2(0, -.3f), 0);
		pFixtureDef.shape = mPolytop;
		Fixture topSensor = getBody().createFixture(pFixtureDef);
		topSensor.setUserData(EnemySensors.TOP);
		mPolytop.dispose();
		
		getBody().setUserData(this);

		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body,
				true, false));
		
		if(!this.isMoving)
			this.move(Action.MOVELEFT);
		

	}
	
	public void move(Action to) {
		//if(!getBody().isActive())
		//	return;
		this.isMoving  = true;
		this.animate(new long[] { 200, 200 }, 0, 1, true);
		this.getBody().setLinearVelocity(((to == Action.MOVELEFT) ? -1 : 1)
				* GameConstants.ENEMY_VELOCITY, 0);
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
	
	public abstract void onDie();

}
