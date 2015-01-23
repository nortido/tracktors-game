package com.games.mygame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class GameMap {

    GameView gameView;
    ScreenGraphics screenGraphics;
    Bitmap mapBitmap;

    private int[][] map;

    GameMap(GameView gameView) {
        this.gameView = gameView;
        this.screenGraphics = gameView.screenGraphics;

        map = new int[gameView.MAP_ROWS][gameView.MAP_COLUMNS];

        setLevel(gameView.level);
    }

    public void setMap(int row, int column, int currentFrame) {
        map[row][column] = currentFrame;
    }

    /**
     * Проверка на наличие обьектов на карте
     */
    public boolean getCellState(int row, int column) {
        return map[row][column] != 0;
    }

    /**
     * Загрузка нужного уровня
     */
    public void setLevel(int levelNum) {
        String[] level;

        switch (levelNum) {
            case 1:
                level = gameView.getResources().getStringArray(R.array.Level1);
                break;
            case 2:
                level = gameView.getResources().getStringArray(R.array.Level2);
                break;
            case 3:
                level = gameView.getResources().getStringArray(R.array.Level3);
                break;
            case 4:
                level = gameView.getResources().getStringArray(R.array.Level4);
                break;
            case 5:
                level = gameView.getResources().getStringArray(R.array.Level5);
                break;
            default:
                level = gameView.getResources().getStringArray(R.array.Level1);
                break;
        }
        for (int i = 0; i < gameView.MAP_COLUMNS; i++)
            for (int j = 0; j < gameView.MAP_ROWS; j++) {
                map[j][i] = Character.getNumericValue(level[i].charAt(j));
            }
        updateBitmapMap();
    }

    /**
     * Создание картинки из массива данных карты
     */
    private Bitmap getBitmapFromMap() {
        /**
         * Временный канвас, который и будет картинкой
         */
        Bitmap out = Bitmap.createBitmap(gameView.MAP_ROWS * 32,
                gameView.MAP_COLUMNS * 32, Bitmap.Config.ARGB_8888);
        Canvas _canvas = new Canvas(out);

        /**
         * Рисует все ячейки карты в канвас
         */
        for (int i = 0; i < gameView.MAP_ROWS; i++)
            for (int j = 0; j < gameView.MAP_COLUMNS; j++) {
                _canvas.drawBitmap(screenGraphics.Sprite(gameView.mapTexture,
                                map[i][j] * 32, 0, 32, (map[i][j] + 1) * 32, 1),
                        i * 32, j * 32, null);
            }

        return out;
    }

    /**
     * Обновляет картинку карты
     */
    public void updateBitmapMap() {
        mapBitmap = getBitmapFromMap();
    }

    /**
     * Рисует картинку карты, затем остальную графику карты поверх нее
     */
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mapBitmap, 0, 0, null);
        screenGraphics.routeDraw(canvas, map);
    }
}
