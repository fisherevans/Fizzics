package com.fisherevans.fizzics.listeners;

import com.fisherevans.fizzics.components.Rectangle;
import com.fisherevans.fizzics.components.Side;

public interface IntersectionListener {
    public abstract void intersection(Rectangle thisRectangle, Rectangle incomingRectangle);
}
