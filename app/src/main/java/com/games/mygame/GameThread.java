package com.games.mygame;

import android.graphics.Canvas;

public class GameThread extends Thread {
    /**
     * Кадры в секунду
     */
    static final long FPS = 30; // НЕ ТРОГАТЬ!

    /**
     * Объект класса GameView
     */
    private GameView gameView;

    /**
     * Задаем состояние потока
     */
    private boolean running = false;

    /**
     * Конструктор класса
     */
    public GameThread(GameView gameView) {
        this.gameView = gameView;
        setDaemon(true);
        setName("MAIN GAME TrEAD");
        setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Задание состояния потока
     */
    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas canvas = null;
            startTime = System.currentTimeMillis();
            try {
                canvas = gameView.getHolder().lockCanvas();
                synchronized (gameView.getHolder()) {
                    gameView.onDraw(canvas);
                    gameView.callAI();
                }
            } finally {
                if (canvas != null) {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {
            }
        }
    }
}
