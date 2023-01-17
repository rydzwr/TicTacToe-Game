package com.rydzwr.tictactoe.database.builder;

import com.rydzwr.tictactoe.database.constants.DatabaseConstants;
import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PlayerBuilder {
    private char pawn;
    private User user;
    private Game game;
    private PlayerType playerType;
    public PlayerBuilder() {
    }

    public PlayerBuilder setGame(Game game) {
        this.game = game;
        return this;
    }

    public PlayerBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public PlayerBuilder setPlayerDetails(PlayerDto playerDto) {
        this.pawn = playerDto.getPlayerPawn();

        // TODO validator

        this.playerType = PlayerType.valueOf(playerDto.getPlayerType());
        return this;
    }

    public Player build() {
        return new Player(this);
    }
}
