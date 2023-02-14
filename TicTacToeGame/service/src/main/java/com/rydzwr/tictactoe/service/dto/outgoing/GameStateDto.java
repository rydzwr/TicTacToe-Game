package com.rydzwr.tictactoe.service.dto.outgoing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rydzwr.tictactoe.service.dto.outgoing.gameState.GameResultDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class GameStateDto {
    private String gameState;
    private GameResultDto gameResult;

    public GameStateDto(String gameState) {
        this.gameState = gameState;
        this.gameResult = null;
    }

    @JsonProperty("gameResult")
    private void unpackNested(Map<String,Object> gameResult) {
        this.gameResult = new GameResultDto((String)gameResult.get("result"), (Character)gameResult.get("winnerPawn"));
    }
}
