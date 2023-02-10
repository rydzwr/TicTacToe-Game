package com.rydzwr.tictactoe.service.game;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.game.builder.PlayerBuilder;
import com.rydzwr.tictactoe.service.game.database.PlayerDatabaseService;
import com.rydzwr.tictactoe.service.game.strategy.selector.PlayerPawnRandomSelector;
import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameBuilderService {

    private final UserDatabaseService userDatabaseService;
    private final PlayerDatabaseService playerDatabaseService;

    public User getCaller(String callerName) {
        return userDatabaseService.findByName(callerName);
    }

    public void buildCallerPlayer(User caller, Game game, PlayerType playerType) {
        Player callerPlayer = new PlayerBuilder()
                .setPlayerType(playerType)
                .setPlayerGameIndex(0)
                .setPlayerPawn('X')
                .setUser(caller)
                .setGame(game)
                .build();

        playerDatabaseService.save(callerPlayer);
    }

    public void buildLocalPlayers(Game game, GameDto gameDto) {
        var playerPawnRandomSelector = new PlayerPawnRandomSelector();

        for (int i = 0; i < gameDto.getPlayers().size(); i++) {
            char pawn = playerPawnRandomSelector.selectPawn();
            Player player = new PlayerBuilder()
                    .setPlayerType(gameDto.getPlayers().get(i))
                    .setPlayerGameIndex(i + 1)
                    .setPlayerPawn(pawn)
                    .setGame(game)
                    .build();
            playerDatabaseService.save(player);
        }
    }

    public void buildAIPlayers(Game game, int aiPlayersCount) {
        PlayerPawnRandomSelector pawnSelector = new PlayerPawnRandomSelector();
        for (int i = 0; i < aiPlayersCount; i++) {
            Player aiPlayer = new PlayerBuilder()
                    .setPlayerPawn(pawnSelector.selectPawn())
                    .setPlayerType(PlayerType.AI)
                    .setPlayerGameIndex(i + 1)
                    .setGame(game)
                    .build();
            playerDatabaseService.save(aiPlayer);
        }
    }
}
