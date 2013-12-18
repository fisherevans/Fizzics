package com.fisherevans.fizzics.listeners;

import com.fisherevans.fizzics.components.Rectangle;
import com.fisherevans.fizzics.components.Side;

public interface CollisionListener {
    public abstract void collision(Rectangle thisRectangle, Rectangle incommingRectangle, Side fromDirection);
}
