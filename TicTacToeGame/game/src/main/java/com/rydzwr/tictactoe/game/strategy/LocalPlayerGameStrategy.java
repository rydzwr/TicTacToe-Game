package com.rydzwr.tictactoe.game.strategy;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.GameService;
import com.rydzwr.tictactoe.database.service.PlayerService;
import com.rydzwr.tictactoe.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LocalPlayerGameStrategy implements BuildGameStrategy {
    @Autowired
    private GameService gameService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlayerService playerService;

    @Override
    @Transactional
    public void buildGame(GameDto gameDto) {
        Game game = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty()).build();
        gameService.save(game);

        User caller = userService.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        assert caller != null;

        for (PlayerDto playerDto : gameDto.getPlayers()) {
            Player player = new PlayerBuilder().setGame(game).setUser(caller).setPlayerDetails(playerDto).build();
            playerService.save(player);
        }

        game.setState(GameState.IN_PROGRESS);

        gameService.save(game);
    }


    @Override
    public boolean applies(GameDto gameDto) {
        return gameDto.getPlayers().stream().allMatch(p -> p.getPlayerType().equals(PlayerType.LOCAL.name()));
    }
}
