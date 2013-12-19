package com.fisherevans.fizzics.components;

public enum Side {
    West, North, East, South;

    public Side getOppsite() {
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

    public boolean isHorizontal() {
        return this == West || this == East;
    }

    public boolean isVertical() {
        return this == North || this == South;
    }
}
