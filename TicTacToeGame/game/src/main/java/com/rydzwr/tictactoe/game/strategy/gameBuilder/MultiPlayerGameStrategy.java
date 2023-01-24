package com.rydzwr.tictactoe.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.GameDto;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.database.service.PlayerDatabaseService;
import com.rydzwr.tictactoe.database.service.UserDatabaseService;
import com.rydzwr.tictactoe.game.algorithm.InviteCodeGenerator;
import com.rydzwr.tictactoe.game.selector.PlayerPawnRandomSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MultiPlayerGameStrategy implements BuildGameStrategy {
    private final GameDatabaseService gameDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private final PlayerDatabaseService playerDatabaseService;
    private final InviteCodeGenerator inviteCodeGenerator;

    @Override
    @Transactional
    public Game buildGame(GameDto gameDto) {
        String inviteCode = inviteCodeGenerator.generateCode();

        Game game = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty())
                .setPlayersCount(gameDto.getPlayers().size())
                .setGameState(GameState.AWAITING_PLAYERS)
                .setInviteCode(inviteCode)
                .build();

        gameDatabaseService.save(game);

        User caller = userDatabaseService.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        assert caller != null;

        int aiPlayersCount = (int) gameDto.getPlayers().stream()
                .filter((playerDto -> playerDto.getPlayerType().equals(PlayerType.AI.name())))
                .count();

        Player callerPlayer = new PlayerBuilder()
                .setPlayerType(PlayerType.ONLINE)
                .setPlayerPawn('X')
                .setUser(caller)
                .setGame(game)
                .build();

        playerDatabaseService.save(callerPlayer);

        PlayerPawnRandomSelector pawnSelector = new PlayerPawnRandomSelector();
        for (int i = 0; i < aiPlayersCount; i++) {
            Player aiPlayer = new PlayerBuilder()
                    .setPlayerPawn(pawnSelector.selectPawn())
                    .setPlayerType(PlayerType.AI)
                    .setGame(game)
                    .build();
            playerDatabaseService.save(aiPlayer);
        }

        gameDatabaseService.save(game);
        return game;
    }

    @Override
    public boolean applies(GameDto gameDto) {
        List<String> playerTypes = gameDto.getPlayers().stream().map(PlayerDto::getPlayerType).toList();
        return playerTypes.contains("ONLINE");
    }
}