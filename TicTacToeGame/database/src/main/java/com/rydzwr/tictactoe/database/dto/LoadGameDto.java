package com.rydzwr.tictactoe.database.dto;

import com.rydzwr.tictactoe.database.model.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadGameDto {
    private String state;
    private String board;
    private char currentPlayerMove;
    private int difficulty;
    private int size;
    private char playerPawn;

    public LoadGameDto(Game game, char playerPawn) {
        this.state = game.getState().name();
        this.board = game.getGameBoard();
        //this.currentPlayerMove = game.getPlayers().get(game.getCurrentPlayerTurn()).getPawn();
        this.currentPlayerMove = 'X';
        this.difficulty = game.getDifficulty();
        this.size = game.getGameSize();
        this.playerPawn = playerPawn;
    }
}
