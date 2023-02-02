package com.rydzwr.tictactoe.service.dto.incoming;

import lombok.Data;

@Data
public class MoveCoordsDto {
    private int x;
    private int y;

    public int getIndex(int gameSize) {
        return x * gameSize + y;
    }
}
