package com.rydzwr.tictactoe.database.builder;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.dto.incoming.PlayerDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class PlayerBuilder {
    private char pawn;
    private User user;
    private Game game;
    private PlayerType playerType;

    private int playerGameIndex;
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

    public PlayerBuilder setPlayerGameIndex(int index) {
        this.playerGameIndex = index;
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
