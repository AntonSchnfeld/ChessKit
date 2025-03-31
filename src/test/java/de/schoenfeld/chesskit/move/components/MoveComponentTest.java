package de.schoenfeld.chesskit.move.components;

import de.schoenfeld.chesskit.model.ChessPiece;
import de.schoenfeld.chesskit.model.GameState;
import de.schoenfeld.chesskit.model.PieceType;
import de.schoenfeld.chesskit.model.Square;
import de.schoenfeld.chesskit.move.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class MoveComponentTest<T extends PieceType> {
    protected GameState<T> gameState;
    protected Move<T> move;
    protected MoveComponent<T> moveComponent;
    protected Square from;
    protected Square to;
    protected ChessPiece<T> piece;

    @BeforeEach
    protected abstract void setup();

    @Test
    void givenMove_whenMakeOn_thenComponentAppliesEffect() {
        // When
        gameState.makeMove(move);

        // Then
        verifyComponentExecuted(gameState, move);
    }

    @Test
    void givenExecutedMove_whenUnmakeOn_thenComponentReversesEffect() {
        // When
        gameState.makeMove(move);
        gameState.unmakeLastMove();

        // Then
        verifyComponentUndone(gameState, move);
    }

    protected abstract void verifyComponentExecuted(GameState<T> gameState, Move<T> move);

    protected abstract void verifyComponentUndone(GameState<T> gameState, Move<T> move);
}
