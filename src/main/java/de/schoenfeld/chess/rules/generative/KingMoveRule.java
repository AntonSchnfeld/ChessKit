package de.schoenfeld.chess.rules.generative;

import de.schoenfeld.chess.board.ChessBoard;
import de.schoenfeld.chess.model.ChessPiece;
import de.schoenfeld.chess.model.GameState;
import de.schoenfeld.chess.model.Square;
import de.schoenfeld.chess.model.StandardPieceType;
import de.schoenfeld.chess.move.Move;
import de.schoenfeld.chess.move.MoveCollection;
import de.schoenfeld.chess.move.components.CaptureComponent;

import java.util.List;

public class KingMoveRule implements GenerativeMoveRule<StandardPieceType> {
    private static final List<Square> KING_DIRECTIONS = List.of(
            new Square(1, 0), new Square(-1, 0), new Square(0, 1), new Square(0, -1),
            new Square(1, 1), new Square(-1, -1), new Square(1, -1), new Square(-1, 1)
    );

    private static void generateKingMoves(GameState<StandardPieceType> gameState,
                                          ChessPiece<StandardPieceType> king,
                                          MoveCollection<StandardPieceType> moves) {
        // Generate moves in all directions
        for (var direction : KING_DIRECTIONS) {
            var from = gameState.chessBoard().getPiecePosition(king);
            var to = from.offset(direction);
            // Check if the target position is on the board
            if (gameState.chessBoard().getBounds().contains(to)) {
                var targetPiece = gameState.chessBoard().getPieceAt(to);
                // Check if the target position is empty or contains an enemy piece
                if (targetPiece == null) moves.add(Move.of(king, from, to));
                    // Capture
                else if (targetPiece.isWhite() != king.isWhite())
                    moves.add(Move.of(king, from, to, new CaptureComponent<>(targetPiece)));
            }
        }
    }

    @Override
    public MoveCollection<StandardPieceType> generateMoves(GameState<StandardPieceType> gameState) {
        MoveCollection<StandardPieceType> moves = new MoveCollection<>();
        ChessBoard<StandardPieceType> board = gameState.chessBoard();

        var kings = board
                .getPiecesOfTypeAndColour(StandardPieceType.KING, gameState.isWhiteTurn())
                .stream()
                .toList();

        for (var king : kings) generateKingMoves(gameState, king, moves);

        return moves;
    }
}
