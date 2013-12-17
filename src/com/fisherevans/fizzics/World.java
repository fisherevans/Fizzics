package com.fisherevans.fizzics;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class World {
    private Vector _gravity;
    private List<Rectangle> _rectangles;
    private List<CollisionListener> _listeners;

    public World(Vector gravity) {
        _gravity = gravity;
        _rectangles = new ArrayList<Rectangle>();
        _listeners = new ArrayList<CollisionListener>();
    }

    public void addRectangle(Rectangle newRec) {
        _rectangles.add(newRec);
    }

    public void step(float delta) {
        Vector gravityVector = _gravity.getCopy().scale(delta);

        Rectangle beforeStep; // allocate loop variables
        float dx, dy, shift;
        Vector thisV, otherV;
        for(Rectangle rectangle:_rectangles) {
            if(!rectangle.isStatic() && rectangle.isCollidable()) { // for each non static rectangle
                beforeStep = rectangle.getCopy(); // keep a copy of the position before movement
                rectangle.move(rectangle.getVelocity().add(gravityVector).getCopy().scale(delta));
                for(Rectangle otherRectangle:_rectangles) { // check for collisions
                    if (otherRectangle != rectangle && rectangle.intersects(otherRectangle)) {
                        for (CollisionListener listener : _listeners) // call listener
                            listener.collision(rectangle, otherRectangle);
                        if(otherRectangle.isCollidable()) {
                            dx = otherRectangle.getCenterX() - beforeStep.getCenterX(); // get the center offset of the two colliding rects
                            dy = otherRectangle.getCenterY() - beforeStep.getCenterY();
                            if (Math.abs(dx) > Math.abs(dy)) { // side to side
                                shift = (rectangle.getWidth() + otherRectangle.getWidth()) / 2f; // min distance between to centers
                                if (dx > 0) { // this rect from the left
                                    shift = otherRectangle.getCenterX() - rectangle.getCenterX() - shift;
                                } else { // this rect from the right
                                    shift = shift - rectangle.getCenterX() + otherRectangle.getCenterX();
                                }
                                rectangle.move(new Vector(shift, 0));
                                if (otherRectangle.isStatic())
                                    rectangle.getVelocity().multiply(new Vector(-1f * rectangle.getRestitution(), 1f));
                                else {
                                    thisV = rectangle.getVelocity().getCopy().scale(otherRectangle.getRestitution());
                                    otherV = otherRectangle.getVelocity().getCopy().scale(rectangle.getRestitution());
                                    rectangle.getVelocity().setX(otherV.getX());
                                    otherRectangle.getVelocity().setX(thisV.getX());
                                }
                            } else { // this rect up and down
                                shift = (rectangle.getHeight() + otherRectangle.getHeight()) / 2f; // min distance between to centers
                                if (dy > 0) { // from the bottom
                                    shift = otherRectangle.getCenterY() - rectangle.getCenterY() - shift;
                                } else { // this rect from the top
                                    shift = shift - rectangle.getCenterY() + otherRectangle.getCenterY();
                                }
                                rectangle.move(new Vector(0, shift));
                                if (otherRectangle.isStatic())
                                    rectangle.getVelocity().multiply(new Vector(1f, -1f * rectangle.getRestitution()));
                                else {
                                    thisV = rectangle.getVelocity().getCopy().scale(otherRectangle.getRestitution());
                                    otherV = otherRectangle.getVelocity().getCopy().scale(rectangle.getRestitution());
                                    rectangle.getVelocity().setY(otherV.getY());
                                    otherRectangle.getVelocity().setY(thisV.getY());
                                }
                            }
                        }
                    } // end if not same as outer and does collide
                } // inner loop of rectangles
            } // end if not static
        } // outer loops of rectangles
    }

    public List<Rectangle> getRectangles() {
        return _rectangles;
    }

    public void addCollisionListener(CollisionListener listener) {
        _listeners.add(listener);
    }

    public void removeCollisionListener(CollisionListener listener) {
        _listeners.remove(listener);
    }
}
