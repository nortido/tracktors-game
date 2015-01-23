package com.games.mygame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.ImageView;

public class ScreenGraphics {

    private GameView gameView;                  //Объект класса GameView
    //    private Rect mapSRC, mapDST;                //
    private int size;
    private int alpha = 0;                      //Интенсивность цвета

    /**
     * Конструктор
     */
    public ScreenGraphics(GameView gameView) {
        this.gameView = gameView;
        this.size = gameView.size;

//        mapSRC = new Rect();
//        mapDST = new Rect();
    }

    /**
     * Рисование следа
     */
    public void routeDraw(Canvas canvas, int[][] map) {
        for (int i = 0; i < gameView.MAP_ROWS; i++)
            for (int j = 0; j < gameView.MAP_COLUMNS; j++) {
                if (map[i][j] > 0 && map[i][j] < 7) {
//
//                    int srcX = map[i][j] * gameView.size;
//                    int srcY = 0;
//
//                    mapSRC.set(srcX, srcY, srcX + gameView.size, srcY + gameView.size);
//                    mapDST.set(i * gameView.size, j * gameView.size, i * gameView.size + gameView.size, j * gameView.size + gameView.size);
//                    canvas.drawBitmap(gameView.mapTexture, mapSRC, mapDST, null);

                    canvas.drawBitmap(
                            Sprite(gameView.mapTexture, map[i][j] * gameView.size, 0,
                                    gameView.size, (map[i][j] + 1) * gameView.size, 1),
                            i * gameView.size, j * gameView.size, null);
                }
            }
    }

    /**

     */

    /**
     * Настоящий СПРАЙТ
     * вырезает нужную область из теустуры и выдает ее в виде Bitmap
     *
     * @param source исходная картинка
     * @param left   левая граница вырезаемого участка на картинке
     * @param top    верхняя
     * @param bottom нижняя
     * @param right  правая
     * @param scale  масштабирование относительно исходного размера (1 для исходного)
     * @return вырезанный участок в виде картинки
     */
    public Bitmap Sprite(Bitmap source, int left, int top, int bottom, int right, float scale) {
        if (source == null) {
            return null;
        }

        final int outputWidth = (int) ((right - left) * scale * gameView.pixelRatio);
        final int outputHeight = (int) ((bottom - top) * scale * gameView.pixelRatio);

        final Bitmap output = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);

        final Rect dest = new Rect(0, 0, outputWidth, outputHeight);
        final Rect src = new Rect(left, top, right, bottom);

        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(source, src, dest, null);

