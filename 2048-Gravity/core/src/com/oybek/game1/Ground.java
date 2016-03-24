
package com.oybek.game1;

import java.util.HashSet;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Ground {
	World world;
	OrthographicCamera camera;
	float
		cameraWidth,
		cameraHeight;

	HashSet<Fixture> fixtureSet;

	HashSet<Fixture> getFixtureSet() {
		return fixtureSet;
	}

	Ground(World world, OrthographicCamera camera) {
		this.world = world;
		this.camera = camera;

		cameraWidth = camera.viewportWidth;
		cameraHeight = camera.viewportHeight;

		fixtureSet = new HashSet<Fixture>();

		// top barier
		fixtureSet.add(createBarier(
			new Vector2(cameraWidth/2f, cameraHeight*2f),
			new Vector2(cameraWidth/2f, 0)
		));

		// bottom barier
		fixtureSet.add(createBarier(
			new Vector2(cameraWidth/2f, 0),
			new Vector2(cameraWidth/2f, 0)
		));

		// left barier
		fixtureSet.add(createBarier(
			new Vector2(0, cameraHeight),
			new Vector2(0, cameraHeight)
		));

		// right barier
		fixtureSet.add(createBarier(
			new Vector2(cameraWidth, cameraHeight),
			new Vector2(0, cameraHeight)
		));
	}

	Fixture createBarier(Vector2 center, Vector2 size) {
		// Create our body definition
		BodyDef groundBodyDef = new BodyDef();
		// Set its world position
		groundBodyDef.position.set(new Vector2(center.x, center.y));

		// Create a body from the defintion and add it to the world
		Body groundBody = world.createBody(groundBodyDef);

		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		// Set the polygon shape as a box which is twice the size of our view port and 20 high
		// (setAsBox takes half-width and half-height as arguments)
		groundBox.setAsBox(size.x, size.y);
		// Create a fixture from our polygon shape and add it to our ground body
		Fixture fixture = groundBody.createFixture(groundBox, 0f);
		// Clean up after ourselves
		groundBox.dispose();

		return fixture;
	}
}

