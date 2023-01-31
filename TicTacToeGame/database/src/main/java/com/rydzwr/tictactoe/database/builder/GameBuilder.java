package com.rydzwr.tictactoe.database.builder;

import com.rydzwr.tictactoe.database.constants.DatabaseConstants;
import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.database.model.Game;
import lombok.Getter;

@Getter
public class GameBuilder {
    private final int gameSize;
    private final int gameDifficulty;
    private int gameOpponents;
    private String gameBoard;
    private String inviteCode;
    private int playersCount;

    private GameState gameState;
    public GameBuilder(int gameSize, int gameDifficulty) {
        if (gameDifficulty > gameSize) {
            throw new IllegalArgumentException(DatabaseConstants.GAME_DIFFICULTY_IS_GRATER_THAN_GAME_SIZE);
        }

        this.gameSize = gameSize;
        this.gameDifficulty = gameDifficulty;
        generateBoard();
    }
    public GameBuilder setGameOpponents(int opponentsCount) {
        this.gameOpponents = opponentsCount;
        return this;
    }

    public GameBuilder setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
        return this;
    }

    public GameBuilder setPlayersCount(int playersCount) {
        this.playersCount = playersCount;
        return this;
    }

    public GameBuilder setGameState(GameState gameState) {
        this.gameState = gameState;
        return this;
    }

    public Game build() {
       return new Game(this);
    }

    // TODO USE SETTERS
    private void generateBoard() {
        this.gameBoard = DatabaseConstants.DEFAULT_BOARD_VALUE.repeat(this.gameSize * this.gameSize);
    }
}
