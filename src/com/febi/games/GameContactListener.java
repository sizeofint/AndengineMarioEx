package com.febi.games;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {
	Game game;

	public GameContactListener(Game game) {
		super();
		this.game = game;
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub

		// Log.v("FEBI", "beginContact()");
		
		
		
		
		this.fireballaction(contact.getFixtureA(),contact.getFixtureB());

		if (contact.getFixtureB().getUserData() != null) {
			// Log.v("FEBI", contact.getFixtureB().getUserData().toString());
			
			
			

			if (contact.getFixtureB().getUserData().toString() == "EnemyLeftSensor") {

				Log.v("FEBI", ((Enemy) contact.getFixtureB().getBody()
						.getUserData()).enemyID
						+ ": EnemyLeftSensor");

				((Enemy) contact.getFixtureB().getBody().getUserData())
						.changeDirection();
			} else if (contact.getFixtureB().getUserData().toString() == "EnemyRightSensor") {
				Log.v("FEBI", ((Enemy) contact.getFixtureB().getBody()
						.getUserData()).enemyID
						+ ": EnemyRightSensor");
				((Enemy) contact.getFixtureB().getBody().getUserData())
						.changeDirection();
			}

			if (contact.getFixtureB().getUserData().toString() == "PlayerFeet")
				game.numFootContacts++;
		}
		if (contact.getFixtureA().getUserData() != null) {
			if (contact.getFixtureA().getUserData().toString() == "EnemyLeftSensor") {
				// contact.getFixtureB().getBody().get
				Log.v("FEBI", ((Enemy) contact.getFixtureA().getBody()
						.getUserData()).enemyID
						+ ": EnemyLeftSensor");
				// contact.getFixtureB().getBody().setLinearVelocity(1
				// * Enemy.ENEMY_VELOCITY, 0);
				((Enemy) contact.getFixtureA().getBody().getUserData())
						.changeDirection();
			} else if (contact.getFixtureA().getUserData().toString() == "EnemyRightSensor") {
				Log.v("FEBI", ((Enemy) contact.getFixtureA().getBody()
						.getUserData()).enemyID
						+ ": EnemyRightSensor");
				((Enemy) contact.getFixtureA().getBody().getUserData())
						.changeDirection();
			}

			// Log.v("FEBI", contact.getFixtureA().getUserData().toString());
			if (contact.getFixtureA().getUserData().toString() == "PlayerFeet")
				game.numFootContacts++;
		}
		if (game.numFootContacts > 0)
			game.jumpingEnd();

	}

	private void fireballaction(Fixture fixtureA, Fixture fixtureB) {
		// TODO Auto-generated method stub
		
		
		if(fixtureA.getBody().getUserData()!=null) {
			
			if(fixtureB.getBody().getUserData()!=null) {
				if(fixtureB.getBody().getUserData().toString()=="player")
					return;
			}
			
			if(fixtureA.getBody().getUserData() instanceof Fireball) {
				//this.game.getmPhysicsWorld().destroyBody(fixtureA.getBody());
				Fireball fb = (Fireball) fixtureA.getBody().getUserData();
				fixtureA.getBody().setUserData(null);
				this.game.fireballContaner.add(fb);
				
			}
		}
		
		if(fixtureB.getBody().getUserData()!=null){
			
			if(fixtureA.getBody().getUserData()!=null) {
				if(fixtureA.getBody().getUserData().toString()=="player")
					return;
			}
			
			if(fixtureB.getBody().getUserData() instanceof Fireball) {
				//this.game.getmPhysicsWorld().destroyBody(fixtureB.getBody());
				Fireball fb = (Fireball) fixtureB.getBody().getUserData();
				fixtureB.getBody().setUserData(null);
				this.game.fireballContaner.add(fb);
				
			}
			
		}
	
		
		
		
	}

	@Override
	public void endContact(Contact contact) {
		// Log.v("FEBI", "endContact()");

		if (contact.getFixtureB().getUserData() != null) {
			// Log.v("FEBI", contact.getFixtureB().getUserData().toString());
			if (contact.getFixtureB().getUserData().toString() == "PlayerFeet")
				game.numFootContacts--;
		}
		if (contact.getFixtureA().getUserData() != null) {
			// Log.v("FEBI", contact.getFixtureA().getUserData().toString());
			if (contact.getFixtureA().getUserData().toString() == "PlayerFeet")
				game.numFootContacts--;
		}
		if (game.numFootContacts < 1)
			game.jumpingStart();

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
