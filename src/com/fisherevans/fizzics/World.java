package com.fisherevans.fizzics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class World {
    private Vector _gravity;
    private List<Rectangle> _rectangles, _rectanglesDeleteQueue, _rectanglesAddQueue;
    private List<GlobalCollisionListener> _listeners;

    public World(Vector gravity) {
        _gravity = gravity;
        _rectangles = new ArrayList<Rectangle>();
        _rectanglesAddQueue = new LinkedList<Rectangle>();
        _rectanglesDeleteQueue = new LinkedList<Rectangle>();
        _listeners = new ArrayList<GlobalCollisionListener>();
    }

    public void addRectangle(Rectangle newRec) {
        _rectanglesAddQueue.add(newRec);
    }

    public void removeRectangle(Rectangle oldRec) {
        _rectanglesDeleteQueue.add(oldRec);
    }

    public List<Rectangle> getRectangles() {
        return _rectangles;
    }

    public void step(float delta) {
        runRectangleQueues();

        Vector gravityVector = _gravity.getCopy().scale(delta);

        Rectangle r1Before; // allocate loop variables
        boolean isVerticalHit;
        float r1v, r2v;
        for(Rectangle r1:_rectangles) {
            if (!r1.isStatic() && r1.isCollidable()) { // for each non static rectangle
                r1Before = r1.getCopy(); // keep a copy of the position before movement
                r1.getVelocity().add(gravityVector);
                r1.travel(delta);
                for (Rectangle r2 : _rectangles) { // check for collisions
                    if (r2 != r1 && r1.intersects(r2)) {
                        resolveCollision(r1Before, r1, r2, delta);
                    } // end if not same as outer and does collide
                } // inner loop of rectangles
            } // end if not static
        } // outer loops of rectangles
    }
    
    private void resolveCollision(Rectangle r1Before, Rectangle r1, Rectangle r2, float delta) {
        callGlobalCollisionListeners(r1, r2);

        if(r2.isCollidable()) { // !!!! ---> R1 is moving rectangle, R2 is the one it's hitting <--- !!!!
            r1.applyFriction(r2.getFriction() * delta);
            if (!r2.isStatic())
                r2.applyFriction(r1.getFriction() * delta);

            boolean isVerticalHit;
            if (r1Before.getX1() >= r2.getX2()) { // from right
                r1.move(new Vector(r2.getX2() - r1.getX1(), 0));
                isVerticalHit = false;
            } else if (r1Before.getX2() <= r2.getX1()) { // from left
                r1.move(new Vector(r2.getX1() - r1.getX2(), 0));
                isVerticalHit = false;
            } else if (r1Before.getY1() <= r2.getY2()) { // from bottom
                r1.move(new Vector(0, r2.getY2() - r1.getY1()));
                isVerticalHit = true;
            } else if (r1Before.getY2() >= r2.getY1()) { // from top
                r1.move(new Vector(0, r2.getY1() - r1.getY2()));
                isVerticalHit = true;
            } else {
                return; // should never be called
            }

            if (isVerticalHit) { // adjust vertical velocity if hitting from top/bottom
                if (r2.isStatic()) {
                    r1.getVelocity().setY(r1.getVelocity().getY() * -1 * r1.getRestitution());
                } else {
                    float r1vy = r2.getVelocity().getY() * r1.getRestitution();
                    float r2vy = r1.getVelocity().getY() * r2.getRestitution();
                    r1.getVelocity().setY(r1vy);
                    r2.getVelocity().setY(r2vy);
                }
            } else { // adjust horizontal velocity if hitting from left/right
                if (r2.isStatic()) {
                    r1.getVelocity().setX(r1.getVelocity().getX() * -1 * r1.getRestitution());
                } else {
                    float r1vx = r2.getVelocity().getX() * r1.getRestitution();
                    float r2vx = r1.getVelocity().getX() * r2.getRestitution();
                    r1.getVelocity().setX(r1vx);
                    r2.getVelocity().setX(r2vx);
                }
            }
        } // end if collidable
    }

    private void runRectangleQueues() {
        while (!_rectanglesAddQueue.isEmpty())
            _rectangles.add(_rectanglesAddQueue.remove(0));

        while (!_rectanglesDeleteQueue.isEmpty())
            _rectangles.remove(_rectanglesDeleteQueue.remove(0));
    }

    private void callGlobalCollisionListeners(Rectangle rect1, Rectangle rect2) {
        rect1.callCollisionListners(rect2);
        rect2.callCollisionListners(rect1);
        for (GlobalCollisionListener listener : _listeners) { // call listeners
            listener.globalCollision(rect1, rect2);
        }
    }

    public void addGlobalCollisionListener(GlobalCollisionListener listener) {
        _listeners.add(listener);
    }

    public void removeGlobalCollisionListener(GlobalCollisionListener listener) {
        _listeners.remove(listener);
    }
}
