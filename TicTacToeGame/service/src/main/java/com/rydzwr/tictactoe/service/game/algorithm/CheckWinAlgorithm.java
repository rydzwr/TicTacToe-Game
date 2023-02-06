package com.rydzwr.tictactoe.service.game.algorithm;

import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckWinAlgorithm {

    public boolean checkWin(GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int gameSize = gameAdapter.getGameSize();
        int gameDifficulty = gameAdapter.getDifficulty();

        return checkHorizontals(gameSize, gameDifficulty, gameAdapter, moveCoordsDto) ||
                checkVerticals(gameSize, gameDifficulty, gameAdapter, moveCoordsDto) ||
                checkDiagonals(gameSize, gameDifficulty, gameAdapter, moveCoordsDto);
    }

    private boolean checkHorizontals(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkHorizontalLeft(gameSize, gameDifficulty, gameAdapter, moveCoordsDto) ||
                checkHorizontalRight(gameSize, gameDifficulty, gameAdapter, moveCoordsDto);
    }

    private boolean checkVerticals(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkVerticalUp(gameSize, gameDifficulty, gameAdapter, moveCoordsDto) ||
                checkVerticalDown(gameSize, gameDifficulty, gameAdapter, moveCoordsDto);
    }

    private boolean checkDiagonals(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkDiagonalLeftDownRightUp(gameSize, gameDifficulty, gameAdapter, moveCoordsDto) ||
                checkDiagonalLeftUpRightDown(gameSize, gameDifficulty, gameAdapter, moveCoordsDto);
    }

    // HORIZONTALS
    // ------------------------------------------
    private boolean checkHorizontalLeft(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 1;
        int currentIndex = moveCoordsDto.getIndex(gameSize);
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        while (currentIndex % gameSize != 0 && gameAdapter.getPawnAtIndex(currentIndex - 1) == candidate) {
            counter++;
            currentIndex--;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkHorizontalRight(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 1;
        int currentIndex = moveCoordsDto.getIndex(gameSize);
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        while ((currentIndex + 1) % gameSize != 0 && gameAdapter.getPawnAtIndex(currentIndex + 1) == candidate) {
            counter++;
            currentIndex++;
        }
        return counter >= gameDifficulty;
    }

    // VERTICALS
    // ------------------------------------------
    private boolean checkVerticalUp(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 1;
        int currentIndex = moveCoordsDto.getIndex(gameSize);
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        while (currentIndex >= gameSize && gameAdapter.getPawnAtIndex(currentIndex - gameSize) == candidate) {
            counter++;
            currentIndex -= gameSize;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkVerticalDown(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 1;
        int currentIndex = moveCoordsDto.getIndex(gameSize);
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        while (currentIndex < gameAdapter.getGameBoardCopy().length() - gameSize && gameAdapter.getPawnAtIndex(currentIndex + gameSize) == candidate) {
            counter++;
            currentIndex += gameSize;
        }
        return counter >= gameDifficulty;
    }

    // DIAGONALS
    // ------------------------------------------
    private boolean checkDiagonalLeftUpRightDown(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkDiagonalLeftUpRightDownPartOne(gameSize, gameDifficulty, gameAdapter, moveCoordsDto) ||
                checkDiagonalLeftUpRightDownPartTwo(gameSize, gameDifficulty, gameAdapter, moveCoordsDto);
    }

    private boolean checkDiagonalLeftUpRightDownPartOne(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 1;
        int currentIndex = moveCoordsDto.getIndex(gameSize);
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        while (currentIndex % gameSize != 0 && currentIndex >= gameSize && gameAdapter.getPawnAtIndex(currentIndex - gameSize - 1) == candidate) {
            counter++;
            currentIndex -= gameSize + 1;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkDiagonalLeftUpRightDownPartTwo(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 1;
        int currentIndex = moveCoordsDto.getIndex(gameSize);
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        while ((currentIndex + 1) % gameSize != 0 && currentIndex < gameAdapter.getGameBoardCopy().length() - gameSize && gameAdapter.getPawnAtIndex(currentIndex + gameSize + 1) == candidate) {
            counter++;
            currentIndex += gameSize + 1;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkDiagonalLeftDownRightUp(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        return checkDiagonalLeftDownRightUpPartOne(gameSize, gameDifficulty, gameAdapter, moveCoordsDto) ||
                checkDiagonalLeftDownRightUpPartTwo(gameSize, gameDifficulty, gameAdapter, moveCoordsDto);
    }

    private boolean checkDiagonalLeftDownRightUpPartOne(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 1;
        int currentIndex = moveCoordsDto.getIndex(gameSize);
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        while (currentIndex % gameSize != 0 && currentIndex < gameAdapter.getGameBoardCopy().length() - gameSize && gameAdapter.getPawnAtIndex(currentIndex + gameSize - 1) == candidate) {
            counter++;
            currentIndex += gameSize - 1;
        }
        return counter >= gameDifficulty;
    }

    private boolean checkDiagonalLeftDownRightUpPartTwo(int gameSize, int gameDifficulty, GameAdapter gameAdapter, MoveCoordsDto moveCoordsDto) {
        int counter = 1;
        int currentIndex = moveCoordsDto.getIndex(gameSize);
        char candidate = gameAdapter.getPawnAtIndex(moveCoordsDto);
        while ((currentIndex + 1) % gameSize != 0 && currentIndex >= gameSize && gameAdapter.getPawnAtIndex(currentIndex - gameSize + 1) == candidate) {
            counter++;
            currentIndex -= gameSize - 1;
        }
        return counter >= gameDifficulty;
    }

}