package com.rydzwr.tictactoe.game.algorithm;

import com.rydzwr.tictactoe.database.model.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckWinAlgorithm {

    public boolean checkWin(Game game) {
        int gameSize = game.getGameSize();
        int gameDifficulty = game.getDifficulty();
        String gameBoard = game.getGameBoard();

        if (!gameBoard.contains("-")) {
            return true;
        }

        boolean horizontal = checkHorizontalWin(gameBoard, gameSize, gameDifficulty);
        boolean vertical = checkHVerticalWin(gameBoard, gameSize, gameDifficulty);
        boolean diagonalWin = checkDiagonalWin(gameBoard, gameSize, gameDifficulty);

        return horizontal || vertical || diagonalWin;
    }

    private boolean checkHorizontalWin(String gameBoard, int gameSize, int gameDifficulty) {
        // CHECK HORIZONTAL WIN
        for (int row = 0; row < gameSize; row++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, row, 0);
            int counter = 1;
            for (int column = 0; column < gameSize; column++) {
                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if ((pawn == candidate) && (pawn != '-')) {
                    counter++;
                } else {
                    counter = 1;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkHVerticalWin(String gameBoard, int gameSize, int gameDifficulty) {
        // CHECK VERTICAL WIN
        for (int column = 0; column < gameSize; column++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, 0, column);
            int counter = 1;
            for (int row = 0; row < gameSize; row++) {
                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if ((pawn == candidate) && (pawn != '-')) {
                    counter++;
                } else {
                    counter = 1;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDiagonalWin(String gameBoard, int gameSize, int gameDifficulty) {
        // CHECK DIAGONAL WIN (LEFT TO RIGHT)
        for (int row = 0; row < gameSize - gameDifficulty + 1; row++) {
            for (int col = 0; col < gameSize - gameDifficulty + 1; col++) {
                char candidate = getPawnAtCoords(gameBoard, gameSize, row, col);
                int counter = 1;
                for (int i = 1; i < gameDifficulty; i++) {
                    int nextRow = row + i;
                    int nextCol = col + i;
                    char pawn = getPawnAtCoords(gameBoard, gameSize, nextRow, nextCol);
                    if ((pawn == candidate) && (pawn != '-')) {
                        counter++;
                    } else {
                        counter = 1;
                        candidate = pawn;
                    }
                    if (counter == gameDifficulty) {
                        return true;
                    }
                }
            }
        }

        // CHECK DIAGONAL WIN (RIGHT TO LEFT)
        for (int row = 0; row < gameSize - gameDifficulty + 1; row++) {
            for (int col = gameSize - 1; col >= gameDifficulty - 1; col--) {
                char candidate = getPawnAtCoords(gameBoard, gameSize, row, col);
                int counter = 1;
                for (int i = 1; i < gameDifficulty; i++) {
                    int nextRow = row + i;
                    int nextCol = col - i;
                    char pawn = getPawnAtCoords(gameBoard, gameSize, nextRow, nextCol);
                    if ((pawn == candidate) && (pawn != '-')) {
                        counter++;
                    } else {
                        counter = 1;
                        candidate = pawn;
                    }
                    if (counter == gameDifficulty) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private char getPawnAtCoords(String board, int gameSize, int row, int column) {
        int index = row * gameSize + column;
        return board.charAt(index);
    }
}
