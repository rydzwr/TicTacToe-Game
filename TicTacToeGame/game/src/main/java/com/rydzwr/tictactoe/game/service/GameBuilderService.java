package com.rydzwr.tictactoe.game.service;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.GameState;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameBuilderService {
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Transactional
    public void buildGame(GameDto gameDto) {
        GameBuilder gameBuilder = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty());
        Game game = gameBuilder.build();

        gameRepository.save(game);

        User caller = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        assert caller != null;

        boolean isLocalMultiplayer = true;
        for (PlayerDto playerDto : gameDto.getPlayers()) {
            Player player = new PlayerBuilder().setGame(game).setUser(caller).setPlayerDetails(playerDto).build();
            playerRepository.save(player);

            if (player.getPlayerType() != PlayerType.LOCAL) {
                isLocalMultiplayer = false;
            }
        }

        if (isLocalMultiplayer) {
            game.setState(GameState.IN_PROGRESS);
        }
        gameRepository.save(game);
    }
}
