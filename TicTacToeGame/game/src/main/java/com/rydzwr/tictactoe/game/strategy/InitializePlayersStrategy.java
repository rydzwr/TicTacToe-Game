package com.rydzwr.tictactoe.game.strategy;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;

import java.util.List;

public interface InitializePlayersStrategy {
    public List<Player> initPlayers(Game game);
    public boolean applies(String gameType);
}
