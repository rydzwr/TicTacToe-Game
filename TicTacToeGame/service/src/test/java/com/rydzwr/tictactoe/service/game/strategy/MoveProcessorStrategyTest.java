package com.rydzwr.tictactoe.service.game.strategy;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.PlayerMoveResponseDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.builder.GameBuilder;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.strategy.moveProcessor.AIPlayerMoveStrategy;
import com.rydzwr.tictactoe.service.game.strategy.moveProcessor.LocalPlayerMoveStrategy;
import com.rydzwr.tictactoe.service.game.strategy.moveProcessor.OnlinePlayerMoveStrategy;
import com.rydzwr.tictactoe.service.game.strategy.selector.PlayerMoveStrategySelector;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import com.rydzwr.tictactoe.service.security.factory.UserFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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
    private UserDatabaseService userDatabaseService;
    @Autowired
    private GameDatabaseService gameDatabaseService;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private GameBuilderService gameBuilderService;

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
    public void aiPlayerMoveStrategyTest() {
        var testUser = userFactory.createUser("aiPlayerMoveStrategyTest", "test");
        userDatabaseService.saveUser(testUser);

        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("aiPlayerMoveStrategyTest")
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
        var readyGame = gameDatabaseService.findByInviteCode("aiPlayerMoveStrategyTest");

        var gameAdapter = new GameAdapter(readyGame);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var accessor = mock(SimpMessageHeaderAccessor.class);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var gameBoardToTest = readyGame.getGameBoard();
        var currentPlayerTurnToTest = readyGame.getCurrentPlayerTurn();

        aiPlayerMoveStrategy.processPlayerMove(moves, gameAdapter, accessor, moveCoordsDto);

        assertNotEquals(gameBoardToTest, readyGame.getGameBoard());
        assertNotEquals(currentPlayerTurnToTest, readyGame.getCurrentPlayerTurn());
        assertEquals(1, moves.getProcessedMovesIndices().size());
        assertEquals(1, moves.getProcessedMovesPawns().size());
    }

    @Test
    public void aiPlayerMoveStrategyTestNotEmptyFieldsCase() {
        var game = mock(Game.class);
        when(game.getGameBoard()).thenReturn("XXXOOOXXX");
        var gameAdapter = new GameAdapter(game);

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            aiPlayerMoveStrategy.processPlayerMove(null, gameAdapter, null, null);
        });

        assertEquals(GameConstants.ALL_FIELDS_ON_BOARD_OCCUPIED_EXCEPTION, exception.getMessage());
    }

    @Test
    public void localPlayerMoveStrategyTest() {
        var testUser = userFactory.createUser("localPlayerMoveStrategyTest", "test");
        userDatabaseService.saveUser(testUser);

        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("localPlayerMoveStrategyTest")
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
        var readyGame = gameDatabaseService.findByInviteCode("localPlayerMoveStrategyTest");

        var gameAdapter = new GameAdapter(readyGame);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var accessor = mock(SimpMessageHeaderAccessor.class);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var gameBoardToTest = readyGame.getGameBoard();
        var currentPlayerTurnToTest = readyGame.getCurrentPlayerTurn();

        localPlayerMoveStrategy.processPlayerMove(moves, gameAdapter, accessor, moveCoordsDto);

        assertNotEquals(gameBoardToTest, readyGame.getGameBoard());
        assertNotEquals(currentPlayerTurnToTest, readyGame.getCurrentPlayerTurn());
        assertEquals(1, moves.getProcessedMovesIndices().size());
        assertEquals(1, moves.getProcessedMovesPawns().size());
    }

    @Test
    public void localPlayerMoveStrategyTestInvalidMoveCase() {
        var game = mock(Game.class);
        when(game.getGameBoard()).thenReturn("XXXOOOXXX");
        var gameAdapter = new GameAdapter(game);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            localPlayerMoveStrategy.processPlayerMove(null, gameAdapter, null, moveCoordsDto);
        });

        assertEquals(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION, exception.getMessage());
    }

    @Test
    public void onlinePlayerMoveStrategyTest() {
        var testUser = userFactory.createUser("onlinePlayerMoveStrategyTest", "test");
        userDatabaseService.saveUser(testUser);

        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("onlinePlayerMoveStrategyTest")
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
        var readyGame = gameDatabaseService.findByInviteCode("onlinePlayerMoveStrategyTest");

        var gameAdapter = new GameAdapter(readyGame);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var gameBoardToTest = readyGame.getGameBoard();
        var currentPlayerTurnToTest = readyGame.getCurrentPlayerTurn();

        var accessor = mock(SimpMessageHeaderAccessor.class);

        var principal = mock(Principal.class);
        when(principal.getName()).thenReturn("onlinePlayerMoveStrategyTest");

        when(accessor.getUser()).thenReturn(principal);

        onlinePlayerMoveStrategy.processPlayerMove(moves, gameAdapter, accessor, moveCoordsDto);

        assertNotEquals(gameBoardToTest, readyGame.getGameBoard());
        assertNotEquals(currentPlayerTurnToTest, readyGame.getCurrentPlayerTurn());
        assertEquals(1, moves.getProcessedMovesIndices().size());
        assertEquals(1, moves.getProcessedMovesPawns().size());
    }

    @Test
    public void onlinePlayerMoveStrategyTestCaseOccupiedFieldPressed() {
        var testUser = userFactory.createUser("onlinePlayerMoveStrategyTest", "test");
        userDatabaseService.saveUser(testUser);

        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("onlinePlayerMoveStrategyTest")
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
        var readyGame = gameDatabaseService.findByInviteCode("onlinePlayerMoveStrategyTest");

        var spyGame = spy(readyGame);
        when(spyGame.getGameBoard()).thenReturn("XXXOOOXXX");

        var gameAdapter = new GameAdapter(spyGame);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var accessor = mock(SimpMessageHeaderAccessor.class);
        var principal = mock(Principal.class);
        when(principal.getName()).thenReturn("onlinePlayerMoveStrategyTest");
        when(accessor.getUser()).thenReturn(principal);

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            onlinePlayerMoveStrategy.processPlayerMove(null, gameAdapter, accessor, moveCoordsDto);
        });

        assertEquals(GameConstants.PLAYER_PRESSED_OCCUPIED_FIELD_EXCEPTION, exception.getMessage());
    }

    @Test
    public void onlinePlayerMoveStrategyTestCaseInvalidPlayerTurn() {
        var testUser = userFactory.createUser("onlinePlayerMoveStrategyTest", "test");
        userDatabaseService.saveUser(testUser);

        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("onlinePlayerMoveStrategyTest")
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
        var readyGame = gameDatabaseService.findByInviteCode("onlinePlayerMoveStrategyTest");

        var spyGame = spy(readyGame);
        when(spyGame.getGameBoard()).thenReturn("---------");
        when(spyGame.getCurrentPlayerTurn()).thenReturn(5);

        var gameAdapter = new GameAdapter(spyGame);

        var moves = new PlayerMoveResponseDto();
        moves.setCurrentPlayerMove('X');

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var accessor = mock(SimpMessageHeaderAccessor.class);
        var principal = mock(Principal.class);
        when(principal.getName()).thenReturn("onlinePlayerMoveStrategyTest");
        when(accessor.getUser()).thenReturn(principal);

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            onlinePlayerMoveStrategy.processPlayerMove(null, gameAdapter, accessor, moveCoordsDto);
        });

        assertEquals(GameConstants.NOT_CALLER_TURN_EXCEPTION, exception.getMessage());
    }
}
