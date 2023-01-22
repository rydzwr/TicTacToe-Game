package com.rydzwr.tictactoe.game.strategy.gameBuilder;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.database.service.GameDatabaseService;
import com.rydzwr.tictactoe.database.service.PlayerDatabaseService;
import com.rydzwr.tictactoe.database.service.UserDatabaseService;
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
public class LocalPlayerGameStrategy implements BuildGameStrategy {
    private final GameDatabaseService gameDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private final PlayerDatabaseService playerDatabaseService;

    @Override
    @Transactional
    public Game buildGame(GameDto gameDto) {
        log.info("----------------------------------------");
        log.info("LOCAL PLAYER GAME STRATEGY HAS BEEN USED");
        log.info("----------------------------------------");
        Game game = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty()).build();
        gameDatabaseService.save(game);

        User caller = userDatabaseService.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        assert caller != null;

        PlayerPawnRandomSelector playerPawnRandomSelector = new PlayerPawnRandomSelector();
        for (PlayerDto playerDto : gameDto.getPlayers()) {
            char pawn = playerPawnRandomSelector.selectPawn();
            Player player = new PlayerBuilder().setGame(game).setUser(caller).setPlayerType(playerDto).setPlayerPawn(pawn).build();
            playerDatabaseService.save(player);
        }

        game.setState(GameState.IN_PROGRESS);

        gameDatabaseService.save(game);
        return game;
    }


    @Override
    public boolean applies(GameDto gameDto) {
        List<String> playerTypes = gameDto.getPlayers().stream().map(PlayerDto::getPlayerType).toList();
        return playerTypes.contains("LOCAL");
    }
}
