package com.rydzwr.tictactoe.game.algorithm;

import com.rydzwr.tictactoe.database.model.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckWinAlgorithm {

    public boolean checkWin(Game game, int playerMoveIdx) {
        int gameSize = game.getGameSize();
        int gameDifficulty = game.getDifficulty();
        String gameBoard = game.getGameBoard();

        return checkHorizontalWin(gameBoard, gameSize, gameDifficulty, playerMoveIdx) ||
                checkHVerticalWin(gameBoard, gameSize, gameDifficulty, playerMoveIdx) ||
                checkDiagonalWin(gameBoard, gameSize, gameDifficulty, playerMoveIdx);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private boolean checkHorizontalWin(String gameBoard, int gameSize, int gameDifficulty, int moveIdx) {
        // CHECK HORIZONTAL WIN
        int moveX = moveIdx / gameSize; // 15/10=1
        int moveY = moveIdx % gameSize; // 15%10=5

        int startX = clamp(moveX - gameDifficulty, 0, gameSize);
        int startY = clamp(moveY - gameDifficulty, 0, gameSize);
        int endX = clamp(moveX + gameDifficulty, 0, gameSize);
        int endY = clamp(moveY + gameDifficulty, 0, gameSize);

        for (int row = startY; row < endY; row++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, row, 0);
            int counter = 1;
            for (int column = startX; column < endX; column++) {
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

    private boolean checkHVerticalWin(String gameBoard, int gameSize, int gameDifficulty, int moveIdx) {
        // CHECK VERTICAL WIN
        int moveX = moveIdx / gameSize;
        int moveY = moveIdx % gameSize;

        int startX = clamp(moveX - gameDifficulty, 0, gameSize);
        int startY = clamp(moveY - gameDifficulty, 0, gameSize);
        int endX = clamp(moveX + gameDifficulty, 0, gameSize);
        int endY = clamp(moveY + gameDifficulty, 0, gameSize);

        for (int column = startX; column < endX; column++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, 0, column);
            int counter = 1;
            for (int row = startY; row < endY; row++) {
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

    private boolean checkDiagonalWin(String gameBoard, int gameSize, int gameDifficulty, int moveIdx) {
        int moveX = moveIdx / gameSize;
        int moveY = moveIdx % gameSize;

        int startX = clamp(moveX - gameDifficulty, 0, gameSize);
        int startY = clamp(moveY - gameDifficulty, 0, gameSize);
        int endX = clamp(moveX + gameDifficulty, 0, gameSize);
        int endY = clamp(moveY + gameDifficulty, 0, gameSize);

        // CHECK DIAGONAL WIN (LEFT TO RIGHT)
        for (int row = startY; row < endY - gameDifficulty + 1; row++) {
            for (int col = startX; col < endY - gameDifficulty + 1; col++) {
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
        for (int row = startY; row < endY - gameDifficulty + 1; row++) {
            for (int col = endX - 1; col > startX - 1; col--) {
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
        int index = row * gameSize + column; // 1 * 10 + 5
        return board.charAt(index);
    }
}
