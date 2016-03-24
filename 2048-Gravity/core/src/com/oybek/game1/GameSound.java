package com.oybek.game1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

class GameSound {
	private boolean
		bgMusicEnabled,
		sfEnabled;

	private Sound
		fMusic;
	private Music
		bgMusic;

	GameSound() {
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("background_sound.ogg"));
		fMusic = Gdx.audio.newSound(Gdx.files.internal("union_sound.mp3"));
		bgMusic.setLooping(true);
		bgMusic.play();

		bgMusicEnabled = true;
		sfEnabled = true;
	}

	boolean isBgMusicEnabled() {
		return bgMusicEnabled;
	}
	boolean isSfEnabled() {
		return sfEnabled;
	}

	void toggleBgMusic() {
		if (bgMusicEnabled) {
			bgMusic.pause();
		} else {
			bgMusic.play();
		}
		bgMusicEnabled = !bgMusicEnabled;
	}

	void playSf() {
		if (sfEnabled)
			fMusic.play();
	}
	void toggleSf() {
		sfEnabled = !sfEnabled;
	}
};

