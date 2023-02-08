package dto;

import com.rydzwr.tictactoe.service.dto.incoming.MoveCoordsDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveCoordsDtoTest {
    @Test
    public void testGetIndex() {
        var moveCoords = new MoveCoordsDto();

        moveCoords.setX(1);
        moveCoords.setY(3);
        assertEquals(6, moveCoords.getIndex(3));

        moveCoords.setX(3);
        moveCoords.setY(3);
        assertEquals(12, moveCoords.getIndex(3));

        moveCoords.setX(4);
        moveCoords.setY(3);
        assertEquals(15, moveCoords.getIndex(3));
    }

    @Test
    public void testCopy() {
        var moveCoords = new MoveCoordsDto(0, 3);
        var copy = moveCoords.copy();
        assertEquals(moveCoords.getX(), copy.getX());
        assertEquals(moveCoords.getY(), copy.getY());
    }

    @Test
    public void testMoveBy() {
        var moveCoords = new MoveCoordsDto(0, 3);
        moveCoords.moveBy(1, 1);
        assertEquals(1, moveCoords.getX());
        assertEquals(4, moveCoords.getY());
    }
}

