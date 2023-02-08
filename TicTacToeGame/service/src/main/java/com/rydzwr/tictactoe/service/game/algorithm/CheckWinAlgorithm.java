package com.rydzwr.tictactoe.service.game.algorithm;

import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckWinAlgorithm {

    public boolean checkWin(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkHorizontals(gameAdapter, moveCoordsDto) ||
                checkVerticals(gameAdapter, moveCoordsDto) ||
                checkDiagonals(gameAdapter, moveCoordsDto);
    }

    private boolean checkHorizontals(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkHorizontalLeft(gameAdapter, moveCoordsDto) ||
                checkHorizontalRight(gameAdapter, moveCoordsDto);
    }

    private boolean checkVerticals(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkVerticalUp(gameAdapter, moveCoordsDto) ||
                checkVerticalDown(gameAdapter, moveCoordsDto);
    }

    private boolean checkDiagonals(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkDiagonalLeftDownRightUp(gameAdapter, moveCoordsDto) ||
                checkDiagonalLeftUpRightDown(gameAdapter, moveCoordsDto);
    }

    // HORIZONTALS
    // ------------------------------------------
    private boolean checkHorizontalLeft(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 0;
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        MoveCoordsDto temp = moveCoordsDto.copy();

        while (gameAdapter.isOutOfBoard(temp) && gameAdapter.hasPawn(temp, candidate) && counter < gameAdapter.getDifficulty()) {
            counter++;
            temp.moveBy(0, 1);
        }

        return counter >= gameAdapter.getDifficulty();
    }

    private boolean checkHorizontalRight(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 0;
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        MoveCoordsDto temp = moveCoordsDto.copy();

        while (gameAdapter.isOutOfBoard(temp) && gameAdapter.hasPawn(temp, candidate) && counter < gameAdapter.getDifficulty()) {
            counter++;
            temp.moveBy(0, -1);
        }

        return counter >= gameAdapter.getDifficulty();
    }

    // VERTICALS
    // ------------------------------------------
    private boolean checkVerticalUp(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 0;
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        MoveCoordsDto temp = moveCoordsDto.copy();

        while (gameAdapter.isOutOfBoard(temp) && gameAdapter.hasPawn(temp, candidate) && counter < gameAdapter.getDifficulty()) {
            counter++;
            temp.moveBy(1, 0);
        }

        return counter >= gameAdapter.getDifficulty();
    }

    private boolean checkVerticalDown(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 0;
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        MoveCoordsDto temp = moveCoordsDto.copy();

        while (gameAdapter.isOutOfBoard(temp) && gameAdapter.hasPawn(temp, candidate) && counter < gameAdapter.getDifficulty()) {
            counter++;
            temp.moveBy(-1, 0);
        }

        return counter >= gameAdapter.getDifficulty();
    }

    // DIAGONALS
    // ------------------------------------------
    private boolean checkDiagonalLeftUpRightDown(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkDiagonalLeftUpRightDownPartOne(gameAdapter, moveCoordsDto) ||
                checkDiagonalLeftUpRightDownPartTwo(gameAdapter, moveCoordsDto);
    }

    private boolean checkDiagonalLeftUpRightDownPartOne(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 0;
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        MoveCoordsDto temp = moveCoordsDto.copy();

        while (gameAdapter.isOutOfBoard(temp) && gameAdapter.hasPawn(temp, candidate) && counter < gameAdapter.getDifficulty()) {
            counter++;
            temp.moveBy(-1, -1);
        }

        return counter >= gameAdapter.getDifficulty();
    }

    private boolean checkDiagonalLeftUpRightDownPartTwo(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 0;
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        MoveCoordsDto temp = moveCoordsDto.copy();

        while (gameAdapter.isOutOfBoard(temp) && gameAdapter.hasPawn(temp, candidate) && counter < gameAdapter.getDifficulty()) {
            counter++;
            temp.moveBy(1, 1);
        }

        return counter >= gameAdapter.getDifficulty();
    }

    private boolean checkDiagonalLeftDownRightUp(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkDiagonalLeftDownRightUpPartOne(gameAdapter, moveCoordsDto) ||
                checkDiagonalLeftDownRightUpPartTwo(gameAdapter, moveCoordsDto);
    }

    private boolean checkDiagonalLeftDownRightUpPartOne(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 0;
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        MoveCoordsDto temp = moveCoordsDto.copy();

        while (gameAdapter.isOutOfBoard(temp) && gameAdapter.hasPawn(temp, candidate) && counter < gameAdapter.getDifficulty()) {
            counter++;
            temp.moveBy(1, -1);
        }

        return counter >= gameAdapter.getDifficulty();
    }

    private boolean checkDiagonalLeftDownRightUpPartTwo(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 0;
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        MoveCoordsDto temp = moveCoordsDto.copy();

        while (gameAdapter.isOutOfBoard(temp) && gameAdapter.hasPawn(temp, candidate) && counter < gameAdapter.getDifficulty()) {
            counter++;
            temp.moveBy(-1, 1);
        }

        return counter >= gameAdapter.getDifficulty();
    }

}