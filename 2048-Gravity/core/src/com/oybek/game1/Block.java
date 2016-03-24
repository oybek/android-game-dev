package com.oybek.game1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Block {
	static final float DENSITY = 0.5f;

	static float BlockSideLen = 3f;
	boolean Marked; /* true if block had been pressed */
	int Value;

	Body body;
	World world;
	TexturePack sprites;
	Sprite sprite;
	SpriteBatch batch;
	OrthographicCamera camera;
	Fixture fixture;

	float getSide() {
		return BlockSideLen * (1+Value/7f);
	}

	float getArea() {
		return (float)Math.pow(getSide(), 2);
	}

	int getValue() {
		return Value;
	}

	float getBlockSidelen() {
		return BlockSideLen * (1+Value/7f);
	}

	void reset() {
		Value = 0;
		incValue();
		body.setTransform(camera.viewportWidth * (float)Math.random(), camera.viewportHeight+2f*BlockSideLen, 0f);
	}

	Block(World world, OrthographicCamera camera, TexturePack sprites, SpriteBatch batch) {
		this.world = world;
		this.camera = camera;
		this.Marked = false;
		this.sprites = sprites;
		this.batch = batch;
		Value = 1;

		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(camera.viewportWidth * (float)Math.random(), camera.viewportHeight+2f*BlockSideLen);

		// Create our body in the world using our body definition
		body = world.createBody(bodyDef);

		// Create a circle shape and set its radius to 6
		PolygonShape square = new PolygonShape();
		square.setAsBox(getSide()/2, getSide()/2);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = square;
		fixtureDef.density = DENSITY;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit

		// Create our fixture and attach it to the body
		fixture = body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		square.dispose();
	}

	int incValue() {
		++Value;
		body.destroyFixture(fixture);

		PolygonShape square = new PolygonShape();
		square.setAsBox(getSide()/2, getSide()/2);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = square;
		fixtureDef.density = DENSITY / (float) Math.pow((1+Value/7f), 2);
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit

		// Create our fixture and attach it to the body
		fixture = body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		square.dispose();

		return (int) Math.pow(2, Value);
	}


	Fixture getFixture() {
		return fixture;
	}

	Vector3 metersToPixels(float x, float y) {
		Vector3 tmp = new Vector3(x, y, 0);
		camera.project(tmp);
		return tmp;
	}

	void render() {
		if (!body.isActive())
			return;
		/* get bottom up coord of block in pixels */
		Vector3 bottomUpPix = metersToPixels(getPosition().x-getSide()/2f, getPosition().y-getSide()/2f);
		/* get block sidelen in pixels */
		Vector3 blockSidelenPix = metersToPixels(getSide(), getSide());

		batch.draw(
			sprites.getSprite(Value),
			bottomUpPix.x,
			bottomUpPix.y,
			blockSidelenPix.x/2,
			blockSidelenPix.y/2,
			blockSidelenPix.x,
			blockSidelenPix.y,
			1f,
			1f,
			body.getAngle() * MathUtils.radiansToDegrees
		);
	}

	void Mark() {
		Marked = true;
	}

	void Unmark() {
		Marked = false;
	}

	boolean IsMarked() {
		return Marked;
	}

	Body getBody() {
		return body;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}
}

