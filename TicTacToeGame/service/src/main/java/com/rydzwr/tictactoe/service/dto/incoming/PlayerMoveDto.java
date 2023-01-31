package com.rydzwr.tictactoe.service.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerMoveDto {
    private int gameBoardElementIndex;
}
