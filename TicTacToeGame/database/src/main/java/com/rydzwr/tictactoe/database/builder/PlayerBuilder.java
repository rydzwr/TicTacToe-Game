package com.rydzwr.tictactoe.database.builder;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.validator.player.ValidPlayerPawn;
import jakarta.validation.Valid;
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

    public PlayerBuilder setPlayerType(@Valid PlayerDto playerDto) {
        this.playerType = PlayerType.valueOf(playerDto.getPlayerType());
        return this;
    }

    public PlayerBuilder setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
        return this;
    }

    public PlayerBuilder setPlayerPawn(char pawn) {
        this.pawn = pawn;
        return this;
    }

    public Player build() {
        return new Player(this);
    }
}
