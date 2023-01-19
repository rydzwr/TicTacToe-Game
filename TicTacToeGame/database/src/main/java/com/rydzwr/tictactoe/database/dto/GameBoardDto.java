package com.rydzwr.tictactoe.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class GameBoardDto {
    private String gameState;
    private char currentPlayerMove;

    public GameBoardDto(String gameState) {
        this.gameState = gameState;
    }

    public GameBoardDto(String gameState, char currentPlayerMove) {
        this.gameState = gameState;
        this.currentPlayerMove = currentPlayerMove;
    }
}
