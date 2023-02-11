package com.rydzwr.tictactoe.service.game;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.ServiceTestsHelper;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
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
public class GameMoveServiceTest {

    @Autowired
    private GameMoveService gameMoveService;
    @Autowired
    private ServiceTestsHelper testsHelper;

    @Test
    @DisplayName("Should Return Valid Player Based On Current Player Index")
    public void getCurrentPlayerTest() {
        // GIVEN
        String GAME_MOVE_SERVICE_TEST_ONE = "gameMoveServiceTestOne";
        var testUser = testsHelper.buildTestUser(GAME_MOVE_SERVICE_TEST_ONE);
        var game = testsHelper.buildGameWithPlayers(GAME_MOVE_SERVICE_TEST_ONE, 5, 5, testUser);

        var gameAdapter = new GameAdapter(game);
        var player = gameAdapter.getCurrentPlayer();

        var accessor = testsHelper.mockAccessor(GAME_MOVE_SERVICE_TEST_ONE);

        // WHEN
        var toTest = gameMoveService.getCurrentPlayer(accessor);

        // THEN
        assertEquals(player.getId(), toTest.getId());
        assertEquals(player.getPawn(), toTest.getPawn());
        assertEquals(player.getPlayerGameIndex(), toTest.getPlayerGameIndex());
    }

    @Test
    @DisplayName("Should Throw Illegal Argument Exception When Player Is Not Found")
    public void getCurrentPlayerTestCasePlayerNull() {
        // GIVEN
        String GAME_MOVE_SERVICE_TEST_TWO = "gameMoveServiceTestTwo";
        var accessor = testsHelper.mockAccessor(GAME_MOVE_SERVICE_TEST_TWO);

        // WHEN + THEN
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            gameMoveService.getCurrentPlayer(accessor);
        });

        assertEquals(GameConstants.PLAYER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    @DisplayName("Should Throw Illegal Argument Exception When Game Is Not Found")
    public void getCurrentPlayerTestCaseGameNull() {
        // GIVEN
        String GAME_MOVE_SERVICE_TEST_THREE = "gameMoveServiceTestThree";
        var testUser = testsHelper.buildTestUser(GAME_MOVE_SERVICE_TEST_THREE);
        testsHelper.buildPlayer(testUser, 0, 'X', PlayerType.LOCAL);
        var accessor = testsHelper.mockAccessor(GAME_MOVE_SERVICE_TEST_THREE);

        // WHEN + THEN
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            gameMoveService.getCurrentPlayer(accessor);
        });

        assertEquals(GameConstants.GAME_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    @DisplayName("Should Return Valid PlayerMoveResponseDto")
    public void processPlayerMoveTest() {
        // GIVEN
        String GAME_MOVE_SERVICE_TEST_FOUR = "gameMoveServiceTestFour";
        var testUser = testsHelper.buildTestUser(GAME_MOVE_SERVICE_TEST_FOUR);

        var game = testsHelper.buildGameWithPlayers(GAME_MOVE_SERVICE_TEST_FOUR,0, 5, testUser);

        var accessor = testsHelper.mockAccessor(GAME_MOVE_SERVICE_TEST_FOUR);

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var currentPlayer = gameAdapter.getCurrentPlayer();

        // WHEN
        gameMoveService.processPlayerMove(moves, accessor, gameAdapter, moveCoordsDto, currentPlayer);
        var updatedGame = testsHelper.findGame(GAME_MOVE_SERVICE_TEST_FOUR);

        final String expectedGameBoard = "X--------";

        // THEN
        assertNotEquals(expectedGameBoard, updatedGame.getGameBoard());
        assertEquals(6, moves.getProcessedMovesIndices().size());
        assertEquals(6, moves.getProcessedMovesPawns().size());
    }

    @Test
    @DisplayName("Should Return GameState Draw For Full Game Board String")
    public void processGameStatusTestDrawCase() {
        // GIVEN
        String GAME_MOVE_SERVICE_TEST_FIVE = "gameMoveServiceTestFive";
        var testUser = testsHelper.buildTestUser(GAME_MOVE_SERVICE_TEST_FIVE);

        final String gameBoard = "QWERTYUIO";
        var game = testsHelper.buildGameWithCustomGameBoard(GAME_MOVE_SERVICE_TEST_FIVE, 0, 1, testUser, gameBoard);

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var currentPlayer = gameAdapter.getCurrentPlayer();

        // WHEN
        var returned = gameMoveService.processGameStatus(moves, gameAdapter, currentPlayer, moveCoordsDto);


        // THEN
        assertTrue(returned instanceof GameStateDto);

        var toTest = (GameStateDto) returned;
        assertEquals("DRAW", toTest.getGameResult().getResult());
    }

    @Test
    @DisplayName("Should Return Game State Win When Player Build Row Filled With His Pawn")
    public void processGameStatusTestWinCase() {
        // GIVEN
        String GAME_MOVE_SERVICE_TEST_SIX = "gameMoveServiceTestSix";
        var testUser = testsHelper.buildTestUser(GAME_MOVE_SERVICE_TEST_SIX);
        final String gameBoard = "-XX------";
        var game = testsHelper.buildGameWithCustomGameBoard(GAME_MOVE_SERVICE_TEST_SIX, 0, 1, testUser, gameBoard);

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var currentPlayer = gameAdapter.getCurrentPlayer();

        // WHEN
        var returned = gameMoveService.processGameStatus(moves, gameAdapter, currentPlayer, moveCoordsDto);

        // THEN
        assertTrue(returned instanceof GameStateDto);

        var toTest = (GameStateDto) returned;
        assertEquals("WIN", toTest.getGameResult().getResult());
        assertEquals('X', toTest.getGameResult().getWinnerPawn().charValue());
    }

    @Test
    @DisplayName("Should Return Continue Game State")
    public void processGameStatusTestContinueCase() {
        // GIVEN
        String GAME_MOVE_SERVICE_TEST_SEVEN = "gameMoveServiceTestSeven";
        var testUser = testsHelper.buildTestUser(GAME_MOVE_SERVICE_TEST_SEVEN);
        final String gameBoard = "QHSF--D-G";
        var game = testsHelper.buildGameWithCustomGameBoard(GAME_MOVE_SERVICE_TEST_SEVEN, 0, 1, testUser, gameBoard);

        var gameAdapter = new GameAdapter(game);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var currentPlayer = gameAdapter.getCurrentPlayer();

        // WHEN
        var returned = gameMoveService.processGameStatus(moves, gameAdapter, currentPlayer, moveCoordsDto);

        // THEN
        assertTrue(returned instanceof PlayerMoveResponseDto);
    }
}
