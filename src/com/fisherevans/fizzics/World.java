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
    public static float SIDE_PROXIMITY = 0.01f;
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

        Rectangle r1Before;
        for(Rectangle r1:_rectangles) {
            if (!r1.isStatic() && r1.isSolid()) { // for each non static rectangle
                r1Before = r1.getCopy(); // keep a copy of the position before movement
                r1.travel(_gravity, delta);
                for (Rectangle r2 : _rectangles) { // check for collisions
                    if (r2 != r1 && r1.inProximity(r2, SIDE_PROXIMITY)) {
                        resolveProximity(r1Before.getSide(r2), r1, r2, delta);
                    }
                } // inner loop of rectangles
            } // end if not static
        } // outer loops of rectangles
    }
    
    private void resolveProximity(Side collisionDirection, Rectangle r1, Rectangle r2, float delta) {
        if(r1.intersects(r2)) { // !!!! ---> R1 is moving rectangle, R2 is the one it's hitting <--- !!!!
            if(r2.isSolid()) {
                r1.applyFriction(r2.getFriction() * delta);
                if (!r2.isStatic())
                    r2.applyFriction(r1.getFriction() * delta);

                if(collisionDirection == Side.East) {
                    r1.move(new Vector(r2.getX2() - r1.getX1(), 0));
                } else if(collisionDirection == Side.West) {
                    r1.move(new Vector(r2.getX1() - r1.getX2(), 0));
                } else if(collisionDirection == Side.North) {
                    r1.move(new Vector(0, r2.getY1() - r1.getY2()));
                } else if(collisionDirection == Side.South) {
                    r1.move(new Vector(0, r2.getY2() - r1.getY1()));
                }

                if(r2.isStatic()) {
                    if(collisionDirection.isVertical()) {
                        r1.getVelocity().setY(r1.getVelocity().getY() * -1 * r1.getRestitution());
                    } else {
                        r1.getVelocity().setX(r1.getVelocity().getX() * -1 * r1.getRestitution());
                    }
                } else {
                    if(collisionDirection.isVertical()) {
                        float r1vy = r2.getVelocity().getY() * r1.getRestitution();
                        float r2vy = r1.getVelocity().getY() * r2.getRestitution();
                        r1.getVelocity().setY(r1vy);
                        r2.getVelocity().setY(r2vy);
                    } else {
                        float r1vx = r2.getVelocity().getX() * r1.getRestitution();
                        float r2vx = r1.getVelocity().getX() * r2.getRestitution();
                        r1.getVelocity().setX(r1vx);
                        r2.getVelocity().setX(r2vx);
                    }
                }
            }
            callGlobalCollisionListeners(r1, r2);
            r1.callCollisionListners(r2, collisionDirection.getOppsite());
            r2.callCollisionListners(r1, collisionDirection);
        }

        if(collisionDirection.isVertical()) {
            r2.setFloor(collisionDirection);
            r1.setFloor(collisionDirection.getOppsite());
        } else {
            r2.setWall(collisionDirection);
            r1.setWall(collisionDirection.getOppsite());
        }
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
