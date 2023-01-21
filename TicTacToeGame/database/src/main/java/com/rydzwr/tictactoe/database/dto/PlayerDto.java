package com.rydzwr.tictactoe.database.dto;

import com.rydzwr.tictactoe.database.validator.player.ValidPlayerType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {

    @NotNull
    @ValidPlayerType
    private String playerType;
}
