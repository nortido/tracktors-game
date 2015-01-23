package com.games.mygame;

import android.graphics.Canvas;
import android.graphics.Point;

public class ObjectControl {

    public boolean flagCollision;       // Флаг столкновения
    GameView gameView;
    ScreenGraphics screenGraphics;
    ObjectGraphics objectGraphics;
    GameMap gameMap;
    GameSound gameSound;
    Point point;                        // Координаты объекта на карте
    int xSpeed, ySpeed;                 // Скорость по X и Y
    int speed = 2;                      // Скорость
    private int currentDirection;       // Текущее направление
    private int futureDirection;        // Будущеее направление
    private int currentFrame;           // Текущий кадр
    private boolean isHuman;            // true - игрок, false - бот

    public ObjectControl(GameView gameView, boolean isHuman) {
        this.gameView = gameView;
        this.screenGraphics = gameView.screenGraphics;
        this.gameMap = gameView.gameMap;
        this.isHuman = isHuman;

        objectGraphics = new ObjectGraphics(gameView, isHuman);
        gameSound = new GameSound(gameView);

        point = new Point();

        speed = (int) (speed * gameView.pixelRatio);

        startingPosition(isHuman);
    }

    /**
     * Начальная позиция трактора
     */
    public void startingPosition(boolean who) {
        if (who) {
            point.x = (gameView.MAP_ROWS - 2) * gameView.size;
            point.y = (gameView.MAP_COLUMNS - 2) * gameView.size;
            currentDirection = 0;
            futureDirection = 0;
        } else {
            point.x = gameView.size;
            point.y = gameView.size;
            currentDirection = 2;
            futureDirection = 2;
        }

        flagCollision = false;
        objectGraphics.currentFrame = 0;
    }

    /**
     * Номер ячейки по Х и по У
     */
    public Point getObjectCell() {
        Point coords = new Point();
        coords.x = point.x / gameView.size;
        coords.y = point.y / gameView.size;
        return coords;
    }

    /**
     * Перемещение объекта, его направление
     *
     * @param futureDirection Будущее направление (0 - вверх, 1 - вправо, 2 - вниз, 3 - влево)
     */
    public void setDirection(int futureDirection) {
        if (correctDirection(futureDirection))
            this.futureDirection = futureDirection;
        else
            this.futureDirection = currentDirection;
    }

    /**
     * Изменение напрвления движения
     *
     * @param xSpeed Скорость перемещения по X
     * @param ySpeed Скорость перемещения по Y
     */
    private void setMotion(int xSpeed, int ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    private void updatePoint() {

        if (point.x % gameView.size == 0 && point.y % gameView.size == 0) {
            if (futureDirection != currentDirection) {
                setRouteFrame(currentDirection, futureDirection);
                if (isHuman) gameView.score++;
                currentDirection = futureDirection;
                gameSound.soundRotate();
            } else {
                setRouteFrame(currentDirection, currentDirection);
                if (isHuman) gameView.score++;
                if (isHuman) gameSound.soundDrive();
            }
        }

        switch (currentDirection) {
            case 0: {// Вверх
                setMotion(0, -speed);
                break;
            }
            case 1: {// Вправо
                setMotion(speed, 0);
                break;
            }
            case 2: {// Вниз
                setMotion(0, speed);
                break;
            }
            case 3: {// Влево
                setMotion(-speed, 0);
                break;
            }
            case -1: {// Стоп
                setMotion(0, 0);
                break;
            }
            default: {
                setMotion(0, speed);
                break;
            }
        }

        if (!flagCollision) {
            point.x += xSpeed;
            point.y += ySpeed;
        }
    }


    /**
     * Запрет езды в обратном направлении
     * <p/>
     * true - можно ехать
     * false - запрет
     */
    private boolean correctDirection(int futureDirection) {

        return !((currentDirection == 0 && futureDirection == 2) || (currentDirection == 1 && futureDirection == 3) ||
                (currentDirection == 2 && futureDirection == 0) || (currentDirection == 3 && futureDirection == 1));
    }

    /**
     * Пройденный путь
     */
    private void setRouteFrame(int currentDirection, int futureDirection) {
        if (!isCollision()) {
            if ((currentDirection == 1 && futureDirection == 1) || (currentDirection == 3 && futureDirection == 3) ||
                    (currentDirection == 1 && futureDirection == 3) || (currentDirection == 3 && futureDirection == 1)) {//Влево, вправо
                currentFrame = 1;
            }
            if ((currentDirection == 0 && futureDirection == 0) || (currentDirection == 2 && futureDirection == 2) ||
                    (currentDirection == 0 && futureDirection == 2) || (currentDirection == 2 && futureDirection == 0)) {//Вверх, вниз
                currentFrame = 2;
            }
            if ((currentDirection == 3 && futureDirection == 0) || (currentDirection == 2 && futureDirection == 1)) {// Угол └
                currentFrame = 3;
                gameSound.soundRotate();
            }
            if ((currentDirection == 1 && futureDirection == 0) || (currentDirection == 2 && futureDirection == 3)) {// Угол ┘
                currentFrame = 4;
                gameSound.soundRotate();
            }
            if ((currentDirection == 0 && futureDirection == 1) || (currentDirection == 3 && futureDirection == 2)) {// Угол┌
                currentFrame = 5;
                gameSound.soundRotate();
            }
            if ((currentDirection == 1 && futureDirection == 2) || (currentDirection == 0 && futureDirection == 3)) {// Угол ┐
                currentFrame = 6;
                gameSound.soundRotate();
            }

            gameMap.setMap(point.x / gameView.size, point.y / gameView.size, currentFrame);
        }
    }

    /**
     * Произошло столкновение?
     */
    private boolean isCollision() {
        if (gameMap.getCellState(point.x / gameView.size, point.y / gameView.size)) {
            gameSound.soundGameOver();
            flagCollision = true;       // Столкновение было
            gameView.gameover = true;

            if (!isHuman) gameView.score += 20; //Очки за убийство бота
        } else if (gameView.botControl.getObjectCell().equals(gameView.playerControl.getObjectCell())) {
            gameSound.soundExplo();
            flagCollision = true;       // Столкновение было
            gameView.gameover = true;
            if (!isHuman) gameView.score += 20; //Очки за убийство бота
        } else {
            flagCollision = false;      // Столкновения не было
            gameView.gameover = false;
        }
        return flagCollision;
    }

    /**
     * Рисуем спрайты
     */
    public void onDraw(Canvas canvas) {
        if (flagCollision) {
            objectGraphics.explosionDraw(canvas, point);
        } else {
            objectGraphics.objectDraw(canvas, point, currentDirection);
            updatePoint();
        }
    }
}
