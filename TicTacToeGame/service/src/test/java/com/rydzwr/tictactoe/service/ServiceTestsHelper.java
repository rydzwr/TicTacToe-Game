package com.rydzwr.tictactoe.service;

import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.service.game.GameBuilderService;
import com.rydzwr.tictactoe.service.game.builder.GameBuilder;
import com.rydzwr.tictactoe.service.game.builder.PlayerBuilder;
import com.rydzwr.tictactoe.service.game.database.GameDatabaseService;
import com.rydzwr.tictactoe.service.game.database.PlayerDatabaseService;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import com.rydzwr.tictactoe.service.security.factory.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Service
@Import(ServiceTestConfiguration.class)
public class ServiceTestsHelper {
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserDatabaseService userDatabaseService;

    @Autowired
    private GameDatabaseService gameDatabaseService;

    @Autowired
    private GameBuilderService gameBuilderService;
    @Autowired
    private PlayerDatabaseService playerDatabaseService;

    public User buildTestUser(String name) {
        var user = userFactory.createUser(name, "test");
        userDatabaseService.saveUser(user);
        return user;
    }

    public Game buildGameWithPlayers(String inviteCode, int localPlayersCount, int aiPlayersCount, User caller) {
        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode(inviteCode)
                .build();

        gameDatabaseService.save(game);

        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < localPlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.LOCAL.name());
            playerDtoList.add(playerDto);
        }

        for (int i = 0; i < aiPlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.AI.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();
        gameDto.setPlayers(playerDtoList);

        gameBuilderService.buildCallerPlayer(caller, game, PlayerType.LOCAL);
        gameBuilderService.buildLocalPlayers(game, gameDto);



        gameDatabaseService.save(game);
        return gameDatabaseService.findByInviteCode(inviteCode);
    }

    public GameDto buildGameDto(int gameSize, int gameDifficulty, int localPlayersCount, int aiPlayersCount) {
        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < localPlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.LOCAL.name());
            playerDtoList.add(playerDto);
        }

        for (int i = 0; i < aiPlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.AI.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();

        gameDto.setGameSize(gameSize);
        gameDto.setGameDifficulty(gameDifficulty);
        gameDto.setPlayers(playerDtoList);
        return gameDto;
    }

    public GameDto buildGameDto(int gameSize, int gameDifficulty, int onlinePlayersCount) {
        List<PlayerDto> playerDtoList = new ArrayList<>();

        for (int i = 0; i < onlinePlayersCount; i++) {
            var playerDto = new PlayerDto();
            playerDto.setPlayerType(PlayerType.ONLINE.name());
            playerDtoList.add(playerDto);
        }

        var gameDto = new GameDto();

        gameDto.setGameSize(gameSize);
        gameDto.setGameDifficulty(gameDifficulty);
        gameDto.setPlayers(playerDtoList);
        return gameDto;
    }

    public SimpMessageHeaderAccessor mockAccessor(String callerName) {
        var accessor = mock(SimpMessageHeaderAccessor.class);
        var principal = mock(Principal.class);
        when(principal.getName()).thenReturn(callerName);
        when(accessor.getUser()).thenReturn(principal);
        return accessor;
    }

    public Game buildGameWithCaller(User caller) {
        Game game = new GameBuilder(3, 3)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode("isUserInGameTestCaseTrue")
                .build();

        gameDatabaseService.save(game);

        gameBuilderService.buildCallerPlayer(caller, game, PlayerType.LOCAL);
        return game;
    }

    public Game buildGameWithCustomGameBoard(String inviteCode, int localPlayersCount, int aiPlayersCount, User caller, String gameBoard) {
       var game = buildGameWithPlayers(inviteCode, localPlayersCount, aiPlayersCount, caller);
       game.setGameBoard(gameBoard);
       gameDatabaseService.save(game);
       return game;
    }

    public Player buildPlayer(User caller, int playerGameIndex, char playerPawn, PlayerType playerType) {
        var player = new PlayerBuilder()
                .setPlayerGameIndex(playerGameIndex)
                .setPlayerType(playerType)
                .setPlayerPawn(playerPawn)
                .setUser(caller)
                .build();

        playerDatabaseService.save(player);
        return player;
    }

    public Game findGame(String inviteCode) {
        return gameDatabaseService.findByInviteCode(inviteCode);
    }

    public Game buildEmptyGame(String inviteCode, GameDto gameDto) {
        var testGame = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty())
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode(inviteCode)
                .build();

        gameDatabaseService.save(testGame);
        return gameDatabaseService.findByInviteCode(inviteCode);
    }

    public Game buildEmptyGame(String inviteCode, int gameSize, int gameDifficulty) {
        var testGame = new GameBuilder(gameSize, gameDifficulty)
                .setGameState(GameState.IN_PROGRESS)
                .setInviteCode(inviteCode)
                .build();

        gameDatabaseService.save(testGame);
        return gameDatabaseService.findByInviteCode(inviteCode);
    }
}
