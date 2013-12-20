package com.fisherevans.fizzics.components;

/**
 * A simple direction
 * @author Fisher Evans
 */
public enum Side {
    West, North, East, South;

    /**
     * gets the opposite direction of this one
     * @return the opposite direction
     */
    public Side getOpposite() {
        switch (this) {
        case West:
            return East;
        case East:
            return West;
        case North:
            return South;
        case South:
            return North;
        }
        return null; // never called
    }

    /**
     * @return if this is West or East
     */
    public boolean isHorizontal() {
        return this == West || this == East;
    }

    /**
     * @return if this is North or South
     */
    public boolean isVertical() {
        return this == North || this == South;
    }
}
