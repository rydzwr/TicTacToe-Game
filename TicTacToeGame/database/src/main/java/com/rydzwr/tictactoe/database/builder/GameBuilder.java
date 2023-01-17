package com.rydzwr.tictactoe.database.builder;

import com.rydzwr.tictactoe.database.constants.DatabaseConstants;
import com.rydzwr.tictactoe.database.model.Game;
import lombok.Getter;

@Getter
public class GameBuilder {
    private int gameSize;
    private int gameDifficulty;
    private int gameOpponents;
    private String gameBoard;
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
    public Game build() {
       return new Game(this);
    }
    private void generateBoard() {
        this.gameBoard = DatabaseConstants.DEFAULT_BOARD_VALUE.repeat(this.gameSize * this.gameSize);
    }
}
