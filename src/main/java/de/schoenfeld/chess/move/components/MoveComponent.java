package de.schoenfeld.chess.move.components;

import de.schoenfeld.chess.board.ChessBoard;
import de.schoenfeld.chess.model.GameState;
import de.schoenfeld.chess.model.PieceType;
import de.schoenfeld.chess.move.Move;

import java.io.Serializable;

public interface MoveComponent<T extends PieceType> extends Serializable {
    ChessBoard<T> executeOn(GameState<T> gameState, Move<T> move);
}
