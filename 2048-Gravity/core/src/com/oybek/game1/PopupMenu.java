
package com.oybek.game1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.lang.Math;
import java.util.HashSet;

public class PopupMenu implements InputProcessor {
	boolean playButtonPressed = false;
	OrthographicCamera camera;
	SpriteBatch batch;
	TexturePack sprites;
	Joystick joystick;
	BitmapFont font;
	Game1 game;
	public String message;
	GlyphLayout layout;
	GameSound gameSound;
	IAds ad;

	Vector2
		playButtonCenter,
		musicButtonCenter,
		sfButtonCenter;

	Vector2
		touchDownPoint,
		touchDragPoint,
		touchUpPoint;

	PopupMenu(
			OrthographicCamera camera,
			SpriteBatch batch,
			TexturePack sprites,
			BitmapFont font,
			Game1 game,
			IAds ad
			) {
		this.ad = ad;
		this.batch = batch;
		this.camera = camera;
		this.sprites = sprites;
		this.font = font;
		this.playButtonCenter = new Vector2(
			Gdx.graphics.getWidth() * 0.5f,
			Gdx.graphics.getHeight() * 0.875f
		);
		this.musicButtonCenter = new Vector2(
			Gdx.graphics.getWidth() * 0.25f,
			Gdx.graphics.getHeight() * 0.875f
		);
		this.sfButtonCenter = new Vector2(
			Gdx.graphics.getWidth() * 0.75f,
			Gdx.graphics.getHeight() * 0.875f
		);
		this.game = game;
		this.layout = new GlyphLayout();
		this.gameSound = null;
	}

	public void setGameSound(GameSound gameSound) {
		this.gameSound = gameSound;
	}

	public void setJoystick(Joystick joystick) {
		this.joystick = joystick;
	}

    @Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		Gdx.app.log("Play button coord: ", playButtonCenter.x+", "+playButtonCenter.y);
		Gdx.app.log("Touch down at: ", x+", "+(Gdx.graphics.getHeight()-y));

		if (musicButtonCenter.dst(new Vector2(x, Gdx.graphics.getHeight()-y)) < 40f) {
			gameSound.toggleBgMusic();
		}
		if (sfButtonCenter.dst(new Vector2(x, Gdx.graphics.getHeight()-y)) < 40f) {
			gameSound.toggleSf();
		}

		if (playButtonCenter.dst(new Vector2(x, Gdx.graphics.getHeight()-y)) < 40f) {
			Gdx.input.setInputProcessor(joystick);
			if (ad != null) {
				ad.adsClose();
				if (ad.adsClosed())
					ad.showAds();
			}

			if (message.substring(0, Math.min(9, message.length())).equals("GAME OVER")) {
				game.restart();
			}
		}
		return false;
    }

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return true;
    }

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) { return false; }

	@Override
	public boolean keyTyped(char character) { return false; }

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	private void drawButton(TextureRegion sprite, Vector2 center) {
		batch.draw(
			sprite,
			center.x-sprite.getRegionWidth() /2f,
			center.y-sprite.getRegionHeight()/2f
		);
	}

	void render() {
		batch.begin();
		batch.draw(
			sprites.popupBar,
			0,
			0,
			Gdx.graphics.getWidth(),
			Gdx.graphics.getHeight()
		);

		drawButton(sprites.playButton, playButtonCenter);
		if (gameSound != null) {
			drawButton(
				gameSound.isBgMusicEnabled() ?  sprites.musicButton : sprites.musicDisabledButton,
				musicButtonCenter);
		}
		if (gameSound != null) {
			drawButton(
				gameSound.isSfEnabled() ?  sprites.sfButton : sprites.sfDisabledButton,
				sfButtonCenter);
		}

		layout.setText(font, message);
		font.draw(batch, message,
			Gdx.graphics.getWidth()/2 - layout.width/2f,
			Gdx.graphics.getHeight() * 0.75f
		);
		batch.end();
	}

}

