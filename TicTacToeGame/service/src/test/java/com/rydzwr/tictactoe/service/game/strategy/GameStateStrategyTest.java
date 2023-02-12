package com.rydzwr.tictactoe.service.game.strategy;

import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.ServiceTestsHelper;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.builder.PlayerBuilder;
import com.rydzwr.tictactoe.service.game.strategy.gameState.ContinueGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.gameState.DrawStateStrategy;
import com.rydzwr.tictactoe.service.game.strategy.gameState.WinGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.selector.GameStateStrategySelector;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@Import(ServiceTestConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class GameStateStrategyTest {
    @Autowired
    private GameStateStrategySelector gameStateStrategySelector;
    @Autowired
    private ContinueGameStrategy continueGameStrategy;
    @Autowired
    private DrawStateStrategy drawStateStrategy;
    @Autowired
    private WinGameStrategy winGameStrategy;
    @Autowired
    private ServiceTestsHelper testsHelper;


    @Test
    @DisplayName("Should Return CONTINUE Game State")
    public void shouldChooseContinueGameStrategy() {
        var strategy = gameStateStrategySelector.chooseStrategy(CheckWinState.CONTINUE);
        assertTrue(strategy instanceof ContinueGameStrategy);
    }

    @Test
    @DisplayName("Should Return WIN Game State")
    public void shouldChooseWinGameStrategy() {
        var strategy = gameStateStrategySelector.chooseStrategy(CheckWinState.WIN);
        assertTrue(strategy instanceof WinGameStrategy);
    }

    @Test
    @DisplayName("Should Return DRAW Game State")
    public void shouldChooseDrawGameStrategy() {
        var strategy = gameStateStrategySelector.chooseStrategy(CheckWinState.DRAW);
        assertTrue(strategy instanceof DrawStateStrategy);
    }

    @Test
    @DisplayName("Should Return Updated Moves Object")
    public void continueGameStrategyTest() {
        // GIVEN
        String GAME_STATE_STRATEGY_TEST_ONE = "gameStateStrategyTestOne";
        var testUser = testsHelper.buildTestUser(GAME_STATE_STRATEGY_TEST_ONE);
        var game = testsHelper.buildGameWithPlayers(GAME_STATE_STRATEGY_TEST_ONE, 5, 5, testUser);

        var gameAdapter = new GameAdapter(game);

        var player = new PlayerBuilder()
                .setPlayerPawn('X')
                .build();

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('O');

        var prev = moves.getCurrentPlayerMove();

        // WHEN
        var updatedMoves = continueGameStrategy.resolve(moves, gameAdapter, player, moveCoordsDto);
        PlayerMoveResponseDto toTest = (PlayerMoveResponseDto) updatedMoves;

        var next = toTest.getCurrentPlayerMove();

        // THEN
        assertNotEquals(prev, next);
    }

    @Test
    @DisplayName("Should Removed Finished Game (DRAW)")
    public void drawStateStrategyTest() {
        // GIVEN
        String GAME_STATE_STRATEGY_TEST_TWO = "gameStateStrategyTestTwo";
        var game = testsHelper.buildEmptyGame(GAME_STATE_STRATEGY_TEST_TWO, 3, 3);
        var gameAdapter = new GameAdapter(game);

        assertNotNull(testsHelper.findGame(GAME_STATE_STRATEGY_TEST_TWO));

        // WHEN
        drawStateStrategy.resolve(null, gameAdapter, null, null);

        // THEN
        assertNull(testsHelper.findGame(GAME_STATE_STRATEGY_TEST_TWO));
    }

    @Test
    @DisplayName("Should Removed Finished Game (WIN), And Return Winner Pawn")
    public void winStateStrategyTest() {
        // GIVEN
        String GAME_STATE_STRATEGY_TEST_THREE = "gameStateStrategyTestThree";
        var game = testsHelper.buildEmptyGame(GAME_STATE_STRATEGY_TEST_THREE, 3, 3);
        var gameAdapter = new GameAdapter(game);

        var player = new Player();
        player.setPawn('X');

        assertNotNull(testsHelper.findGame(GAME_STATE_STRATEGY_TEST_THREE));

        // WHEN
        var winState = winGameStrategy.resolve(null, gameAdapter, player, null);
        var gameState = (GameStateDto) winState;

        // THEN
        assertEquals('X', (char) gameState.getGameResult().getWinnerPawn());
        assertNull(testsHelper.findGame(GAME_STATE_STRATEGY_TEST_THREE));
    }
}
