package com.games.mygame;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends Activity {

    CheckBox checkBoxAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkBoxAudio = (CheckBox) findViewById(R.id.checkBoxAudio);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Обработка нажатия кнопок
     */
    public void onClick(View v) {
        switch (v.getId()) {
            //Старт
            case R.id.button_start: {
                setContentView(R.layout.game_layout);
            }
            break;
            //Настройки
            case R.id.button_settings: {
                setContentView(R.layout.settings_layout);
            }
            break;
            //Выход
            case R.id.button_exit: {
                getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                this.finish();
            }
            break;
            //Пауза
            case R.id.button_pause: {
                ((GameView) findViewById(R.id.gameView)).screenGraphics.pause();
            }
            break;
            //Возврат в меню
            case R.id.button_back: {
                setContentView(R.layout.menu_layout);
            }
            break;
            //Следующий уровень
            case R.id.button_next: {
                /**
                 * Изменяет layout активити от имени главного потока
                 */
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(R.layout.game_layout);
                    }
                });

                ((GameView) findViewById(R.id.gameView)).nextLevel();

            }
            //Переиграть
            case R.id.imageView1: {
                /**
                 * Изменяет layout активити от имени главного потока
                 */
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(R.layout.game_layout);
                    }
                });

                ((GameView) findViewById(R.id.gameView)).reloadLevel();
            }
            break;
            //Музыка
            case R.id.checkBoxAudio: {
                if (checkBoxAudio.isChecked())
                    startService(new Intent(this, ServiceAudio.class)); //Фоновая музыка
                else
                    stopService(new Intent(this, ServiceAudio.class)); //Вырубаем музыку
            }
            break;
            default:
                break;
        }
    }

    public void onBackPressed() {
        ((GameView) findViewById(R.id.gameView)).screenGraphics.pause();
        stopService(new Intent(this, ServiceAudio.class)); //Вырубаем музыку
    }
}
