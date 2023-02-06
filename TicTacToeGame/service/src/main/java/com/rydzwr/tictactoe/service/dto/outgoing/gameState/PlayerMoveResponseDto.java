package com.rydzwr.tictactoe.service.dto.outgoing.gameState;

import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import com.rydzwr.tictactoe.service.game.adapter.GameAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerMoveResponseDto {
    private List<Integer> processedMovesIndices;
    private List<Character> processedMovesPawns;
    private char currentPlayerMove;

    public PlayerMoveResponseDto() {
        this.processedMovesIndices = new ArrayList<>();
        this.processedMovesPawns = new ArrayList<>();
        this.currentPlayerMove = '-';
    }

    public void addValueToProcessedMovesIndices(MoveCoordsDto moveCoordsDto, GameAdapter gameAdapter) {
        var index = moveCoordsDto.getIndex(gameAdapter.getGameSize());
        processedMovesIndices.add(index);
    }

    public void addValueToProcessedMovesPawns(char pawn) {
        processedMovesPawns.add(pawn);
    }

    public void setCurrentPlayerMove(char currentPlayerMove) {
        this.currentPlayerMove = currentPlayerMove;
    }
}
