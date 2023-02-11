package com.rydzwr.tictactoe.service.game.validator;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.ServiceTestConfiguration;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import com.rydzwr.tictactoe.service.game.builder.GameBuilder;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
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
public class PlayerMoveValidatorTest {

    @Autowired
    private PlayerMoveValidator playerMoveValidator;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserDatabaseService userDatabaseService;

    @Autowired
    private GameDatabaseService gameDatabaseService;
    @Autowired
    private GameBuilderService gameBuilderService;

    @Test
    public void validatePlayerMoveTestReturnsTrue() {
        var game = new GameBuilder(3, 3).build();

        var spyGame = spy(game);
        when(spyGame.getGameBoard()).thenReturn("X--------");

        var gameAdapter = new GameAdapter(spyGame);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var toTest = playerMoveValidator.validatePlayerMove(gameAdapter, moveCoordsDto);
        assertTrue(toTest);
    }

    @Test
    public void validatePlayerMoveTestReturnsFalse() {
        var game = new GameBuilder(3, 3).build();

        var spyGame = spy(game);
        when(spyGame.getGameBoard()).thenReturn("-X-------");

        var gameAdapter = new GameAdapter(spyGame);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(0);
        moveCoordsDto.setY(0);

        var toTest = playerMoveValidator.validatePlayerMove(gameAdapter, moveCoordsDto);
        assertFalse(toTest);
    }

    @Test
    public void validatePlayerMoveTestCaseOutOfBoard() {
        var game = new GameBuilder(3, 3).build();

        var spyGame = spy(game);
        when(spyGame.getGameBoard()).thenReturn("-X-------");

        var gameAdapter = new GameAdapter(spyGame);

        var moveCoordsDto = new MoveCoordsDto();
        moveCoordsDto.setX(10);
        moveCoordsDto.setY(10);

        var exception = assertThrows(IllegalArgumentException.class, () -> {
             playerMoveValidator.validatePlayerMove(gameAdapter, moveCoordsDto);
        });

        assertEquals(GameConstants.PLAYER_MOVE_OUT_OF_BOARD_EXCEPTION, exception.getMessage());
    }

    @Test
    public void validateCurrentPlayerTurnReturnsTrue() {
        var testUser = userFactory.createUser("testCaseTrue", "test");
        userDatabaseService.saveUser(testUser);

        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("testCaseTrue")
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
        var readyGame = gameDatabaseService.findByInviteCode("testCaseTrue");

        var spyGame = spy(readyGame);
        when(spyGame.getGameBoard()).thenReturn("---------");
        when(spyGame.getCurrentPlayerTurn()).thenReturn(0);

        var gameAdapter = new GameAdapter(spyGame);

        var accessor = mock(SimpMessageHeaderAccessor.class);
        var principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testCaseTrue");
        when(accessor.getUser()).thenReturn(principal);

        var toTest = playerMoveValidator.validateCurrentPlayerTurn(gameAdapter, accessor);
        log.info("BOOL: --> {}", toTest);
        assertTrue(toTest);
    }

    @Test
    public void validateCurrentPlayerTurnReturnsFalse() {
        var testUser = userFactory.createUser("validateCurrentPlayerTurnReturnsFalse", "test");
        userDatabaseService.saveUser(testUser);

        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("validateCurrentPlayerTurnReturnsFalse")
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
        var readyGame = gameDatabaseService.findByInviteCode("validateCurrentPlayerTurnReturnsFalse");

        var spyGame = spy(readyGame);
        when(spyGame.getGameBoard()).thenReturn("---------");
        when(spyGame.getCurrentPlayerTurn()).thenReturn(10);

        var gameAdapter = new GameAdapter(spyGame);

        var accessor = mock(SimpMessageHeaderAccessor.class);
        var principal = mock(Principal.class);
        when(principal.getName()).thenReturn("validateCurrentPlayerTurnReturnsFalse");
        when(accessor.getUser()).thenReturn(principal);

        var toTest = playerMoveValidator.validateCurrentPlayerTurn(gameAdapter, accessor);
        assertFalse(toTest);
    }
}
