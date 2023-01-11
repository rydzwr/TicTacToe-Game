package com.rydzwr.tictactoe.database.builder;

import com.rydzwr.tictactoe.database.constants.DatabaseConstants;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.database.model.User;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PlayerBuilder {
    private char pawn;
    private int score;
    private final String name;
    private final User user;
    private Game game;

    public PlayerBuilder(User user) {
        this.user = user;
        this.score = 0;
        this.name = user.getName();
    }

    public PlayerBuilder setPawn(char pawn) {
        this.pawn = pawn;
        return this;
    }

    public PlayerBuilder setGame(Game game) {
        this.game = game;
        return this;
    }

    public Player build(){
        if (Character.isWhitespace(this.pawn) || this.game == null) {
            throw new NullPointerException(DatabaseConstants.PLAYER_BUILD_EXCEPTION_VALUE);
        }
        return new Player(this);
    }
}
