package com.rydzwr.tictactoe.service.game.strategy;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.ServiceTestsHelper;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.game.strategy.moveProcessor.AIPlayerMoveStrategy;
import com.rydzwr.tictactoe.service.game.strategy.moveProcessor.LocalPlayerMoveStrategy;
import com.rydzwr.tictactoe.service.game.strategy.moveProcessor.OnlinePlayerMoveStrategy;
import com.rydzwr.tictactoe.service.game.strategy.selector.PlayerMoveStrategySelector;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@Import(ServiceTestConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class MoveProcessorStrategyTest {
    @Autowired
    private PlayerMoveStrategySelector playerMoveStrategySelector;
    @Autowired
    private AIPlayerMoveStrategy aiPlayerMoveStrategy;
    @Autowired
    private LocalPlayerMoveStrategy localPlayerMoveStrategy;
    @Autowired
    private OnlinePlayerMoveStrategy onlinePlayerMoveStrategy;
    @Autowired
    private ServiceTestsHelper testsHelper;

    @Test
    public void shouldChooseAiPlayerStrategy() {
        var strategy = playerMoveStrategySelector.chooseStrategy(PlayerType.AI);
        assertTrue(strategy instanceof AIPlayerMoveStrategy);
    }
    @Test
    public void shouldChooseLocalPlayerStrategy() {
        var strategy = playerMoveStrategySelector.chooseStrategy(PlayerType.LOCAL);
        assertTrue(strategy instanceof LocalPlayerMoveStrategy);
    }
    @Test
    public void shouldChooseOnlinePlayerStrategy() {
        var strategy = playerMoveStrategySelector.chooseStrategy(PlayerType.ONLINE);
        assertTrue(strategy instanceof OnlinePlayerMoveStrategy);
    }

    @Test
    @DisplayName("Should Process Move For AI Player")
    public void aiPlayerMoveStrategyTest() {
        // GIVEN
        String MOVE_PROCESSOR_STRATEGY_TEST_ONE = "moveProcessorStrategyTestOne";
        var testUser = testsHelper.buildTestUser(MOVE_PROCESSOR_STRATEGY_TEST_ONE);
        var game = testsHelper.buildGameWithPlayers(MOVE_PROCESSOR_STRATEGY_TEST_ONE, 5, 5, testUser);

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var accessor = mock(SimpMessageHeaderAccessor.class);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var gameBoardToTest = game.getGameBoard();
        var currentPlayerTurnToTest = game.getCurrentPlayerTurn();

        // WHEN
        aiPlayerMoveStrategy.processPlayerMove(moves, gameAdapter, accessor, moveCoordsDto);

        // THEN
        assertNotEquals(gameBoardToTest, game.getGameBoard());
        assertNotEquals(currentPlayerTurnToTest, game.getCurrentPlayerTurn());
        assertEquals(1, moves.getProcessedMovesIndices().size());
        assertEquals(1, moves.getProcessedMovesPawns().size());
    }

    @Test
    @DisplayName("Should Trow Exception When Trying To Process Move On Full Game Board")
    public void aiPlayerMoveStrategyTestNotEmptyFieldsCase() {
        // GIVEN
        var game = mock(Game.class);
        when(game.getGameBoard()).thenReturn("XXXOOOXXX");
        var gameAdapter = new GameAdapter(game);

        // WHEN + THEN
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            aiPlayerMoveStrategy.processPlayerMove(null, gameAdapter, null, null);
        });

        assertEquals(GameConstants.ALL_FIELDS_ON_BOARD_OCCUPIED_EXCEPTION, exception.getMessage());
    }

    @Test
    @DisplayName("Should Process Move For Local Player")
    public void localPlayerMoveStrategyTest() {
        // GIVEN
        String MOVE_PROCESSOR_STRATEGY_TEST_TWO = "moveProcessorStrategyTestTwo";
        var testUser = testsHelper.buildTestUser(MOVE_PROCESSOR_STRATEGY_TEST_TWO);
        var game = testsHelper.buildGameWithPlayers(MOVE_PROCESSOR_STRATEGY_TEST_TWO, 5, 5, testUser);

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var accessor = mock(SimpMessageHeaderAccessor.class);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var gameBoardToTest = game.getGameBoard();
        var currentPlayerTurnToTest = game.getCurrentPlayerTurn();

        // WHEN
        localPlayerMoveStrategy.processPlayerMove(moves, gameAdapter, accessor, moveCoordsDto);

        // THEN
        assertNotEquals(gameBoardToTest, game.getGameBoard());
        assertNotEquals(currentPlayerTurnToTest, game.getCurrentPlayerTurn());
        assertEquals(1, moves.getProcessedMovesIndices().size());
        assertEquals(1, moves.getProcessedMovesPawns().size());
    }

    @Test
    @DisplayName("Should Throw Exception When Player Pressed Occupied Field")
    public void localPlayerMoveStrategyTestInvalidMoveCase() {
        // GIVEN
        var game = mock(Game.class);
        when(game.getGameBoard()).thenReturn("XXXOOOXXX");
        var gameAdapter = new GameAdapter(game);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        // WHEN + THEN
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            localPlayerMoveStrategy.processPlayerMove(null, gameAdapter, null, moveCoordsDto);
        });

        assertEquals(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION, exception.getMessage());
    }

    @Test
    @DisplayName("Should Process Move For Online Player")
    public void onlinePlayerMoveStrategyTest() {
        // GIVEN
        String MOVE_PROCESSOR_STRATEGY_TEST_THREE = "moveProcessorStrategyTestThree";
        var testUser = testsHelper.buildTestUser(MOVE_PROCESSOR_STRATEGY_TEST_THREE);
        var game = testsHelper.buildGameWithPlayers(MOVE_PROCESSOR_STRATEGY_TEST_THREE, 5, 5, testUser);

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var gameBoardToTest = game.getGameBoard();
        var currentPlayerTurnToTest = game.getCurrentPlayerTurn();

        var accessor = testsHelper.mockAccessor(MOVE_PROCESSOR_STRATEGY_TEST_THREE);

        // WHEN
        onlinePlayerMoveStrategy.processPlayerMove(moves, gameAdapter, accessor, moveCoordsDto);

        // THEN
        assertNotEquals(gameBoardToTest, game.getGameBoard());
        assertNotEquals(currentPlayerTurnToTest, game.getCurrentPlayerTurn());
        assertEquals(1, moves.getProcessedMovesIndices().size());
        assertEquals(1, moves.getProcessedMovesPawns().size());
    }

    @Test
    @DisplayName("Should Throw Error When Player Pressed Occupied Field In Online Game")
    public void onlinePlayerMoveStrategyTestCaseOccupiedFieldPressed() {
        // GIVEN
        String MOVE_PROCESSOR_STRATEGY_TEST_FOUR = "moveProcessorStrategyTestFour";
        var testUser = testsHelper.buildTestUser(MOVE_PROCESSOR_STRATEGY_TEST_FOUR);
        var game = testsHelper.buildGameWithCustomGameBoard(MOVE_PROCESSOR_STRATEGY_TEST_FOUR, 5, 5, testUser, "XXXOOOXXX");

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var accessor = testsHelper.mockAccessor(MOVE_PROCESSOR_STRATEGY_TEST_FOUR);

        // WHEN + THEN
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            onlinePlayerMoveStrategy.processPlayerMove(null, gameAdapter, accessor, moveCoordsDto);
        });

        assertEquals(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION, exception.getMessage());
    }

    @Test
    @DisplayName("Should Throw Error When Is Not Caller Turn")
    public void onlinePlayerMoveStrategyTestCaseInvalidPlayerTurn() {
        // GIVEN
        String MOVE_PROCESSOR_STRATEGY_TEST_FIVE = "moveProcessorStrategyTestFive";
        var testUser = testsHelper.buildTestUser(MOVE_PROCESSOR_STRATEGY_TEST_FIVE);
        var game = testsHelper.buildGameWithCustomGameBoard(MOVE_PROCESSOR_STRATEGY_TEST_FIVE, 5, 5, testUser, "---------");

        game.setCurrentPlayerTurn(5);

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var accessor = testsHelper.mockAccessor(MOVE_PROCESSOR_STRATEGY_TEST_FIVE);

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            onlinePlayerMoveStrategy.processPlayerMove(null, gameAdapter, accessor, moveCoordsDto);
        });

        assertEquals(GameConstants.NOT_CALLER_TURN_EXCEPTION, exception.getMessage());
    }
}
