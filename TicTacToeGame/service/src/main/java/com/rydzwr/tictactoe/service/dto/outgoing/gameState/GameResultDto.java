package com.rydzwr.tictactoe.service.dto.outgoing.gameState;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameResultDto {
    private String result;
    private Character winnerPawn;
}
