package com.rydzwr.tictactoe.service.game;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.ServiceTestsHelper;
import com.rydzwr.tictactoe.service.game.strategy.gameBuilder.MultiPlayerGameStrategy;
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
public class GameServiceTest {

    @Autowired
    private GameService gameService;
    @Autowired
    private MultiPlayerGameStrategy multiPlayerGameStrategy;
    @Autowired
    private ServiceTestsHelper testsHelper;

    @Test
    @DisplayName("Should Return Valid And Saved Game Entity")
    public void buildGameTest() {
        // GIVEN
        String GAME_SERVICE_TEST_ONE = "gameServiceTestOne";
        var testUser = testsHelper.buildTestUser(GAME_SERVICE_TEST_ONE);
        var gameDto = testsHelper.buildGameDto(10, 6, 5, 5);

        // WHEN
        var toTest = gameService.buildGame(gameDto, GAME_SERVICE_TEST_ONE);

        // THEN
        assertEquals(GameState.IN_PROGRESS, toTest.getState());
        assertEquals(10, toTest.getGameSize());
        assertEquals(6, toTest.getDifficulty());
    }

    @Test
    @DisplayName("Should Retrieve Invite Code From Game Entity In Database")
    public void getInviteCodeTest() {
        // GIVEN
        String GAME_SERVICE_TEST_TWO = "gameServiceTestTwo";
        var testUser = testsHelper.buildTestUser(GAME_SERVICE_TEST_TWO);
        testsHelper.buildGameWithPlayers(GAME_SERVICE_TEST_TWO, 5, 5, testUser);

        // WHEN
        var toTest = gameService.getInviteCode(GAME_SERVICE_TEST_TWO);

        //THEN
        assertEquals(GAME_SERVICE_TEST_TWO, toTest);
    }

    @Test
    @DisplayName("Should Return Player Assigned To User")
    public void retrieveAnyPlayerFromUserTest() {
        // GIVEN
        String GAME_SERVICE_TEST_THREE = "gameServiceTestThree";
        var testUser = testsHelper.buildTestUser(GAME_SERVICE_TEST_THREE);
        testsHelper.buildGameWithPlayers(GAME_SERVICE_TEST_THREE, 5, 5, testUser);

        var accessor = testsHelper.mockAccessor(GAME_SERVICE_TEST_THREE);

        // WHEN
        var toTest = gameService.retrieveAnyPlayerFromUser(accessor);

        // THEN
        assertEquals(testUser.getName(), toTest.getUser().getName());
    }

    @Test
    @DisplayName("Should Return True When User Is Assigned To Game")
    public void isUserInGameTestCaseTrue() {
        // GIVEN
        String GAME_SERVICE_TEST_FOUR = "gameServiceTestFour";
        var testUser = testsHelper.buildTestUser(GAME_SERVICE_TEST_FOUR);
        testsHelper.buildGameWithCaller(testUser);

        // WHEN
        var toTest = gameService.isUserInGame(GAME_SERVICE_TEST_FOUR);

        // THEN
        assertTrue(toTest);
    }

    @Test
    @DisplayName("Should Return True When User Is Not Assigned To Game")
    public void isUserInGameTestCaseFalse() {
        // GIVEN
        String GAME_SERVICE_TEST_FIVE = "gameServiceTestFive";
        testsHelper.buildTestUser(GAME_SERVICE_TEST_FIVE);

        // WHEN
        var toTest = gameService.isUserInGame(GAME_SERVICE_TEST_FIVE);

        // THEN
        assertFalse(toTest);
    }

    @Test
    @DisplayName("Should Return Previous User's Game")
    public void loadPreviousPlayerGame() {
        // GIVEN
        final String customGameBoard = "XOXXOXXOX";
        String GAME_SERVICE_TEST_SIX = "gameServiceTestSix";
        var testUser = testsHelper.buildTestUser(GAME_SERVICE_TEST_SIX);
        testsHelper.buildGameWithCustomGameBoard(GAME_SERVICE_TEST_SIX, 5, 5, testUser, customGameBoard);

        // WHEN
        var toTest = gameService.loadPreviousPlayerGame(GAME_SERVICE_TEST_SIX);

        // THEN
        assertEquals(customGameBoard, toTest.getBoard());
    }

    @Test
    @DisplayName("Should Remove Game Assigned To User")
    public void removePrevUserGameTest() {
        // GIVEN
        final String customGameBoard = "XOXXOXXOX";
        String GAME_SERVICE_TEST_SEVEN = "gameServiceTestSeven";
        var testUser = testsHelper.buildTestUser(GAME_SERVICE_TEST_SEVEN);
        testsHelper.buildGameWithCustomGameBoard(GAME_SERVICE_TEST_SEVEN, 5, 5, testUser, customGameBoard);

        // WHEN
        gameService.removePrevUserGame(GAME_SERVICE_TEST_SEVEN);

        // THEN
        var toTest = gameService.isUserInGame(GAME_SERVICE_TEST_SEVEN);
        assertFalse(toTest);
    }

    @Test
    @DisplayName("Should Add New User To Existing Online Game")
    public void addPlayerToOnlineGameTest() {
        // GIVEN
        String GAME_SERVICE_TEST_EIGHT_PLAYER_ONE = "gameServiceTestEightPlayerOne";
        testsHelper.buildTestUser(GAME_SERVICE_TEST_EIGHT_PLAYER_ONE);

        var gameDto = testsHelper.buildGameDto(10, 6, 5, 5);
        var game = multiPlayerGameStrategy.buildGame(gameDto, GAME_SERVICE_TEST_EIGHT_PLAYER_ONE);
        var inviteCode = game.getInviteCode();

        String GAME_SERVICE_TEST_EIGHT_PLAYER_TWO = "gameServiceTestEightPlayerTwo";
        testsHelper.buildTestUser(GAME_SERVICE_TEST_EIGHT_PLAYER_TWO);

        // WHEN
        gameService.addPlayerToOnlineGame(GAME_SERVICE_TEST_EIGHT_PLAYER_TWO, inviteCode);

        // THEN
        var toTest = gameService.isUserInGame(GAME_SERVICE_TEST_EIGHT_PLAYER_TWO);
        assertTrue(toTest);
    }
}
