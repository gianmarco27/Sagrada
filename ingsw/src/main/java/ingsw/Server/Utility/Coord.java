package ingsw.Server.Utility;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable class for holding coordinates values
 */
public final class Coord implements Serializable {

    public final int x;
    public final int y;

    /**
     * Set the initial (and final) values for the X and Y
     */
    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object toCompare) {
        if (toCompare == null)
            return false;
        if (!(toCompare instanceof Coord))
            return false;
        Coord other = (Coord) toCompare;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return x + "-" + y; // 3-4
    }
}
