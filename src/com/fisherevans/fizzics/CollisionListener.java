package com.fisherevans.fizzics;

public interface CollisionListener {
    public abstract void collision(Rectangle thisRectangle, Rectangle incommingRectangle, Side fromDirection);
}
