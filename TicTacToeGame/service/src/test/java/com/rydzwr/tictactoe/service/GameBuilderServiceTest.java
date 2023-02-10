package com.rydzwr.tictactoe.service;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.builder.GameBuilder;
import com.rydzwr.tictactoe.service.game.builder.PlayerBuilder;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.database.PlayerDatabaseService;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import com.rydzwr.tictactoe.service.security.factory.UserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import(ServiceTestConfiguration.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class GameBuilderServiceTest {

    @Autowired
    private UserDatabaseService userDatabaseService;
    @Autowired
    private PlayerDatabaseService playerDatabaseService;
    @Autowired
    private GameDatabaseService gameDatabaseService;
    @Autowired
    private GameBuilderService gameBuilderService;
    @Autowired
    private UserFactory userFactory;

    @Test
    public void injectionTest() {
        assertNotNull(userDatabaseService);
        assertNotNull(playerDatabaseService);
        assertNotNull(gameBuilderService);
    }

    @Test
    public void buildCallerPlayerTest() {
        // GIVEN
        var testUser = userFactory.createUser("buildCallerPlayerTest", "test");
        userDatabaseService.saveUser(testUser);

        var testGame = new GameBuilder(3, 3).setGameState(GameState.IN_PROGRESS).build();
        gameDatabaseService.save(testGame);

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
    public void buildLocalPlayersTest() {

        var testGame = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("buildLocalPlayersTest")
                .build();

        gameDatabaseService.save(testGame);

        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.LOCAL.name());
            playerDtoList.add(playerDto);
        }

        for (int i = 0; i < 5; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.AI.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();
        gameDto.setPlayers(playerDtoList);


        gameBuilderService.buildLocalPlayers(testGame, gameDto);

        var gameToTest = gameDatabaseService.findByInviteCode("buildLocalPlayersTest");

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
        assertEquals(5, localPlayers);
        assertEquals(5, aiPlayers);
        assertEquals(0, onlinePlayers);
        assertEquals(10, gameToTest.getPlayers().size());
    }

    @Test
    public void buildAIPlayersTest() {
        var testGame = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("buildAIPlayersTest")
                .build();

        gameDatabaseService.save(testGame);

        gameBuilderService.buildAIPlayers(testGame, 10);

        var gameToTest = gameDatabaseService.findByInviteCode("buildAIPlayersTest");

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
        assertEquals(10, gameToTest.getPlayers().size());
    }
}
