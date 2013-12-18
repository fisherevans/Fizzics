package com.fisherevans.fizzics.listeners;

import com.fisherevans.fizzics.components.Rectangle;

public interface GlobalCollisionListener {
    public void globalCollision(Rectangle rect1, Rectangle rect2);
}
