package com.rydzwr.tictactoe.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LoadGameDto {
    private String gameState;
    private char currentPlayerMove;
    private int gameSize;
    private char pawn;

    public LoadGameDto(int gameSize, char pawn) {
        this.gameSize = gameSize;
        this.pawn = pawn;
    }

    public LoadGameDto(String gameState, char currentPlayerMove, int gameSize) {
        this.gameState = gameState;
        this.currentPlayerMove = currentPlayerMove;
        this.gameSize = gameSize;
    }
}
