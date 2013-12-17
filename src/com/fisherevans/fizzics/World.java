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
    private List<GlobalCollisionListener> _listeners;

    public World(Vector gravity) {
        _gravity = gravity;
        _rectangles = new ArrayList<Rectangle>();
        _listeners = new ArrayList<GlobalCollisionListener>();
    }

    public void addRectangle(Rectangle newRec) {
        _rectangles.add(newRec);
    }

    public void step(float delta) {
        Vector gravityVector = _gravity.getCopy().scale(delta);

        Rectangle beforeStep; // allocate loop variables
        boolean isVerticalHit;
        float r1v, r2v;
        for(Rectangle r1:_rectangles) {
            if(!r1.isStatic() && r1.isCollidable()) { // for each non static rectangle
                beforeStep = r1.getCopy(); // keep a copy of the position before movement
                r1.move(r1.getVelocity().add(gravityVector).getCopy().scale(delta));
                for(Rectangle r2:_rectangles) { // check for collisions
                    if (r2 != r1 && r1.intersects(r2)) {
                        for (GlobalCollisionListener listener : _listeners) { // call listeners
                            listener.globalCollision(r1, r2);
                        }
                        if(r2.isCollidable()) { // !!!! ---> R1 is moving rect, R2 is the one it's hitting <--- !!!!
                            if (beforeStep.getX1() >= r2.getX2()) { // from
                                                                    // right
                                r1.move(new Vector(r2.getX2() - r1.getX1(), 0));
                                isVerticalHit = false;
                            } else if (beforeStep.getX2() <= r2.getX1()) { // from
                                                                           // left
                                r1.move(new Vector(r2.getX1() - r1.getX2(), 0));
                                isVerticalHit = false;
                            } else if (beforeStep.getY1() <= r2.getY2()) { // from
                                                                           // bottom
                                r1.move(new Vector(0, r2.getY2() - r1.getY1()));
                                isVerticalHit = true;
                            } else if (beforeStep.getY2() >= r2.getY1()) { // from
                                                                           // top
                                r1.move(new Vector(0, r2.getY1() - r1.getY2()));
                                isVerticalHit = true;
                            } else {
                                continue; // should never be called
                            }
                            if (isVerticalHit) { // adjust vertical velocity if hitting from top/bottom
                                if (r2.isStatic()) {
                                    r1.getVelocity().setY(r1.getVelocity().getY() * -1 * r1.getRestitution());
                                } else {
                                    r1v = r2.getVelocity().getY() * r1.getRestitution();
                                    r2v = r1.getVelocity().getY() * r2.getRestitution();
                                    r1.getVelocity().setY(r1v);
                                    r2.getVelocity().setY(r2v);
                                }
                            } else { // adjust horizontal velocity if hitting from left/right
                                if (r2.isStatic()) {
                                    r1.getVelocity().setX(r1.getVelocity().getX() * -1 * r1.getRestitution());
                                } else {
                                    r1v = r2.getVelocity().getX() * r1.getRestitution();
                                    r2v = r1.getVelocity().getX() * r2.getRestitution();
                                    r1.getVelocity().setX(r1v);
                                    r2.getVelocity().setX(r2v);
                                }
                            }
                        } // end if collidable
                    } // end if not same as outer and does collide
                } // inner loop of rectangles
            } // end if not static
        } // outer loops of rectangles
    }

    public List<Rectangle> getRectangles() {
        return _rectangles;
    }

    public void addGlobalCollisionListener(GlobalCollisionListener listener) {
        _listeners.add(listener);
    }

    public void removeGlobalCollisionListener(GlobalCollisionListener listener) {
        _listeners.remove(listener);
    }
}
