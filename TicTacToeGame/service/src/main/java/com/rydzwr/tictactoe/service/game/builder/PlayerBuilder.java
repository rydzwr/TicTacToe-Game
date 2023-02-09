package com.rydzwr.tictactoe.service.game.builder;

import com.rydzwr.tictactoe.database.constants.PlayerType;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.service.dto.incoming.PlayerDto;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PlayerBuilder {
    private char pawn;
    private User user;
    private Game game;
    private PlayerType playerType;
    private int playerGameIndex;


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
        var newPlayer = new Player();
        newPlayer.setPawn(this.pawn);
        newPlayer.setUser(this.user);
        newPlayer.setGame(this.game);
        newPlayer.setPlayerType(this.playerType);
        newPlayer.setPlayerGameIndex(this.playerGameIndex);
        return newPlayer;
    }
}
