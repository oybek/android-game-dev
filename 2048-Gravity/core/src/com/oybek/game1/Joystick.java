package com.oybek.game1;

import java.util.HashSet;
import java.lang.Math;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.InputProcessor;

public class Joystick implements InputProcessor {
	HashSet<Block> blocks;
	Vector2 pauseButtonCenter;
	OrthographicCamera camera;
	SpriteBatch batch;
	TexturePack sprites;
	PopupMenu popupMenu;
	boolean flag;
	IAds ad;

	Block chosenBlock;

	Vector2
		touchDownPoint,
		touchDragPoint,
		touchUpPoint;

	Joystick(
			OrthographicCamera camera,
			HashSet<Block> blocks,
			TexturePack sprites,
			SpriteBatch batch,
			PopupMenu popupMenu,
			IAds ad
		) {
		this.ad = ad;
		this.blocks = blocks;
		this.camera = camera;
		this.flag = false;
		this.chosenBlock = null;
		this.pauseButtonCenter = new Vector2(Gdx.graphics.getWidth()-32, Gdx.graphics.getHeight()-32);
		this.sprites = sprites;
		this.batch = batch;
		this.popupMenu = popupMenu;
	}

	void render() {
		batch.draw(
			sprites.pauseButton,
			pauseButtonCenter.x-sprites.pauseButton.getRegionHeight()/2,
			pauseButtonCenter.y-sprites.pauseButton.getRegionWidth()/2
		);
	}

    @Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		Gdx.app.log("Pause button coord: ", pauseButtonCenter.x+", "+pauseButtonCenter.y);
		Gdx.app.log("Touch down at: ", x+", "+(Gdx.graphics.getHeight()-y));

		if (pauseButtonCenter.dst(new Vector2(x, Gdx.graphics.getHeight()-y)) < 32f) {
			Gdx.input.setInputProcessor(popupMenu);
			if (ad != null) {
				ad.adsClose();
				if (ad.adsClosed())
					ad.showAds();
			}
			popupMenu.message = "PAUSE";
		}

		flag = false;
		/* converting pixel coordinates to real world coordinates */
		Vector3 tmp = new Vector3(x, y, 0);
		camera.unproject(tmp);
		touchDownPoint = new Vector2(tmp.x, tmp.y);

		//Gdx.app.log("TouchDown at", touchDownPoint.x+", "+touchDownPoint.y);

		for (Block b : blocks) {
			/* TODO: eliminate unnamed constants */
			if (touchDownPoint.dst(b.getPosition()) < b.getBlockSidelen()/2f) {
				chosenBlock = b;
				b.Mark();
				break;
			}
		}
		return false;
    }

	boolean getFlag() {
		if (flag) {
			flag = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		//Gdx.app.log("Blocks number", ""+blocks.size());

		flag = true;
		/* converting pixel coordinates to real world coordinates */
		Vector3 tmp = new Vector3(x, y, 0);
		camera.unproject(tmp);
		touchUpPoint = new Vector2(tmp.x, tmp.y);

		for (Block b : blocks) {
			/* TODO: eliminate unnamed constants */
			if (b.IsMarked()) {
				float dx = touchUpPoint.x - chosenBlock.getPosition().x;
				float dy = touchUpPoint.y - chosenBlock.getPosition().y;
				b.getBody().applyLinearImpulse(
					10*dx, 10*dy, b.getPosition().x, b.getPosition().y, true
				);
				b.Unmark();
			}
			//Gdx.app.log("Block coordinate", b.getPosition().x+", "+b.getPosition().y);
		}
		//Gdx.app.log("TouchUp at", touchUpPoint.x+", "+touchUpPoint.y);
		chosenBlock = null;
		touchUpPoint = null;
		return true;
    }

	public Vector2 getChosenBlockPoint() {
		return chosenBlock != null ? chosenBlock.getPosition() : null;
	}

	public Vector2 getPointUnderFinger() {
		return touchDragPoint;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		Vector3 tmp = new Vector3(x, y, 0);
		camera.unproject(tmp);
		touchDragPoint = new Vector2(tmp.x, tmp.y);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		Gdx.app.log("Hello", "hello");
		return false;
	}

	@Override
	public boolean keyUp(int keycode) { return false; }

	@Override
	public boolean keyTyped(char character) { return false; }

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
}

