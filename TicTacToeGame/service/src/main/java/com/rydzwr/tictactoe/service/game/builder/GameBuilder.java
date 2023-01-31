package com.rydzwr.tictactoe.service.game.builder;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.constants.GameState;
import com.rydzwr.tictactoe.game.constants.GameConstants;

public class GameBuilder {
    private final int gameSize;
    private final int gameDifficulty;
    private String gameBoard;
    private String inviteCode;
    private int playersCount;

    private GameState gameState;
    public GameBuilder(int gameSize, int gameDifficulty) {
        this.gameSize = gameSize;
        this.gameDifficulty = gameDifficulty;
        generateBoard();
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
        var newGame = new Game();
        newGame.setGameSize(this.gameSize);
        newGame.setDifficulty(this.gameDifficulty);
        newGame.setState(this.gameState);
        newGame.setGameBoard(this.gameBoard);
        newGame.setInviteCode(this.inviteCode);
        newGame.setPlayersCount(this.playersCount);
       return newGame;
    }

    private void generateBoard() {
        this.gameBoard = GameConstants.DEFAULT_BOARD_VALUE.repeat(this.gameSize * this.gameSize);
    }
}
