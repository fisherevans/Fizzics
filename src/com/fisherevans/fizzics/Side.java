package com.fisherevans.fizzics;

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
        return North; // never called
    }
}
