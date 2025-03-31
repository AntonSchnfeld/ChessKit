package de.schoenfeld.chesskit.move;

import de.schoenfeld.chesskit.model.ChessPiece;
import de.schoenfeld.chesskit.model.GameState;
import de.schoenfeld.chesskit.model.PieceType;
import de.schoenfeld.chesskit.model.Square;
import de.schoenfeld.chesskit.move.components.MoveComponent;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Move<T extends PieceType> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1921632465085309764L;

    // Store components in an array for space efficiency.
    private final MoveComponent<T>[] components;
    private final ChessPiece<T> movedPiece;
    private final Square from, to;

    private Move(ChessPiece<T> movedPiece, Square from, Square to, MoveComponent<T>[] components) {
        // Defensive copy (if needed) using Arrays.copyOf:
        this.components = Arrays.copyOf(components, components.length);
        this.movedPiece = movedPiece;
        this.from = from;
        this.to = to;
    }

    @SafeVarargs
    public static <T extends PieceType> Move<T> of(ChessPiece<T> movedPiece, Square from, Square to,
                                                   MoveComponent<T>... components) {
        return new Move<>(movedPiece, from, to, components);
    }

    public ChessPiece<T> movedPiece() {
        return movedPiece;
    }

    public Square from() {
        return from;
    }

    public Square to() {
        return to;
    }

    public <C extends MoveComponent<T>> C getComponent(Class<C> clazz) {
        for (int i = 0; i < components.length; i++) {
            MoveComponent<T> comp = components[i];
            if (clazz.isInstance(comp)) {
                // The cast is safe because of the check above.
                return clazz.cast(comp);
            }
        }
        return null;
    }

    public boolean hasComponent(Class<? extends MoveComponent> clazz) {
        for (int i = 0; i < components.length; i++)
            if (clazz.isInstance(components[i]))
                return true;
        return false;
    }

    public MoveComponent<T>[] getComponents() {
        return components;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Move<?> move = (Move<?>) object;
        return Objects.equals(movedPiece, move.movedPiece) &&
                Objects.equals(from, move.from) &&
                Objects.equals(to, move.to) &&
                Arrays.equals(components, move.components);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(movedPiece, from, to);
        result = 31 * result + Arrays.hashCode(components);
        return result;
    }

    @Override
    public String toString() {
        return "Move{" +
                "components=" + Arrays.toString(components) +
                ", movedPiece=" + movedPiece +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
