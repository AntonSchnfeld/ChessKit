package de.schoenfeld.chess.move.components;

import de.schoenfeld.chess.board.ChessBoard;
import de.schoenfeld.chess.model.ChessPiece;
import de.schoenfeld.chess.model.GameState;
import de.schoenfeld.chess.model.PieceType;
import de.schoenfeld.chess.model.Square;
import de.schoenfeld.chess.move.Move;

public record CastlingComponent(Move rookMove) implements MoveComponent {

    public CastlingComponent(ChessPiece rook, Square from, Square to) {
        this(Move.of(rook, from, to));
    }

    @Override
    public <T extends PieceType> ChessBoard<T> executeOn(GameState<T> gameState,
                                                         Move move) {
        return gameState.chessBoard().withPieceMoved(rookMove.from(), rookMove.to());
    }
}
