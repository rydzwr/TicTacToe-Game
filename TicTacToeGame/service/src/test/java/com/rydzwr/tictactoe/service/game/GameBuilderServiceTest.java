package com.rydzwr.tictactoe.service.game;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.ServiceTestsHelper;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.game.builder.PlayerBuilder;
import com.rydzwr.tictactoe.service.game.database.PlayerDatabaseService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import(ServiceTestConfiguration.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class GameBuilderServiceTest {

    @Autowired
    private PlayerDatabaseService playerDatabaseService;
    @Autowired
    private GameBuilderService gameBuilderService;
    @Autowired
    private ServiceTestsHelper testsHelper;

    @Test
    @DisplayName("Should Build New Player With Assigned User and Game")
    public void buildCallerPlayerTest() {
        // GIVEN
        String GAME_BUILDER_SERVICE_TEST_ONE = "gameBuilderServiceTestOne";
        var testUser = testsHelper.buildTestUser(GAME_BUILDER_SERVICE_TEST_ONE);

        var gameDto = new GameDto();
        gameDto.setGameSize(3);
        gameDto.setGameDifficulty(3);

        var testGame = testsHelper.buildEmptyGame(GAME_BUILDER_SERVICE_TEST_ONE, gameDto);

        var playerType = PlayerType.LOCAL;

        // EXPECTED
        Player expectedPlayer = new PlayerBuilder()
                .setPlayerType(playerType)
                .setPlayerGameIndex(0)
                .setPlayerPawn('X')
                .setUser(testUser)
                .setGame(testGame)
                .build();

        // WHEN
        gameBuilderService.buildCallerPlayer(testUser, testGame, playerType);

        var toTest = playerDatabaseService.findFirstByUser(testUser);

        // THEN
        assertNotNull(toTest);
        assertEquals('X', toTest.getPawn());
        assertEquals(0, toTest.getPlayerGameIndex());
        assertEquals(expectedPlayer.getPlayerType(), toTest.getPlayerType());
        assertEquals(expectedPlayer.getGame().getId(), toTest.getGame().getId());
        assertEquals(expectedPlayer.getUser().getId(), toTest.getUser().getId());
    }

    @Test
    @DisplayName("Should Build All Local Players For Game")
    public void buildLocalPlayersTest() {

        var gameDto = testsHelper.buildGameDto(30, 3, 5, 5);
        String GAME_BUILDER_SERVICE_TEST_TWO = "gameBuilderServiceTestTwo";
        var game = testsHelper.buildEmptyGame(GAME_BUILDER_SERVICE_TEST_TWO, gameDto);

        gameBuilderService.buildLocalPlayers(game, gameDto);

        var updatedGame = testsHelper.findGame(GAME_BUILDER_SERVICE_TEST_TWO);

        int localPlayers = (int) updatedGame.getPlayers().stream()
                .filter((player -> player.getPlayerType().equals(PlayerType.LOCAL)))
                .count();

        int aiPlayers = (int) updatedGame.getPlayers().stream()
                .filter((player -> player.getPlayerType().equals(PlayerType.AI)))
                .count();

        int onlinePlayers = (int) updatedGame.getPlayers().stream()
                .filter((player -> player.getPlayerType().equals(PlayerType.ONLINE)))
                .count();

        assertNotNull(updatedGame);
        assertEquals(5, localPlayers);
        assertEquals(5, aiPlayers);
        assertEquals(0, onlinePlayers);
    }

    @Test
    @DisplayName("Should Build All AI Players For Game")
    public void buildAIPlayersTest() {
        var gameDto = testsHelper.buildGameDto(30, 3, 0, 10);
        String GAME_BUILDER_SERVICE_TEST_THREE = "gameBuilderServiceTestThree";
        var game = testsHelper.buildEmptyGame(GAME_BUILDER_SERVICE_TEST_THREE, gameDto);

        gameBuilderService.buildAIPlayers(game, 10);

        var gameToTest = testsHelper.findGame(GAME_BUILDER_SERVICE_TEST_THREE);

        int localPlayers = (int) gameToTest.getPlayers().stream()
                .filter((player -> player.getPlayerType().equals(PlayerType.LOCAL)))
                .count();

        int aiPlayers = (int) gameToTest.getPlayers().stream()
                .filter((player -> player.getPlayerType().equals(PlayerType.AI)))
                .count();

        int onlinePlayers = (int) gameToTest.getPlayers().stream()
                .filter((player -> player.getPlayerType().equals(PlayerType.ONLINE)))
                .count();

        assertNotNull(gameToTest);
        assertEquals(0, localPlayers);
        assertEquals(10, aiPlayers);
        assertEquals(0, onlinePlayers);
    }
}
