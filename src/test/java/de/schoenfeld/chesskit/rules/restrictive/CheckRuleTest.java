package de.schoenfeld.chesskit.rules.restrictive;

import de.schoenfeld.chesskit.model.ChessPiece;
import de.schoenfeld.chesskit.model.GameState;
import de.schoenfeld.chesskit.model.Square;
import de.schoenfeld.chesskit.model.StandardPieceType;
import de.schoenfeld.chesskit.move.Move;
import de.schoenfeld.chesskit.move.MoveLookup;
import de.schoenfeld.chesskit.rules.MoveGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CheckRuleTest {
    private CheckRule checkRule;
    private MoveGenerator<StandardPieceType> moveGenerator;
    private GameState<StandardPieceType> gameState;
    private MoveLookup<StandardPieceType> moves;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        moveGenerator = mock(MoveGenerator.class);
        checkRule = new CheckRule(moveGenerator);

        gameState = mock(GameState.class);
        moves = new MoveLookup<>();
    }

    @Test
    public void givenNoMoves_whenFilterMoves_thenNothingChanges() {
        assertTrue(moves.isEmpty());

        checkRule.filterMoves(moves, gameState);

        assertTrue(moves.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenLegalMove_whenFilterMoves_thenMoveRemains() {
        Move<StandardPieceType> legalMove = mock(Move.class);

        when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                .thenReturn(List.of(Square.e1)); // King's position
        when(moveGenerator.generateMoves(gameState)).thenReturn(new MoveLookup<>()); // No checks

        moves.add(legalMove);

        checkRule.filterMoves(moves, gameState);

        assertFalse(moves.isEmpty());
        assertTrue(moves.contains(legalMove));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenIllegalMove_whenFilterMoves_thenMoveIsRemoved() {
        Move<StandardPieceType> illegalMove = mock(Move.class);

        when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                .thenReturn(List.of(Square.e1)); // King's position

        MoveLookup<StandardPieceType> opponentMoves = MoveLookup.of(
                Move.of(mock(ChessPiece.class), Square.d2, Square.e1) // Attacking the king
        );
        when(moveGenerator.generateMoves(gameState)).thenReturn(opponentMoves);

        moves.add(illegalMove);

        checkRule.filterMoves(moves, gameState);

        assertTrue(moves.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenMixedMoves_whenFilterMoves_thenOnlyIllegalMovesAreRemoved() {
        Move<StandardPieceType> legalMove = mock(Move.class);
        Move<StandardPieceType> illegalMove = mock(Move.class);

        when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                .thenReturn(List.of(Square.e1));

        MoveLookup<StandardPieceType> opponentMoves = mock(MoveLookup.class);
        when(opponentMoves.containsMoveTo(Square.e1)).thenReturn(true);
        when(moveGenerator.generateMoves(gameState)).thenReturn(opponentMoves);

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e2)); // Move king to a safe square
            return null;
        }).when(gameState).makeMove(legalMove);

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e1)); // King stays in check
            return null;
        }).when(gameState).makeMove(illegalMove);

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e1)); // Restore original state
            return null;
        }).when(gameState).unmakeLastMove();

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e1));
            return null;
        }).when(gameState).unmakeLastMove();

        moves.add(legalMove);
        moves.add(illegalMove);

        checkRule.filterMoves(moves, gameState);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(legalMove));
        assertFalse(moves.contains(illegalMove));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenPinnedPieceMove_whenFilterMoves_thenPinnedMoveIsRemoved() {
        Move<StandardPieceType> pinnedMove = mock(Move.class);

        when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                .thenReturn(List.of(Square.e1));

        MoveLookup<StandardPieceType> opponentMoves = mock(MoveLookup.class);
        when(opponentMoves.containsMoveTo(Square.e1)).thenReturn(true);
        when(moveGenerator.generateMoves(gameState)).thenReturn(opponentMoves);

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e1)); // King remains in danger (pinned piece moves)
            return null;
        }).when(gameState).makeMove(pinnedMove);

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e1)); // Restore original state
            return null;
        }).when(gameState).unmakeLastMove();

        moves.add(pinnedMove);

        checkRule.filterMoves(moves, gameState);

        assertTrue(moves.isEmpty()); // Pinned move should be removed
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenKingMoves_whenFilterMoves_thenOnlySafeKingMovesRemain() {
        Move<StandardPieceType> safeKingMove = mock(Move.class);
        Move<StandardPieceType> unsafeKingMove = mock(Move.class);

        when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                .thenReturn(List.of(Square.e1));

        MoveLookup<StandardPieceType> opponentMoves = mock(MoveLookup.class);
        when(opponentMoves.containsMoveTo(Square.e2)).thenReturn(false); // e2 is safe
        when(opponentMoves.containsMoveTo(Square.d1)).thenReturn(true);  // d1 is attacked
        when(moveGenerator.generateMoves(gameState)).thenReturn(opponentMoves);

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e2)); // Move king to e2
            return null;
        }).when(gameState).makeMove(safeKingMove);

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.d1)); // Move king to d1 (unsafe)
            return null;
        }).when(gameState).makeMove(unsafeKingMove);

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e1)); // Restore original state
            return null;
        }).when(gameState).unmakeLastMove();

        doAnswer(invocation -> {
            when(gameState.getSquaresWithTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn()))
                    .thenReturn(List.of(Square.e1));
            return null;
        }).when(gameState).unmakeLastMove();

        moves.add(safeKingMove);
        moves.add(unsafeKingMove);

        checkRule.filterMoves(moves, gameState);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(safeKingMove));
        assertFalse(moves.contains(unsafeKingMove));
    }
}
