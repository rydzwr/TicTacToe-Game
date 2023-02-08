package com.rydzwr.tictactoe.service.dto.incoming;

import com.rydzwr.tictactoe.service.game.validator.player.ValidPlayerType;
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
