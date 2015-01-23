package com.games.mygame;

import android.media.AudioManager;
import android.media.SoundPool;

public class GameSound {

    private SoundPool sounds;
    private int drive;
    private int explosion;
    private int rotate;
    private int gameOver;

    GameSound(GameView gameView) {
        sounds = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        drive = sounds.load(gameView.getContext(), R.raw.drive, 1);
        explosion = sounds.load(gameView.getContext(), R.raw.explo, 1);
        rotate = sounds.load(gameView.getContext(), R.raw.rotate, 1);
        gameOver = sounds.load(gameView.getContext(), R.raw.gameover, 1);
    }

    public void soundDrive() {
//        sounds.play(drive, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void soundExplo() {
        sounds.play(explosion, 1.0f, 1.0f, 3, 0, 1.0f);
    }

    public void soundRotate() {
        sounds.play(rotate, 1.0f, 1.0f, 2, 0, 0.7f);
    }

    public void soundGameOver() {

        sounds.play(gameOver, 1.0f, 1.0f, 10, 0, 1.0f);
    }

}
