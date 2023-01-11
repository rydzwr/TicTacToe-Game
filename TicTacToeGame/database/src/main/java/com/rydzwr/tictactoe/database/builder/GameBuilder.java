package com.rydzwr.tictactoe.database.builder;

import com.rydzwr.tictactoe.database.constants.DatabaseConstants;
import com.rydzwr.tictactoe.database.model.Game;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GameBuilder {
    private int gameSize;
    private int difficulty;

    private String gameBoard;

    public GameBuilder(int gameSize, int gameDifficulty) {
        // TODO Game difficulty cannot be grater than boardSize

        this.gameSize = gameSize;
        this.difficulty = gameDifficulty;
        generateBoard();
    }

    private void generateBoard() {
        this.gameBoard = DatabaseConstants.DEFAULT_BOARD_VALUE.repeat(Math.max(0, this.gameSize));
    }

    public Game build() {
       return new Game(this);
    }
}
