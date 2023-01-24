package com.rydzwr.tictactoe.database.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameStateDto {
    private String gameState;
    private char winnerPawn;

    public GameStateDto(String gameState) {
        this.gameState = gameState;
    }

    public GameStateDto(String gameState, char winnerPawn) {
        this.gameState = gameState;
        this.winnerPawn = winnerPawn;
    }
}
