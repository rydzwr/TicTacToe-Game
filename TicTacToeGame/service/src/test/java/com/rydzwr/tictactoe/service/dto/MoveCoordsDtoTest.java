package com.rydzwr.tictactoe.service.dto;

import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoveCoordsDtoTest {
    @Test
    public void testGetIndex() {
        var moveCoords = new MoveCoordsDto();

        moveCoords.setX(1);
        moveCoords.setY(3);
        Assertions.assertEquals(6, moveCoords.getIndex(3));

        moveCoords.setX(3);
        moveCoords.setY(3);
        Assertions.assertEquals(12, moveCoords.getIndex(3));

        moveCoords.setX(4);
        moveCoords.setY(3);
        Assertions.assertEquals(15, moveCoords.getIndex(3));
    }

    @Test
    public void testCopy() {
        var moveCoords = new MoveCoordsDto(0, 3);
        var copy = moveCoords.copy();
        Assertions.assertEquals(moveCoords.getX(), copy.getX());
        Assertions.assertEquals(moveCoords.getY(), copy.getY());
    }

    @Test
    public void testMoveBy() {
        var moveCoords = new MoveCoordsDto(0, 3);
        moveCoords.moveBy(1, 1);
        Assertions.assertEquals(1, moveCoords.getX());
        Assertions.assertEquals(4, moveCoords.getY());
    }
}

