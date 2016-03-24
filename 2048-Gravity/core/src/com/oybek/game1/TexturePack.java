
package com.oybek.game1;

import java.lang.Math;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TexturePack {
	Texture
		blockSpriteMap,
		popupSpriteMap;

	public TextureRegion
		playButton,
		pauseButton,
		popupBar,
		musicButton,
		musicDisabledButton,
		sfButton,
		sfDisabledButton;

	TextureRegion[] block;

	TexturePack() {
		/* loading spritemaps */
		this.blockSpriteMap = new Texture(Gdx.files.internal("spritemap.png"));
		this.popupSpriteMap = new Texture(Gdx.files.internal("popupmenu.png"));

		/* Getting each region of block */
		block = new TextureRegion[17];
		block[0] = new TextureRegion(blockSpriteMap, 0, 0, 0, 0);
		int offset = 0;
		for (int i = 1; i < block.length; ++i) {
			block[block.length-i] = new TextureRegion(blockSpriteMap, offset, 0, 399-20*(i-1), 399-20*(i-1));
			offset += 400-20*(i-1);
		}

		popupBar = new TextureRegion(popupSpriteMap, 0, 0, 299, 199);
		playButton = new TextureRegion(popupSpriteMap, 300, 0, 64, 64);
		pauseButton = new TextureRegion(popupSpriteMap, 300, 128, 64, 64);

		musicButton = new TextureRegion(popupSpriteMap, 364, 0, 64, 64);
		musicDisabledButton = new TextureRegion(popupSpriteMap, 300, 64, 64, 64);

		sfButton = new TextureRegion(popupSpriteMap, 364, 64, 64, 64);
		sfDisabledButton = new TextureRegion(popupSpriteMap, 364, 128, 64, 64);
	}

	public TextureRegion getSprite(int i) {
		return block[i];
	}
}

