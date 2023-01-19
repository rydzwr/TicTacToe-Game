package com.rydzwr.tictactoe.database.dto;

import com.rydzwr.tictactoe.database.validator.player.ValidPlayerPawn;
import com.rydzwr.tictactoe.database.validator.player.ValidPlayerType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerDto {

    @NotNull
    @ValidPlayerType
    private String playerType;
    @NotNull
    @ValidPlayerPawn
    private char playerPawn;
}
