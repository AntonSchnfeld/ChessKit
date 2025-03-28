package de.schoenfeld.chess.core;

import de.schoenfeld.chess.events.*;
import de.schoenfeld.chess.model.*;
import de.schoenfeld.chess.move.Move;
import de.schoenfeld.chess.move.MoveCollection;
import de.schoenfeld.chess.rules.Rules;
import de.schoenfeld.chess.ui.ChessBoardPanel;
import de.schoenfeld.chess.ui.ChessSquare;
import de.schoenfeld.chess.ui.ChessUIClient;

import javax.swing.*;
import java.awt.event.*;
import java.util.Map;

public class UIPlayer<T extends PieceType> extends Player<T> {
    private final ChessUIClient uiClient;
    private Square selectedSquare;
    private MoveCollection<T> legalMoves;
    private final Rules<T> rules;
    private GameState<T> gameState;

    public UIPlayer(PlayerData playerData, EventBus eventBus, ChessUIClient uiClient, Rules<T> rules) {
        super(playerData, eventBus);
        this.uiClient = uiClient;
        this.selectedSquare = null;
        this.legalMoves = MoveCollection.of();
        this.rules = rules;

        registerClickListener();
    }

    private void registerClickListener() {
        ChessBoardPanel panel = uiClient.getBoardPanel();
        Map<Square, ChessSquare> squares = panel.getSquares();

        for (Map.Entry<Square, ChessSquare> entry : squares.entrySet()) {
            entry.getValue().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mousePressed(e);
                    if (gameState.isWhiteTurn() == playerData.isWhite())
                        handleBoardClick(entry.getKey());
                }
            });
        }
    }

    private void handleBoardClick(Square clickedSquare) {
        ChessPiece<T> piece = gameState.getPieceAt(clickedSquare);

        // No piece selected yet
        if (selectedSquare == null) {
            // Select our piece and highlight its moves
            if (piece != null && piece.isWhite() == playerData.isWhite()) {
                selectedSquare = clickedSquare;
                MoveCollection<T> movesForPiece = legalMoves.getMovesFromSquare(clickedSquare);
                for (Move<T> move : movesForPiece)
                    uiClient.getBoardPanel().setSquareHighlight(move.to(), true);
            } else {
                // Enemy piece or no piece
                clearSelection();
            }
            return;
        }

        MoveCollection<T> movesForSelectedPiece = legalMoves.getMovesFromSquare(selectedSquare);
        // Publish move if legal
        if (movesForSelectedPiece.containsMoveTo(clickedSquare)) {
            Move<T> theMove = movesForSelectedPiece.getMovesTo(clickedSquare).getFirst();
            MoveProposedEvent<T> event = new MoveProposedEvent<>(gameId, playerData, theMove);
            eventBus.publish(event);
        } else clearSelection();
    }

    @Override
    protected void onGameEnded(GameEndedEvent event) {
        clearSelection();
    }

    @Override
    protected void onGameStateChanged(GameStateChangedEvent<T> event) {
        gameState = event.newState();
        legalMoves = rules.generateMoves(gameState);
        clearSelection();
    }

    @Override
    protected void onError(ErrorEvent event) {
        JOptionPane.showMessageDialog(null, "Error: " + event.errorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearSelection() {
        selectedSquare = null;
        uiClient.getBoardPanel().clearHighlights();
    }
}
