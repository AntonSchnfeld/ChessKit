package de.schoenfeld.chess.rules.generative.sliding;

import de.schoenfeld.chess.model.ChessPiece;
import de.schoenfeld.chess.model.GameState;
import de.schoenfeld.chess.model.StandardPieceType;
import de.schoenfeld.chess.move.MoveCollection;

import java.util.List;

public class QueenMoveRule extends SlidingPieceMoveRule<StandardPieceType> {

    public QueenMoveRule() {
        super(SlidingPieceMoveRule.ALL_DIRECTIONS);
    }

    @Override
    public MoveCollection<StandardPieceType> generateMoves(GameState<StandardPieceType> gameState) {
        List<ChessPiece<StandardPieceType>> queens = gameState.chessBoard()
                .getPiecesOfTypeAndColour(StandardPieceType.QUEEN, gameState.isWhiteTurn());
        MoveCollection<StandardPieceType> moves = new MoveCollection<>();

        for (ChessPiece<StandardPieceType> queen : queens) {
            generateMoves(gameState, queen, moves);
        }

        return moves;
    }
}
