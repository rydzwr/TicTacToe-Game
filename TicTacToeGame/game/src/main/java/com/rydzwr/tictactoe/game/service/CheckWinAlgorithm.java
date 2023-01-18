package com.rydzwr.tictactoe.game.service;

import com.rydzwr.tictactoe.database.model.Game;
import org.springframework.stereotype.Component;

@Component
public class CheckWinAlgorithm {

    public boolean checkWin(Game game) {
        int gameSize = game.getGameSize();
        int gameDifficulty = game.getDifficulty();
        String gameBoard = game.getGameBoard();

        // CHECK HORIZONTAL WIN
        for (int row = 0; row < gameSize; row++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, row, 0);
            int counter = 0;
            for (int column = 0; column < gameSize; column++) {
                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if ((pawn == candidate) && (pawn != '-')) {
                    counter++;
                } else {
                    counter = 0;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {

                    return true;
                }
            }
        }

        // CHECK VERTICAL WIN
        for (int column = 0; column < gameSize; column++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, 0, column);
            int counter = 0;
            for (int row = 0; row < gameSize; row++) {
                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if ((pawn == candidate) && (pawn != '-')) {
                    counter++;
                } else {
                    counter = 0;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {
                    return true;
                }
            }
        }

        // CHECK RIGHT-TOP DIAGONALS
      /*  for (int diagonal = 0; diagonal < gameSize; diagonal++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, diagonal, 0);
            int counter = 0;
            for (int i = 0; i < diagonal; i++) {
                int row = diagonal - i;
                int column = i;

                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if (pawn == candidate) {
                    counter++;
                } else {
                    counter = 0;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {

                    return true;
                }
            }
        }

        // CHECK RIGHT-BOTTOM DIAGONALS
        for (int diagonal = 0; diagonal < gameSize; diagonal++) {
            char candidate = getPawnAtCoords(gameBoard, gameSize, gameSize - 1, diagonal);
            int counter = 0;
            for (int i = 0; i < gameSize - diagonal; i++) {
                int row = gameSize - 1 - i;
                int column = i;

                char pawn = getPawnAtCoords(gameBoard, gameSize, row, column);

                if (pawn == candidate) {
                    counter++;
                } else {
                    counter = 0;
                    candidate = pawn;
                }

                if (counter == gameDifficulty) {

                    return true;
                }
            }
        }*/

        return false;
    }

    private char getPawnAtCoords(String board, int gameSize, int row, int column) {
        int index = row * gameSize + column;
        return board.charAt(index);
    }
}