        return output;
    }

    /**
     * Перерисовка очков игрока
     * очень муторный скрипт, хоть и короткий
     */
    public void scoreUpdate() {
        /**
         * Создание пустой картинки
         */
        int digits = 5;
        int outWidth = (int) (digits * size * gameView.pixelRatio);
        int outHeight = (int) (size * 2 * gameView.pixelRatio);
        final Bitmap out = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas _canvas = new Canvas(out);
        /**
         * Получает очки число очков
         */
        int num = gameView.score;
        /**
         * Получает каждой цифры числа очков
         */
        for (int i = 1; i <= digits; i++) {
            /**
             * получает позицию цифры в числе очков, нумерация справа налево
             * предполагается что окно под каждую цифру квадратное
             */
            int posX = outWidth - i * size;
            /**
             * рисует крайнюю правую цифру числа спрайтом
             */
            _canvas.drawBitmap(Sprite(gameView.mapTexture,
                    num % 10 * (size / 2), 128, 160, num % 10 * (size / 2) + (size / 2), 2), posX, 0, null);
            /**
             * убирает из числа нарисованую цифру
             */
            num = num / 10;
        }
        /**
         * Выводит картину с числом очков на imageView от имени главного потока
         */
        gameView.post(new Runnable() {
            @Override
            public void run() {
                ((ImageView) ((MainActivity) gameView.getContext())
                        .findViewById(R.id.imageView2)).setImageBitmap(out);

            }
        });
    }


    public void gameOver(Canvas canvas, final boolean who) {
        /** Вывод надписи проигрыша */
        if (alpha <= 255) {
            canvas.drawARGB(alpha, 0, 0, 0);
            alpha += 15;
        } else {
            canvas.drawARGB(255, 0, 0, 0);
            gameView.gameThread.setRunning(false);

            final Bitmap outTextBmp, backButtonBmp, nextButtonBmp, reloadButtonBmp;
            /** Проиграл игрок */
            if (who) {
                outTextBmp = Sprite(gameView.mapTexture, 0, 64, 96, 128, 4);

            }
            /** Проиграл ИИ */
            else {
                outTextBmp = Sprite(gameView.mapTexture, 128, 64, 96, 256, 4);
            }


            /** Кнопки */
            backButtonBmp = Sprite(gameView.mapTexture, 256, 64, 96, 288, 4);

            if (who)
                nextButtonBmp = Sprite(gameView.mapTexture, 288, 96, 128, 320, 4);
            else
                nextButtonBmp = Sprite(gameView.mapTexture, 288, 64, 96, 320, 4);

            reloadButtonBmp = Sprite(gameView.mapTexture, 288, 32, 64, 320, 4);

            /**
             * Вставляет полученные выше картинки кнопок в объекты от имени главного потока
             */
            gameView.post(new Runnable() {
                @Override
                public void run() {


                    ((MainActivity) gameView.getContext()).setContentView(R.layout.gameover_layout);
                    ((ImageView) ((MainActivity) gameView.getContext())
                            .findViewById(R.id.imageView)).setImageBitmap(outTextBmp);

                    ((ImageView) ((MainActivity) gameView.getContext())
                            .findViewById(R.id.button_back)).setImageBitmap(backButtonBmp);

                    ((ImageView) ((MainActivity) gameView.getContext())
                            .findViewById(R.id.button_next)).setImageBitmap(nextButtonBmp);
                    /**
                     * Делает кнопку далее не кликабельной, если выигрыша не было
                     */
                    if (who) (((MainActivity) gameView.getContext())
                            .findViewById(R.id.button_next)).setClickable(false);

                    ((ImageView) ((MainActivity) gameView.getContext())
                            .findViewById(R.id.imageView1)).setImageBitmap(reloadButtonBmp);

                }
            });
        }
    }

    public void pause() {
        gameView.gameThread.setRunning(false);
        final Bitmap outTextBmp, backButtonBmp, nextButtonBmp, reloadButtonBmp;
        /** Проиграл игрок */
        outTextBmp = Sprite(gameView.mapTexture, 128, 96, 128, 256, 4);


        /** Кнопки */
        backButtonBmp = Sprite(gameView.mapTexture, 256, 64, 96, 288, 4);
        nextButtonBmp = Sprite(gameView.mapTexture, 288, 96, 128, 320, 4);

        reloadButtonBmp = Sprite(gameView.mapTexture, 288, 32, 64, 320, 4);

        /**
         * Вставляет полученные выше картинки кнопок в объекты от имени главного потока
         */
        gameView.post(new Runnable() {
            @Override
            public void run() {


                ((MainActivity) gameView.getContext()).setContentView(R.layout.gameover_layout);
                ((ImageView) ((MainActivity) gameView.getContext())
                        .findViewById(R.id.imageView)).setImageBitmap(outTextBmp);

                ((ImageView) ((MainActivity) gameView.getContext())
                        .findViewById(R.id.button_back)).setImageBitmap(backButtonBmp);

                ((ImageView) ((MainActivity) gameView.getContext())
                        .findViewById(R.id.button_next)).setImageBitmap(nextButtonBmp);
                (((MainActivity) gameView.getContext())
                        .findViewById(R.id.button_next)).setClickable(false);

                ((ImageView) ((MainActivity) gameView.getContext())
                        .findViewById(R.id.imageView1)).setImageBitmap(reloadButtonBmp);

            }
        });

    }
}