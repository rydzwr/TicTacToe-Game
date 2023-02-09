package com.rydzwr.tictactoe.service.game.algorithm;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MinimaxAlgorithmTest {
    @Test
    public void processMove_returnsValidMove_WhenBoardHasEmptySpaces() {
        // GIVEN
        var algorithm = new MinimaxAlgorithm();

        var game = new Game();
        game.setGameBoard("--XO-----");
        game.setGameSize(3);
        game.setDifficulty(3);

        var gameAdapter = new GameAdapter(game);

        // WHEN
        int moveIndex = algorithm.processMove(gameAdapter, 'X');

        // THEN
        Assertions.assertTrue(moveIndex >= 0);
        Assertions.assertTrue(moveIndex < game.getGameSize() * game.getGameSize());
        Assertions.assertEquals("-", Character.toString(game.getGameBoard().charAt(moveIndex)));
    }
}
