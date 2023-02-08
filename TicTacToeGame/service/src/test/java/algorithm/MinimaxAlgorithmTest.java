package algorithm;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.algorithm.MinimaxAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(moveIndex >= 0);
        assertTrue(moveIndex < game.getGameSize() * game.getGameSize());
        assertEquals("-", Character.toString(game.getGameBoard().charAt(moveIndex)));
    }
}
