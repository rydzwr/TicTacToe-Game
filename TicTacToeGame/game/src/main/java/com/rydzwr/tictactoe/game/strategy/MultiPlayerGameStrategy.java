package com.rydzwr.tictactoe.game.strategy;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MultiPlayerGameStrategy implements BuildGameStrategy {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlayerRepository playerRepository;

    // TODO CLASS NOT IMPLEMENTED PROPERLY!

    @Override
    @Transactional
    public void buildGame(GameDto gameDto) {
        Game game = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty()).build();
        gameRepository.save(game);

        User caller = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        assert caller != null;

        for (PlayerDto playerDto : gameDto.getPlayers()) {
            Player player = new PlayerBuilder().setGame(game).setUser(caller).setPlayerDetails(playerDto).build();
            playerRepository.save(player);
        }

        // TODO NEED TO IMPLEMENT AWAITING FOR ONLINE PLAYERS ACCEPTING INVITATIONS

        //game.setState(GameState.IN_PROGRESS);

        gameRepository.save(game);
    }

    @Override
    public boolean applies(GameDto gameDto) {
        return gameDto.getPlayers().stream().anyMatch(p -> p.getPlayerType().equals(PlayerType.ONLINE.name()));
    }
}