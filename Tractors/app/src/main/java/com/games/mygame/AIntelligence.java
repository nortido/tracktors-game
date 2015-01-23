package com.games.mygame;

import android.graphics.Point;

public class AIntelligence {

    ObjectControl botControl;

    private int futureDirection;
    private Point prevCell;

    AIntelligence(ObjectControl botControl) {
        this.botControl = botControl;
        prevCell = new Point(this.botControl.getObjectCell());
        botControl.setDirection(setDirection());
    }

    public int setDirection() {
        if (!botControl.flagCollision && !prevCell.equals(botControl.getObjectCell())) {
            prevCell = new Point(botControl.getObjectCell());
            /** Проверка на положение игрока относительно бота
             * положение сравнивается сначала по вертикали, потом по горизонтали*/
            if (Math.abs(botControl.point.y - botControl.gameView.playerControl.point.y) / botControl.gameView.size >
                    Math.abs(botControl.point.x - botControl.gameView.playerControl.point.x) / botControl.gameView.size) {
                /** Если игрок ниже бота - ехать вниз, иначе - вверх*/
                if (botControl.point.y > botControl.gameView.playerControl.point.y) {
                    futureDirection = 0;
                } else {
                    futureDirection = 2;
                }
            } else {
                /** Если игрок левее бота - ехать влево, иначе - вправо*/
                if (botControl.point.x > botControl.gameView.playerControl.point.x) {
                    futureDirection = 3;
                } else {
                    futureDirection = 1;
                }
            }

            Point mapCell = null;

            /** Перепроверка выбранного направления 4 раза (4 возможных направления)*/
            for (int i = 0; i < 4; i++) {

                /** Приведение направления к виду от -1 до 1
                 * по У отрицание потому что у нас инвертирована ось У*/
                int radY = (int) -Math.sin(Math.PI / 2 * ((5 - futureDirection) % 4));
                int radX = (int) Math.cos(Math.PI / 2 * ((5 - futureDirection) % 4));
                /** Определяем вертикальное или горизонтальное направление*/
                if (radY != 0) {
                    mapCell = new Point(botControl.getObjectCell().x, radY + botControl.getObjectCell().y);
                } else if (radX != 0) {
                    mapCell = new Point(radX + botControl.getObjectCell().x, botControl.getObjectCell().y);
                }
                /** Не проверять крайние ячейки экрана*/
                if (mapCell.x < botControl.gameView.MAP_ROWS && mapCell.y < botControl.gameView.MAP_COLUMNS
                        && mapCell.x >= 0 && mapCell.y >= 0)
                /** Если следующая ячейка в выбранном направлении > 0 то сменить направление*/
                    if (botControl.gameView.gameMap.getCellState(mapCell.x, mapCell.y)) {
                        futureDirection = ++futureDirection % 4;
                    }
            }
        }
        return futureDirection;
    }

}
