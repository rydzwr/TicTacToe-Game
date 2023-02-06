package com.rydzwr.tictactoe.service.dto.incoming;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MoveCoordsDto {
    private int x;
    private int y;

    public int getIndex(int gameSize) {
        return x * gameSize + y;
    }

    public MoveCoordsDto(int index, int gameSize) {
        this.x = index / gameSize;
        this.y = index % gameSize;
    }
}
