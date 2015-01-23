package com.games.mygame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by PastoriXx on 27.07.14.
 */
public class ServiceAudio extends Service {
    private static final String TAG = "ServiceAudio";
    MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        player = MediaPlayer.create(this, R.raw.sound);
        player.setLooping(true); // зацикливаем
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "ServiceAudio Stopped", Toast.LENGTH_LONG).show();
        player.stop();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "ServiceAudio Started", Toast.LENGTH_LONG).show();
        player.start();
    }
}