package com.games.mygame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class ObjectGraphics {

    private GameView gameView;                  //Объект класса GameView
    private Rect objectSRC, objectDST;          //
    public int currentFrame = 0;               //Текущий кадр = 0
    private Bitmap objectSprite;
    private boolean who;                        //true - игрок, false - бот

    /**
     * Конструктор
     */
    public ObjectGraphics(GameView gameView, boolean who) {
        this.gameView = gameView;
        this.who = who;

        objectSRC = new Rect();
        objectDST = new Rect();

        if (who)
            objectSprite = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.traktor);
        else
            objectSprite = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.traktor2);
    }

    /**
     * Спрайт игрока
     */
    public void objectDraw(Canvas canvas, Point point, int direction) {
        currentFrame = ++currentFrame % gameView.BMP_COLUMNS;

        int srcX = currentFrame * gameView.size;
        int srcY = direction * gameView.size;

        objectSRC.set(srcX, srcY, srcX + gameView.size, srcY + gameView.size);
        objectDST.set(point.x, point.y, point.x + gameView.size, point.y + gameView.size);
        canvas.drawBitmap(objectSprite, objectSRC, objectDST, null);
    }

    /**
     * Спрайт взрыва
     */
    public void explosionDraw(Canvas canvas, Point point) {
//        gameView.playerControl.setDirection(-1);
//        gameView.botControl.setDirection(-1);
        if (currentFrame < 6) {
            //Кадры взрыва
            currentFrame = ++currentFrame;

            int srcX = currentFrame * gameView.size;
            int srcY = gameView.size;

            objectSRC.set(srcX, srcY, srcX + gameView.size, srcY + gameView.size);
            objectDST.set(point.x, point.y, point.x + gameView.size, point.y + gameView.size);
            canvas.drawBitmap(gameView.mapTexture, objectSRC, objectDST, null);
        } else {
            gameView.screenGraphics.gameOver(canvas, who);
        }
    }
}
