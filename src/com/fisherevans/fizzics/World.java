package com.fisherevans.fizzics;

import java.util.ArrayList;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class World {
    private Vector _gravity;
    private ArrayList<Rectangle> _rectangles;

    public World(Vector gravity) {
        _gravity = gravity;
        _rectangles = new ArrayList<Rectangle>();
    }

    public void addRectangle(Rectangle newRec) {
        _rectangles.add(newRec);
    }

    public void step(float delta) {
        Vector gravityVector = _gravity.getCopy().scale(delta);
        for(Rectangle rectangle:_rectangles) {
            rectangle.getVelocity().add(gravityVector);
            rectangle.move(rectangle.getVelocity().getCopy().scale(delta));

        }
    }
}
