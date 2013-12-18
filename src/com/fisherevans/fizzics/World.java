package com.fisherevans.fizzics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fisherevans.fizzics.components.Rectangle;
import com.fisherevans.fizzics.components.Side;
import com.fisherevans.fizzics.components.Vector;
import com.fisherevans.fizzics.listeners.GlobalCollisionListener;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class World {
    private Vector _gravity;
    private List<Rectangle> _rectangles, _rectanglesDeleteQueue, _rectanglesAddQueue;
    private List<GlobalCollisionListener> _listeners;
    private Side _floor = Side.South;

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
                    }
                } // inner loop of rectangles
            } // end if not static
        } // outer loops of rectangles
    }
    
    private void resolveCollision(Rectangle r1Before, Rectangle r1, Rectangle r2, float delta) {
        Side collisonDirection = Side.North;
        if (r1Before.getX1() >= r2.getX2()) collisonDirection = Side.East;
        else if (r1Before.getX2() <= r2.getX1()) collisonDirection = Side.West;
        else if (r1Before.getY1() <= r2.getY2()) collisonDirection = Side.South;
        else if (r1Before.getY2() >= r2.getY1()) collisonDirection = Side.North;

        if(r2.isCollidable()) { // !!!! ---> R1 is moving rectangle, R2 is the one it's hitting <--- !!!!
            r1.applyFriction(r2.getFriction() * delta);
            if (!r2.isStatic())
                r2.applyFriction(r1.getFriction() * delta);

            switch (collisonDirection) {
            case East:
                r1.move(new Vector(r2.getX2() - r1.getX1(), 0));
                break;
            case West:
                r1.move(new Vector(r2.getX1() - r1.getX2(), 0));
                break;
            case North:
                r1.move(new Vector(0, r2.getY1() - r1.getY2()));
                break;
            case South:
                r1.move(new Vector(0, r2.getY2() - r1.getY1()));
                break;
            }

            if (collisonDirection == Side.North || collisonDirection == Side.South) { // adjust vertical velocity if hitting from top/bottom
                if (r2.isStatic())
                    r1.getVelocity().setY(r1.getVelocity().getY() * -1 * r1.getRestitution());
                else {
                    float r1vy = r2.getVelocity().getY() * r1.getRestitution();
                    float r2vy = r1.getVelocity().getY() * r2.getRestitution();
                    r1.getVelocity().setY(r1vy);
                    r2.getVelocity().setY(r2vy);
                }
                r2.setOnFloor(collisonDirection);
                r1.setOnFloor(collisonDirection.getOppsite());
            } else { // adjust horizontal velocity if hitting from left/right
                if (r2.isStatic())
                    r1.getVelocity().setX(r1.getVelocity().getX() * -1 * r1.getRestitution());
                else {
                    float r1vx = r2.getVelocity().getX() * r1.getRestitution();
                    float r2vx = r1.getVelocity().getX() * r2.getRestitution();
                    r1.getVelocity().setX(r1vx);
                    r2.getVelocity().setX(r2vx);
                }
                r2.setOnWall(collisonDirection);
                r1.setOnWall(collisonDirection.getOppsite());
            }
        } // end if collidable

        callGlobalCollisionListeners(r1, r2);
        r1.callCollisionListners(r2, collisonDirection.getOppsite());
        r2.callCollisionListners(r1, collisonDirection);
    }

    private void runRectangleQueues() {
        while (!_rectanglesAddQueue.isEmpty())
            _rectangles.add(_rectanglesAddQueue.remove(0));

        while (!_rectanglesDeleteQueue.isEmpty())
            _rectangles.remove(_rectanglesDeleteQueue.remove(0));
    }

    private void callGlobalCollisionListeners(Rectangle rect1, Rectangle rect2) {
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
