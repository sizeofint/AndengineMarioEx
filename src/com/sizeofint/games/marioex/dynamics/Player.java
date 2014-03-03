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
import com.sizeofint.games.marioex.constants.GameConstants;
import com.sizeofint.games.marioex.manager.ResourcesManager;

public abstract class Player extends AnimatedSprite {
	private Body body;

	private boolean canRun = false;
	private int footContacts = 0;

	protected Action lastdirection;

	private boolean isMoving = false;

	private boolean isJumping = true;

	public Player(float pX, float pY, VertexBufferObjectManager vbo,
			Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
	}

	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, this,
				BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0f, 0f,
						0f, false, GameConstants.CATEGORYBIT_PLAYER,
						GameConstants.MASKBITS_PLAYER, (short) 0));

		body.setUserData("player");
		body.setFixedRotation(true);

		final PhysicsHandler physicsHandler = new PhysicsHandler(this);
		this.registerUpdateHandler(physicsHandler);
		
		
		
		
		
		
		
		
		final PolygonShape mPoly2 = new PolygonShape();
		mPoly2.setAsBox(8f/GameConstants.PIXEL_TO_METER_RATIO_DEFAULT, 15f/GameConstants.PIXEL_TO_METER_RATIO_DEFAULT, new Vector2(0, -.09f), 0);
		final FixtureDef pFixtureDef2 = PhysicsFactory.createFixtureDef(0f, 0f,
				0f, true, GameConstants.CATEGORYBIT_PLAYERBODY,GameConstants.MASKBITS_PLAYERBODY, (short) 0);
		pFixtureDef2.shape = mPoly2;
		Fixture mPlayerbodyf = body.createFixture(pFixtureDef2);
		mPlayerbodyf.setUserData("playerbody");
		mPoly2.dispose();
		
		
		
		
		

		final PolygonShape mPoly = new PolygonShape();
		mPoly.setAsBox(.3f, .1f, new Vector2(0, .5f), 0);
		final FixtureDef pFixtureDef = PhysicsFactory.createFixtureDef(0f, 0f,
				0f, true, GameConstants.CATEGORYBIT_PLAYER,
				GameConstants.MASKBITS_PLAYER, (short) 0);
		pFixtureDef.shape = mPoly;
		Fixture mFeet = body.createFixture(pFixtureDef);
		mFeet.setUserData("playerFeet");

		mPoly.dispose();

		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body,
				true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				// camera.onUpdate(0.1f);

				if (getY() <= 0) {
					onDie();
				}

				if (canRun) {
					body.setLinearVelocity(new Vector2(5, body
							.getLinearVelocity().y));
				}
			}
		});

	}

	public void move(Action to) {

		if (to == Action.MOVELEFT) {

			this.isMoving = true;

			lastdirection = to;

			this.animate(new long[] { 200, 200, 200 }, 7, 9, true);

			body.setLinearVelocity(-1 * GameConstants.PLAYER_VELOCITY, 0);

		} else if (to == Action.MOVERIGHT) {

			this.isMoving = true;

			lastdirection = to;

			this.animate(new long[] { 200, 200, 200 }, 1, 3, true);

			body.setLinearVelocity(1 * GameConstants.PLAYER_VELOCITY, 0);
		} else if (to == Action.STOP) {
			this.isMoving = false;
			body.setLinearVelocity(0, 0);

			if (lastdirection == Action.MOVELEFT) {
				this.stopAnimation(6);
			} else if (lastdirection == Action.MOVERIGHT) {
				this.stopAnimation(0);
			}
		}

	}

	public void jump() {

		if (this.footContacts > 0) {
			ResourcesManager.getInstance().jumpSound.play();
			this.isJumping = true;
			if (lastdirection == Action.MOVELEFT) {
				this.animate(new long[] { 100 }, new int[] { 11 });

			} else if (lastdirection == Action.MOVERIGHT) {

				this.animate(new long[] { 100 }, new int[] { 5 });
			}

			body.setLinearVelocity(body.getLinearVelocity().x, -9);

		}

	}

	public void increaseFootContacts() {
		footContacts++;
		if (footContacts > 0 && this.isJumping) {
			if (this.isMoving) {
				if (lastdirection == Action.MOVELEFT) {
					this.animate(new long[] { 200, 200, 200 }, 7, 9, true);

				} else if (lastdirection == Action.MOVERIGHT) {

					this.animate(new long[] { 200, 200, 200 }, 1, 3, true);
				}

			} else {

				if (lastdirection == Action.MOVELEFT) {
					this.stopAnimation(6);

				} else if (lastdirection == Action.MOVERIGHT) {

					this.stopAnimation(0);
				}
			}
			this.isJumping = false;
		}
	}

	public void decreaseFootContacts() {
		footContacts--;
	}

	@Override
	public void onManagedUpdate(final float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

	}

	public abstract void onDie();
	public abstract void shoot();
}
