package com.oybek.game1;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Game1 implements ApplicationListener {
	BitmapFont font;
	Box2DDebugRenderer debugRenderer;
	Collisions collisions;
	Ground ground;
	HashSet<Block> blocks;
	Joystick joystick;
	OrthographicCamera camera;
	PopupMenu popupMenu;
	Queue<Block> queueOfInactives;
	ShapeRenderer shapeDrawer;
	Sprite backgroundSprite;
	SpriteBatch batch;
	Texture background;
	TexturePack sprites;
	World world;
	IAds ad;
	GameSound gameSound;

	boolean flag;
	int score, bestScore;
	float areaBusy;

	public Game1(IAds ad) {
		this.ad = ad;
	}

	@Override
	public void create() {
		/* Initalize box2d world. Need to Call */
		Box2D.init();

		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);

		shapeDrawer = new ShapeRenderer();
		/* first is gravity vectro, no horizontal force, -10 vertical */
		world = new World(new Vector2(0, -10), true);

		/* debug renderer of the world Box2D tool */
		debugRenderer = new Box2DDebugRenderer();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		background = new Texture(Gdx.files.internal("background.jpg"));
		background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		backgroundSprite = new Sprite(background);

		/* camera shows 30 meters height, and 30 * (h/w) meters width */
		camera = new OrthographicCamera(30, 30 * (h / w));

		/* creating sprites */
		sprites = new TexturePack();

		/* camera centered, TODO: what does 3rd argument? */
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();

		blocks = new HashSet<Block>();

		Block tmp = new Block(world, camera, sprites, batch);
		blocks.add(tmp);

		ground = new Ground(world, camera);

		popupMenu = new PopupMenu(camera, batch, sprites, font, this, ad);

		/* creating joystick */
		joystick = new Joystick(camera, blocks, sprites, batch, popupMenu, ad);
		Gdx.input.setInputProcessor(joystick);

		popupMenu.setJoystick(joystick);

		collisions = new Collisions(world, blocks, ground);
		world.setContactListener(collisions);

		queueOfInactives = new LinkedList();

		Gdx.gl20.glLineWidth(5);
		shapeDrawer.setProjectionMatrix(camera.combined);

		/* load font */
		font.setColor(1, 1, 1, 1f);

		score = 0;

		backgroundSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		loadBestScore();

		gameSound = new GameSound();
		popupMenu.setGameSound(gameSound);

		flag = true;
	}

	void saveBestScore() {
		FileHandle fileHandle = Gdx.files.local("bestscore.txt");
		fileHandle.writeString(bestScore+"", false);
	}

	void loadBestScore() {
		FileHandle fileHandle = Gdx.files.local("bestscore.txt");
		if (fileHandle.exists()) {
			bestScore = Integer.parseInt(fileHandle.readString());
		} else {
			bestScore = 0;
			fileHandle.writeString(bestScore+"", false);
		}
	}

	float getBusyArea() {
		float area = 0f;
		for (Block b : blocks) {
			if (b.getBody().isActive())
				area += b.getArea();
		}
		return area;
	}

	private void drawForceLine() {
		Vector2 chosenBlockPoint = joystick.getChosenBlockPoint();
		Vector2 pointUnderFinger = joystick.getPointUnderFinger();
		batch.begin();
			shapeDrawer.begin(ShapeType.Line);
			shapeDrawer.setColor(0, 1, 0, 0.5f);

			shapeDrawer.setColor(0xf2/(float)0xff, 0xce/(float)0xff, 0f, 0.5f);

			if (chosenBlockPoint != null && pointUnderFinger != null) {
				shapeDrawer.line(chosenBlockPoint.x, chosenBlockPoint.y, pointUnderFinger.x, pointUnderFinger.y);
			}
			shapeDrawer.end();
		batch.end();
	}

	int getBusyAreaPercent() {
		return Math.round(getBusyArea()/camera.viewportHeight/camera.viewportWidth*100f);
	}

	private void createNewBlock() {
		Gdx.app.log("Busy area", getBusyAreaPercent()+"%");
		if (queueOfInactives.isEmpty()) {
			Block tmp = new Block(world, camera, sprites, batch);

			blocks.add(tmp);
			Gdx.app.log("block", "new");
		} else {
			Block tmp = queueOfInactives.remove();
			tmp.getBody().setActive(true);
			Gdx.app.log("block", "from queue");
		}
	}

	private void handleBlockCollisions() {
		for (Block b : collisions.toDestroy) {
			b.reset();
			b.getBody().setActive(false);
			queueOfInactives.add(b);
		}
		for (Block b : collisions.toValueup) {
			score += b.incValue();

			if (gameSound != null)
				gameSound.playSf();
		}
		collisions.toDestroy.clear();
		collisions.toValueup.clear();
	}

	private void renderGame() {
		batch.begin();
			for (Block b : blocks) {
				b.render();
			}
		batch.end();

		drawForceLine();

		batch.begin();
			joystick.render();
		batch.end();

		batch.begin();
			font.draw(batch, ""+score, 10, Gdx.graphics.getHeight()-10);
		batch.end();

		/* world simulation step */
		world.step(1/45f, 6, 2);

		handleBlockCollisions();

		if (joystick.getFlag()) {
			createNewBlock();
		}

		if (Gdx.input.getInputProcessor() == joystick && getBusyAreaPercent() > 90) {
			if (score > bestScore) {
				popupMenu.message = "GAME OVER!\nNEW RECORD!\nSCORES:\nYOUR-"+score+"\nBEST-"+bestScore;
				Gdx.input.setInputProcessor(popupMenu);
				bestScore = score;
				saveBestScore();
			} else {
				popupMenu.message = "GAME OVER!\nYOU LOSE!\nSCORES:\nYOUR-"+score+"\nBEST-"+bestScore;
				Gdx.input.setInputProcessor(popupMenu);
			}
		}
	}

	public void restart() {
		score = 0;
		collisions.toDestroy = new ArrayList<Block>(blocks);
		handleBlockCollisions();
	}

	private void renderPopup() {
		popupMenu.render();
	}

	@Override
	public void render() {
		/* draw background */
		Gdx.gl.glClearColor(0x2b/(float)0xff, 0x38/(float)0xff, 0x56/(float)0xff, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		renderGame();
		if (Gdx.input.getInputProcessor() == popupMenu) {
			renderPopup();
		}
	}

	@Override
	public void resize(int width, int height) {}
	@Override
	public void pause() {}
	@Override
	public void resume() {}
	@Override
	public void dispose() {}
}

