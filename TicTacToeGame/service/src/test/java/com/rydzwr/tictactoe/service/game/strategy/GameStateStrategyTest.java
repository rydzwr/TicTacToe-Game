package com.rydzwr.tictactoe.service.game.strategy;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.dto.outgoing.GameStateDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.CheckWinState;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.builder.GameBuilder;
import com.rydzwr.tictactoe.service.game.builder.PlayerBuilder;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.strategy.gameState.ContinueGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.gameState.DrawStateStrategy;
import com.rydzwr.tictactoe.service.game.strategy.gameState.WinGameStrategy;
import com.rydzwr.tictactoe.service.game.strategy.selector.GameStateStrategySelector;
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
    private UserFactory userFactory;
    @Autowired
    private GameDatabaseService gameDatabaseService;
    @Autowired
    private GameBuilderService gameBuilderService;
    @Autowired
    private UserDatabaseService userDatabaseService;

    @Test
    public void shouldChooseContinueGameStrategy() {
        var strategy = gameStateStrategySelector.chooseStrategy(CheckWinState.CONTINUE);
        assertTrue(strategy instanceof ContinueGameStrategy);
    }

    @Test
    public void shouldChooseWinGameStrategy() {
        var strategy = gameStateStrategySelector.chooseStrategy(CheckWinState.WIN);
        assertTrue(strategy instanceof WinGameStrategy);
    }

    @Test
    public void shouldChooseDrawGameStrategy() {
        var strategy = gameStateStrategySelector.chooseStrategy(CheckWinState.DRAW);
        assertTrue(strategy instanceof DrawStateStrategy);
    }

    @Test
    public void continueGameStrategyTest() {
        // GIVEN
        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('O');

        var testUser = userFactory.createUser("continueGameStrategyTest", "test");
        userDatabaseService.saveUser(testUser);

        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("continueGameStrategyTest")
                .build();

        gameDatabaseService.save(game);

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

        gameBuilderService.buildCallerPlayer(testUser, game, PlayerType.LOCAL);
        gameBuilderService.buildLocalPlayers(game, gameDto);

        gameDatabaseService.save(game);
        var readyGame = gameDatabaseService.findByInviteCode("continueGameStrategyTest");

        var gameAdapter = new GameAdapter(readyGame);

        var player = new PlayerBuilder()
                .setPlayerPawn('X')
                .build();

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var prev = moves.getCurrentPlayerMove();

        // WHEN
        var updatedMoves = continueGameStrategy.resolve(moves, gameAdapter, player, moveCoordsDto);
        PlayerMoveResponseDto toTest = (PlayerMoveResponseDto) updatedMoves;

        var next = toTest.getCurrentPlayerMove();

        // THEN
        assertNotEquals(prev, next);
    }

    @Test
    public void drawStateStrategyTest() {
        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("drawStateStrategyTest")
                .build();

        gameDatabaseService.save(game);

        var gameAdapter = new GameAdapter(game);

        assertNotNull(gameDatabaseService.findByInviteCode("drawStateStrategyTest"));

        drawStateStrategy.resolve(null, gameAdapter, null, null);

        assertNull(gameDatabaseService.findByInviteCode("drawStateStrategyTest"));
    }

    @Test
    public void winStateStrategyTest() {
        // GIVEN
        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("winStateStrategyTest")
                .build();

        gameDatabaseService.save(game);

        var player = new Player();
        player.setPawn('X');

        var gameAdapter = new GameAdapter(game);

        assertNotNull(gameDatabaseService.findByInviteCode("winStateStrategyTest"));

        // WHEN
        var winState = winGameStrategy.resolve(null, gameAdapter, player, null);
        var gameState = (GameStateDto) winState;

        // THEN
        assertEquals('X', (char) gameState.getGameResult().getWinnerPawn());
        assertNull(gameDatabaseService.findByInviteCode("winStateStrategyTest"));
    }
}
