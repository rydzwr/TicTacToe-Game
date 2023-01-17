package com.rydzwr.tictactoe.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GameDto {
    private int gameSize;
    private int gameDifficulty;
    private List<PlayerDto> players;
}
