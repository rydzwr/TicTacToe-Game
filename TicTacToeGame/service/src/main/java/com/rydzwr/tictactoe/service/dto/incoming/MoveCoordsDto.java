package com.rydzwr.tictactoe.service.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveCoordsDto {
    private int x;
    private int y;

    public int getIndex(int gameSize) {
        return this.x * gameSize + this.y;
    }

    public MoveCoordsDto copy() {
        var copy = new MoveCoordsDto();
        copy.setX(this.x);
        copy.setY(this.y);
        return copy;
    }

    public void moveBy(int x, int y) {
        this.setX(this.getX() + x);
        this.setY(this.getY() + y);
    }

    public void setCoords(int index, int gameSize) {
        this.x = index / gameSize;
        this.y = index % gameSize;
    }
}
