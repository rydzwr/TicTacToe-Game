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

        var game = new Game();
        game.setGameBoard("XXX------");
        game.setGameSize(3);
        game.setDifficulty(3);

        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(2);

        boolean result = algorithm.checkWin(gameAdapter, moveCoordsDto);

        Assertions.assertTrue(result);
    }

    @Test
    public void checkWin_returnsTrue_VerticalWins() {
        var algorithm = new CheckWinAlgorithm();
        var game = new Game();
        game.setGameBoard("X--X--X--");
        game.setGameSize(3);
        game.setDifficulty(3);

        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(2);
        moveCoordsDto.setY(0);

        boolean result = algorithm.checkWin(gameAdapter, moveCoordsDto);

        Assertions.assertTrue(result);
    }

    @Test
    public void checkWin_returnsTrue_FirstDiagonalWins() {
        var algorithm = new CheckWinAlgorithm();

        var game = new Game();
        game.setGameBoard("X---X---X");
        game.setGameSize(3);
        game.setDifficulty(3);

        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(2);
        moveCoordsDto.setY(2);

        boolean result = algorithm.checkWin(gameAdapter, moveCoordsDto);

        Assertions.assertTrue(result);
    }

    @Test
    public void checkWin_returnsTrue_SecondDiagonalWins() {
        var algorithm = new CheckWinAlgorithm();

        var game = new Game();
        game.setGameBoard("--X--X--X");
        game.setGameSize(3);
        game.setDifficulty(3);

        var gameAdapter = new GameAdapter(game);
        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        boolean result = algorithm.checkWin(gameAdapter, moveCoordsDto);

        Assertions.assertTrue(result);
    }
}
