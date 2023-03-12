package com.rydzwr.tictactoe.service.game.algorithm;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckWinAlgorithmTest {

    @Test
    public void checkWin_returnsTrue_HorizontalWins() {
        var algorithm = new CheckWinAlgorithm();

        final String gameBoard = "XXX------";
        var gameAdapter = createGameAdapter(gameBoard);

        var moveCoordsDto = new MoveCoordsDto(0,2);

        boolean result = algorithm.checkWin(gameAdapter, moveCoordsDto);

        Assertions.assertTrue(result);
    }

    @Test
    public void checkWin_returnsTrue_VerticalWins() {
        var algorithm = new CheckWinAlgorithm();

        final String gameBoard = "X--X--X--";
        var gameAdapter = createGameAdapter(gameBoard);

        var moveCoordsDto = new MoveCoordsDto(2,0);

        boolean result = algorithm.checkWin(gameAdapter, moveCoordsDto);

        Assertions.assertTrue(result);
    }

    @Test
    public void checkWin_returnsTrue_FirstDiagonalWins() {
        var algorithm = new CheckWinAlgorithm();

        final String gameBoard = "X---X---X";
        var gameAdapter = createGameAdapter(gameBoard);

        var moveCoordsDto = new MoveCoordsDto(2,2);

        boolean result = algorithm.checkWin(gameAdapter, moveCoordsDto);

        Assertions.assertTrue(result);
    }

    @Test
    public void checkWin_returnsTrue_SecondDiagonalWins() {
        var algorithm = new CheckWinAlgorithm();

        final String gameBoard = "--X--X--X";
        var gameAdapter = createGameAdapter(gameBoard);

        var moveCoordsDto = new MoveCoordsDto(0,0);

        boolean result = algorithm.checkWin(gameAdapter, moveCoordsDto);

        Assertions.assertTrue(result);
    }

    private GameAdapter createGameAdapter(String gameBoard) {

        var game = new Game();
        game.setGameBoard(gameBoard);
        game.setGameSize(3);
        game.setDifficulty(3);

        return new GameAdapter(game);
    }
}
