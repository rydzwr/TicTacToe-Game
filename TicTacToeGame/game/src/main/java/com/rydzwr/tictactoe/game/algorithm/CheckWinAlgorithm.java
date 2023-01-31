package com.rydzwr.tictactoe.game.algorithm;

import com.rydzwr.tictactoe.database.model.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckWinAlgorithm {

    public boolean checkWin(Game game, int playerMoveIndex) {
        int gameSize = game.getGameSize();
        int gameDifficulty = game.getDifficulty();
        String gameBoard = game.getGameBoard();

        log.info("CALLED CHECK WIN: --> ---------------------------------------------");

        return checkHorizontals(gameBoard, gameSize, gameDifficulty, playerMoveIndex) ||
                checkVerticals(gameBoard, gameSize, gameDifficulty, playerMoveIndex) ||
                checkDiagonals(gameBoard, gameSize, gameDifficulty, playerMoveIndex);
    }

    private boolean checkHorizontals(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        return checkHorizontalLeft(gameBoard, gameSize, gameDifficulty, playerMoveIndex) ||
                checkHorizontalRight(gameBoard, gameSize, gameDifficulty, playerMoveIndex);
    }

    private boolean checkVerticals(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        return checkVerticalUp(gameBoard, gameSize, gameDifficulty, playerMoveIndex) ||
                checkVerticalDown(gameBoard, gameSize, gameDifficulty, playerMoveIndex);
    }

    private boolean checkDiagonals(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        return checkDiagonalLeftDownRightUp(gameBoard, gameSize, gameDifficulty, playerMoveIndex) ||
                checkDiagonalLeftUpRightDown(gameBoard, gameSize, gameDifficulty, playerMoveIndex);
    }

    // HORIZONTALS
    // ------------------------------------------
    private boolean checkHorizontalLeft(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        log.info("IN CHECK HORIZONTALS LEFT");

        int counter = 1;
        int currentIndex = playerMoveIndex;
        char candidate = gameBoard.charAt(currentIndex);
        while (currentIndex % gameSize != 0 && gameBoard.charAt(currentIndex - 1) == candidate) {
            counter++;
            currentIndex--;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkHorizontalRight(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        log.info("IN CHECK HORIZONTALS RIGHT");
        int counter = 1;
        int currentIndex = playerMoveIndex;
        char candidate = gameBoard.charAt(currentIndex);
        while ((currentIndex + 1) % gameSize != 0 && gameBoard.charAt(currentIndex + 1) == candidate) {
            counter++;
            currentIndex++;
        }
        return counter >= gameDifficulty;
    }

    // VERTICALS
    // ------------------------------------------
    private boolean checkVerticalUp(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        log.info("IN CHECK VERTICAL UP");
        int counter = 1;
        int currentIndex = playerMoveIndex;
        char candidate = gameBoard.charAt(currentIndex);
        while (currentIndex >= gameSize && gameBoard.charAt(currentIndex - gameSize) == candidate) {
            counter++;
            currentIndex -= gameSize;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkVerticalDown(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        log.info("IN CHECK VERTICAL DOWN");
        int counter = 1;
        int currentIndex = playerMoveIndex;
        char candidate = gameBoard.charAt(currentIndex);
        while (currentIndex < gameBoard.length() - gameSize && gameBoard.charAt(currentIndex + gameSize) == candidate) {
            counter++;
            currentIndex += gameSize;
        }
        return counter >= gameDifficulty;
    }

    // DIAGONALS
    // ------------------------------------------
    private boolean checkDiagonalLeftUpRightDown(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
      return checkDiagonalLeftUpRightDownPartOne(gameBoard, gameSize, gameDifficulty, playerMoveIndex) ||
              checkDiagonalLeftUpRightDownPartTwo(gameBoard, gameSize, gameDifficulty, playerMoveIndex);
    }

    private boolean checkDiagonalLeftUpRightDownPartOne(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        log.info("IN CHECK DIAGONAL LEFT UP RIGHT DOWN PART ONE");
        int counter = 1;
        int currentIndex = playerMoveIndex;
        char candidate = gameBoard.charAt(currentIndex);
        while (currentIndex % gameSize != 0 && currentIndex >= gameSize && gameBoard.charAt(currentIndex - gameSize - 1) == candidate) {
            counter++;
            currentIndex -= gameSize + 1;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkDiagonalLeftUpRightDownPartTwo(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        log.info("IN CHECK DIAGONAL LEFT UP RIGHT DOWN PART TWO");
        int counter = 1;
        int currentIndex = playerMoveIndex;
        char candidate = gameBoard.charAt(currentIndex);
        while ((currentIndex + 1) % gameSize != 0 && currentIndex < gameBoard.length() - gameSize && gameBoard.charAt(currentIndex + gameSize + 1) == candidate) {
            counter++;
            currentIndex += gameSize + 1;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkDiagonalLeftDownRightUp(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
       return checkDiagonalLeftDownRightUpPartOne(gameBoard, gameSize, gameDifficulty, playerMoveIndex) ||
               checkDiagonalLeftDownRightUpPartTwo(gameBoard, gameSize, gameDifficulty, playerMoveIndex);
    }

    private boolean checkDiagonalLeftDownRightUpPartOne(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        log.info("IN CHECK DIAGONAL LEFT DOWN RIGHT UP PART ONE");
        int counter = 1;
        int currentIndex = playerMoveIndex;
        char candidate = gameBoard.charAt(currentIndex);
        while (currentIndex % gameSize != 0 && currentIndex < gameBoard.length() - gameSize && gameBoard.charAt(currentIndex + gameSize - 1) == candidate) {
            counter++;
            currentIndex += gameSize - 1;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkDiagonalLeftDownRightUpPartTwo(String gameBoard, int gameSize, int gameDifficulty, int playerMoveIndex) {
        log.info("IN CHECK DIAGONAL LEFT DOWN RIGHT UP PART TWO");
        int counter = 1;
        int currentIndex = playerMoveIndex;
        char candidate = gameBoard.charAt(currentIndex);
        while ((currentIndex + 1) % gameSize != 0 && currentIndex >= gameSize && gameBoard.charAt(currentIndex - gameSize + 1) == candidate) {
            counter++;
            currentIndex -= gameSize - 1;
        }
        return counter >= gameDifficulty;
    }

}