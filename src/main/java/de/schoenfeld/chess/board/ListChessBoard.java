package de.schoenfeld.chess.board;

import de.schoenfeld.chess.model.ChessBoardBounds;
import de.schoenfeld.chess.model.ChessPiece;
import de.schoenfeld.chess.model.PieceType;
import de.schoenfeld.chess.model.Position;

import java.util.*;
import java.util.stream.IntStream;

public record ListChessBoard(
        List<ChessPiece> pieces,
        ChessBoardBounds bounds
) implements ImmutableChessBoard {

    public ListChessBoard {
        Objects.requireNonNull(pieces, "Pieces list cannot be null");
        Objects.requireNonNull(bounds, "Bounds cannot be null");
        pieces = List.copyOf(pieces);
        validateBoardSize();
    }

    private void validateBoardSize() {
        int expectedSize = bounds.rows() * bounds.columns();
        if (pieces.size() != expectedSize) {
            throw new IllegalArgumentException(
                    "Invalid pieces list size. Expected: " + expectedSize +
                            ", Actual: " + pieces.size()
            );
        }
    }

    private int calculateIndex(Position position) {
        if (!bounds.contains(position)) {
            throw new IndexOutOfBoundsException("Position out of bounds: " + position);
        }
        return position.x() + position.y() * bounds.columns();
    }

    @Override
    public ChessPiece getPieceAt(Position position) {
        return pieces.get(calculateIndex(position));
    }

    @Override
    public Position getPiecePosition(ChessPiece chessPiece) {
        int index = IntStream.range(0, pieces.size())
                .filter(i -> chessPiece.equals(pieces.get(i)))
                .findFirst()
                .orElse(-1);

        if (index == -1) return null;

        int y = index / bounds.columns();
        int x = index % bounds.columns();
        return new Position(x, y);
    }

    @Override
    public ChessBoardBounds getBounds() {
        return bounds;
    }

    @Override
    public List<ChessPiece> getPieces(boolean isWhite) {
        return pieces.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.isWhite() == isWhite)
                .toList();
    }

    @Override
    public List<ChessPiece> getPiecesOfType(PieceType pieceType, boolean colour) {
        return pieces.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getPieceType() == pieceType && p.isWhite() == colour)
                .toList();
    }

    @Override
    public ImmutableChessBoard withPieceAt(ChessPiece piece, Position position) {
        List<ChessPiece> newPieces = new ArrayList<>(pieces);
        newPieces.set(calculateIndex(position), piece);
        return new ListChessBoard(newPieces, bounds);
    }

    @Override
    public ImmutableChessBoard withoutPieceAt(Position position) {
        List<ChessPiece> newPieces = new ArrayList<>(pieces);
        newPieces.set(calculateIndex(position), null);
        return new ListChessBoard(newPieces, bounds);
    }

    @Override
    public ImmutableChessBoard withPieceMoved(Position from, Position to) {
        ChessPiece piece = getPieceAt(from);
        List<ChessPiece> newPieces = new ArrayList<>(pieces);
        newPieces.set(calculateIndex(from), null);
        newPieces.set(calculateIndex(to), piece);
        return new ListChessBoard(newPieces, bounds);
    }

    @Override
    public ImmutableChessBoard withAllPieces(Map<Position, ChessPiece> pieces) {
        List<ChessPiece> newPieces = createEmptyList();
        pieces.forEach((pos, piece) ->
                newPieces.set(calculateIndex(pos), piece)
        );
        return new ListChessBoard(newPieces, bounds);
    }

    @Override
    public ImmutableChessBoard withoutPieces() {
        return new ListChessBoard(createEmptyList(), bounds);
    }

    @Override
    public ImmutableChessBoard withBounds(ChessBoardBounds newBounds) {
        List<ChessPiece> newPieces = new ArrayList<>(
                Collections.nCopies(newBounds.rows() * newBounds.columns(), null)
        );

        IntStream.range(0, Math.min(bounds.rows(), newBounds.rows()))
                .forEach(y -> IntStream.range(0, Math.min(bounds.columns(), newBounds.columns()))
                        .forEach(x -> {
                            Position pos = new Position(x, y);
                            if (newBounds.contains(pos)) {
                                newPieces.set(pos.x() + pos.y() * newBounds.columns(), getPieceAt(pos));
                            }
                        }));

        return new ListChessBoard(newPieces, newBounds);
    }

    @Override
    public String toFen() {
        StringBuilder fen = new StringBuilder();
        for (int y = bounds.rows() - 1; y >= 0; y--) {
            int emptyCounter = 0;

            for (int x = 0; x < bounds.columns(); x++) {
                ChessPiece piece = getPieceAt(new Position(x, y));

                if (piece == null) {
                    emptyCounter++;
                } else {
                    if (emptyCounter > 0) {
                        fen.append(emptyCounter);
                        emptyCounter = 0;
                    }
                    fen.append(pieceToFenChar(piece));
                }
            }

            if (emptyCounter > 0) fen.append(emptyCounter);
            if (y > 0) fen.append('/');
        }
        return fen.toString();
    }

    private String pieceToFenChar(ChessPiece piece) {
        String base = piece.getPieceType().symbol();
        return piece.isWhite() ? base.toUpperCase() : base;
    }

    private List<ChessPiece> createEmptyList() {
        return new ArrayList<>(Collections.nCopies(
                bounds.rows() * bounds.columns(),
                null
        ));
    }
}