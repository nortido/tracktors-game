package com.games.mygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GameView extends SurfaceView {


    public static final int BMP_ROWS = 4;       // Рядов в спрайте трактора
    public static final int BMP_COLUMNS = 4;    // Колонок в спрайте трактора
    public static final int MAP_ROWS = 21;      // Рядов на карте
    public static final int MAP_COLUMNS = 21;   // Колонок на карте
    public static int score = 0, tempScore = 0; // Очки игрока
    public static int level = 0;                // Номер игрового уровня
    public Bitmap mapTexture;                    // Спрайт карты
    public GameThread gameThread;               // Объект класса GameThread
    public ScreenGraphics screenGraphics;       // Объект класса ScreenGraphics
    public GameMap gameMap;                     // Объект класса GameMap
    public ObjectControl playerControl;         // Объект класса Move (игрок)
    public ObjectControl botControl;            // Объект класса Move (бот)
    public boolean gameover = false;            // Экран проигрыша
    public float pixelRatio = 1.0f,             // Коэффициент масштабирования объектов
            screenScale = 1.0f;                         // Коэффициент масштабирования экрана
    public int size = 32;                       // Размер ячейки на карте
    protected int MIN_DISTANCE = 150;
    protected int MAX_TOUCH_TIME = 500;
    private AIntelligence AI;                   // Мозги бота
    private float x;                            // Переменные свайпа
    private float y;
    private long stateTime;

    /**
     * @param attrSet нужно чтобы наш surfaceview встраивался в layout
     */
    public GameView(final Context context, AttributeSet attrSet) {
        super(context, attrSet);

        mapTexture = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        pixelRatio = (float) getResources().getDisplayMetrics().widthPixels / (MAP_ROWS * size);
        screenScale = pixelRatio;
        /**
         * если разрешение экрана ниже родного разрешения текстур
         */
        if (pixelRatio < 1) pixelRatio = 1;
        /** Графический интерфейс игры*/
        screenGraphics = new ScreenGraphics(this);

        gameMap = new GameMap(this);

        botControl = new ObjectControl(this, false);
        AI = new AIntelligence(botControl);
        playerControl = new ObjectControl(this, true);

        gameThread = new GameThread(this);

        /** загрузка картинки с надписью "Score"*/
        post(new Runnable() {
            @Override
            public void run() {
                ((ImageView) ((MainActivity) getContext()).findViewById(R.id.imageView1))
                        .setImageBitmap(screenGraphics.Sprite(mapTexture, 0, 96, 128, 64, 2));
                ((ImageButton) ((MainActivity) getContext()).findViewById(R.id.button_pause))
                        .setImageBitmap(screenGraphics.Sprite(mapTexture, 256, 96, 128, 288, 2));

            }
        });

        /** Прорисовка объектов*/
        getHolder().addCallback(new SurfaceHolder.Callback() {
            /**
             * Уничтожение области рисования
             */
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameThread.setRunning(false);
                while (retry) {
                    try {
                        gameThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

            /** Создание области рисования */
            public void surfaceCreated(SurfaceHolder holder) {
                gameThread.setRunning(true);
                gameThread.start();
            }

            /** Изменение области рисования */
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {

            }
        });
    }


    /**
     * Функция рисующая все
     */
    protected void onDraw(Canvas canvas) {
        canvas.scale(screenScale, screenScale);     // Масштабирование

        /** Отрисовка очков игрока на экране только при их изменении*/
        if (tempScore != score) {
            screenGraphics.scoreUpdate();   // Очки игрока
            tempScore = score;
        }
        gameMap.onDraw(canvas);         // Карта
        botControl.onDraw(canvas);      // Бот
        playerControl.onDraw(canvas);   // Игрок

    }

    /**
     * Вызов AI бота
     */
    protected void callAI() {
        if (!gameover)
            botControl.setDirection(AI.setDirection());
    }

    /**
     * Перезагрузка уровня
     */
    protected void reloadLevel() {

        botControl.startingPosition(false);
        playerControl.startingPosition(true);

        gameover = false;

        gameThread.setRunning(true);

        /**
         * Выполняет загрузку уровня от имени главного потока
         */
        this.post(new Runnable() {

            @Override
            public void run() {
                gameMap.setLevel(level);

            }
        });
    }

    /**
     * Загрузка следующего уровня
     */
    protected void nextLevel() {
        level++;
        gameMap.setLevel(level);
        reloadLevel();
    }

    /**
     * Обработка жестов
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                /**Обновление позиции касания */
                if (System.currentTimeMillis() - stateTime > MAX_TOUCH_TIME) {
                    x = event.getX();
                    y = event.getY();
                    stateTime = System.currentTimeMillis();
                }
                /** Установка аправления движения игрока через свайп */
                if (event.getX() - x > MIN_DISTANCE) playerControl.setDirection(1);
                else if (x - event.getX() > MIN_DISTANCE) playerControl.setDirection(3);
                else if (event.getY() - y > MIN_DISTANCE) playerControl.setDirection(2);
                else if (y - event.getY() > MIN_DISTANCE) playerControl.setDirection(0);

                break;
            }
        }

        return true;
    }
}

