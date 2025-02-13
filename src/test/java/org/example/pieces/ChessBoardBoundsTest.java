package org.example.pieces;

import org.example.ChessBoardBounds;
import org.example.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChessBoardBoundsTest {
    @Test
    public void givenEightByEightDimensionsAndOutOfBoundsPositivePosition_whenContains_thenReturnFalse() {
        ChessBoardBounds dimensions = new ChessBoardBounds(8, 8);
        Position outOfBoundsPosition = new Position(8, 8);
        Assertions.assertFalse(dimensions.contains(outOfBoundsPosition));
    }

    @Test
    public void givenEightByEightDimensionsAndInBoundsPositivePosition_whenContains_thenReturnTrue() {
        ChessBoardBounds dimensions = new ChessBoardBounds(8, 8);
        Position inBoundsPosition = new Position(3, 3);
        Assertions.assertTrue(dimensions.contains(inBoundsPosition));
    }

    @Test
    public void givenNullPosition_whenContains_thenReturnFalse() {
        ChessBoardBounds dimensions = new ChessBoardBounds(8, 8);
        Assertions.assertFalse(dimensions.contains(null));
    }

    @Test
    public void givenNegativeColumns_whenConstructor_thenThrowsIllegalArgumentException() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> new ChessBoardBounds(8, -1));
        Assertions.assertEquals("columns must be at least 1", exception.getMessage());
    }

    @Test
    public void givenNegativeRows_whenConstructor_thenThrowsIllegalArgumentException() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> new ChessBoardBounds(-1, 8));
        Assertions.assertEquals("rows must be at least 1", exception.getMessage());
    }

    @Test
    public void givenZeroRows_whenConstructor_thenThrowsIllegalArgumentException() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> new ChessBoardBounds(0, 8));
        Assertions.assertEquals("rows must be at least 1", exception.getMessage());
    }

    @Test
    public void givenZeroColumns_whenConstructor_thenThrowsIllegalArgumentException() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> new ChessBoardBounds(8, 0));
        Assertions.assertEquals("columns must be at least 1", exception.getMessage());
    }

    @Test
    public void givenEightByEightDimensionsAndPositionAtUpperBounds_whenContains_thenReturnTrue() {
        ChessBoardBounds dimensions = new ChessBoardBounds(8, 8);
        Position upperBoundPosition = new Position(7, 7);
        Assertions.assertTrue(dimensions.contains(upperBoundPosition));
    }

    @Test
    public void givenEightByEightDimensionsAndPositionJustOutside_whenContains_thenReturnFalse() {
        ChessBoardBounds dimensions = new ChessBoardBounds(8, 8);
        Position outOfBoundsPosition = new Position(8, 7);
        Assertions.assertFalse(dimensions.contains(outOfBoundsPosition));
    }
}
