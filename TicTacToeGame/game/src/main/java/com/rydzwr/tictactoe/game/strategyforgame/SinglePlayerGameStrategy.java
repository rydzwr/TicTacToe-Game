package com.rydzwr.tictactoe.game.strategyforgame;

import com.rydzwr.tictactoe.database.builder.GameBuilder;
import com.rydzwr.tictactoe.database.builder.PlayerBuilder;
import com.rydzwr.tictactoe.database.constants.DatabaseConstants;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.GameRepository;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SinglePlayerGameStrategy implements BuildGameStrategy{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameRepository gameRepository;

    @Override
    public Game buildGame(GameDto gameDto) {
        /*
        // TODO Basic player builder init can be optimised for every game type
        GameBuilder gameBuilder = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty());
        Game game = gameBuilder.setGameOpponents(gameDto.getPlayers().size()).build();

        gameRepository.save(game);

        User caller = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());

        if (caller == null) {
            throw new NullPointerException(GameConstants.COULD_NOT_FIND_USER_EXCEPTION);
        }

        for (int i = 0; i < gameDto.getPlayers().size(); i++) {
            PlayerBuilder playerBuilder = new PlayerBuilder(game, gameDto.getPawnsString().charAt(i));
            playerBuilder.setUser(caller);
            playerBuilder.setType(DatabaseConstants.PERSON_PLAYER_TYPE);
            Player player = playerBuilder.build();
            playerRepository.save(player);
        }
        */

        return new Game();
    }



    @Override
    public boolean applies(String gameType) {
        return gameType.equals(GameConstants.SINGLE_PLAYER_GAME_NAME);
    }


}
