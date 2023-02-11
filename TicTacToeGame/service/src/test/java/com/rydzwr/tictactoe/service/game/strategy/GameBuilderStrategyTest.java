package com.rydzwr.tictactoe.service.game.strategy;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.strategy.gameBuilder.LocalPlayerGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.gameBuilder.MultiPlayerGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.selector.GameBuilderStrategySelector;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import com.rydzwr.tictactoe.service.security.factory.UserFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@Import(ServiceTestConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class GameBuilderStrategyTest {

    @Autowired
    private GameBuilderStrategySelector strategySelector;
    @Autowired
    private LocalPlayerGameStrategy localPlayerGameStrategy;
    @Autowired
    private MultiPlayerGameStrategy multiPlayerGameStrategy;
    @Autowired
    private GameBuilderService gameBuilderService;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserDatabaseService userDatabaseService;

    @Autowired
    private GameDatabaseService gameDatabaseService;

    @Test
    public void testChooseLocalStrategy() {
        // GIVEN
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

        // WHEN
        var toTest = strategySelector.chooseStrategy(gameDto);

        // THEN
        assertTrue(toTest instanceof LocalPlayerGameStrategy);
    }

    @Test
    public void testChooseMultiStrategy() {
        // GIVEN
        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.ONLINE.name());
            playerDtoList.add(playerDto);
        }

        for (int i = 0; i < 5; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.AI.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();
        gameDto.setPlayers(playerDtoList);

        // WHEN
        var toTest = strategySelector.chooseStrategy(gameDto);

        // THEN
        assertTrue(toTest instanceof MultiPlayerGameStrategy);
    }

    @Test
    public void localPlayerGameStrategyTest() {
        // GIVEN
        var testUser = userFactory.createUser("localPlayerGameStrategyTest", "test");
        userDatabaseService.saveUser(testUser);

        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.LOCAL.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();
        gameDto.setGameSize(3);
        gameDto.setGameDifficulty(3);
        gameDto.setPlayers(playerDtoList);

        // WHEN
        var toTest = localPlayerGameStrategy.buildGame(gameDto, "localPlayerGameStrategyTest");

        // THEN
        assertEquals(3, toTest.getGameSize());
        assertEquals(3, toTest.getDifficulty());
        assertEquals(GameState.IN_PROGRESS, toTest.getState());
    }

    @Test
    public void multiPlayerGameStrategyTest() {
        // GIVEN
        var testUser = userFactory.createUser("multiPlayerGameStrategyTest", "test");
        userDatabaseService.saveUser(testUser);

        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.AI.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();
        gameDto.setGameSize(10);
        gameDto.setGameDifficulty(6);
        gameDto.setPlayers(playerDtoList);

        // WHEN
        var toTest = multiPlayerGameStrategy.buildGame(gameDto, "multiPlayerGameStrategyTest");
        var updatedGame = gameDatabaseService.findById(toTest.getId());

        // THEN
        assertEquals(10, updatedGame.getGameSize());
        assertEquals(6, updatedGame.getDifficulty());
        assertEquals(3, updatedGame.getPlayers().size());
        assertEquals(GameState.AWAITING_PLAYERS, updatedGame.getState());
    }
}




