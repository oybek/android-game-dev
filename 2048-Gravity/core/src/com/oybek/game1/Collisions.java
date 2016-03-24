
package com.oybek.game1;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Collisions implements ContactListener {
	World world;
	HashSet<Block> blocks;
	Ground ground;

	public ArrayList<Block> toDestroy = new ArrayList<Block>();
	public ArrayList<Block> toValueup = new ArrayList<Block>();

	public Collisions(World world, HashSet<Block> blocks, Ground ground) {
		this.world = world;
		this.blocks = blocks;
		this.ground = ground;
		toDestroy = new ArrayList<Block>();
		toValueup = new ArrayList<Block>();
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (ground.getFixtureSet().contains(fixtureA) ||
			ground.getFixtureSet().contains(fixtureB))
			return;

		for (Block a : blocks) {
			if (a.getFixture() == fixtureA) {
				for (Block b : blocks) {
					if (b.getFixture() == fixtureB) {
						if (a.getValue() == b.getValue()) {
							toDestroy.add(b);
							toValueup.add(a);
						}
						return;
					}
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
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

