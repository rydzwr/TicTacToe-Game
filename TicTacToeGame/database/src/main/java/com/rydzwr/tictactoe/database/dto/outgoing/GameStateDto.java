package com.rydzwr.tictactoe.database.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameStateDto {
    private String gameState;
    private GameResultDto gameResult;

    public GameStateDto(String gameState) {
        this.gameState = gameState;
        this.gameResult = null;
    }
}
