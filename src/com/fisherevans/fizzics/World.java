package com.fisherevans.fizzics;

import java.util.*;

import com.fisherevans.fizzics.components.Rectangle;
import com.fisherevans.fizzics.components.RectangleSorter;
import com.fisherevans.fizzics.components.Side;
import com.fisherevans.fizzics.components.Vector;
import com.fisherevans.fizzics.listeners.GlobalCollisionListener;
import com.fisherevans.fizzics.listeners.GlobalIntersectionListener;

/**
 * A simple 2D physics world that updates rectangle positions based on time and colliding rectangles
 * @author Fisher Evans
 * Date: 12/16/13
 */
public class World {
    private Vector _gravity;
    private Comparator<Rectangle> _rectangleComparator;
    private PriorityQueue<Rectangle> _rectangles;
    private List<Rectangle> _rectanglesDeleteQueue, _rectanglesAddQueue;
    private List<GlobalCollisionListener> _collisionListeners;
    private List<GlobalIntersectionListener> _intersectionListeners;
    private int _iterations = 1;

    /**
     * Creates the world starting with a supplied gravity.
     * @param gravity the y acceleration for non static objects
     */
    public World(float gravity) {
        setGravity(gravity);
        _rectangleComparator = new RectangleSorter(this);
        _rectangles = new PriorityQueue<Rectangle>(50, _rectangleComparator);
        _rectanglesAddQueue = new LinkedList<Rectangle>();
        _rectanglesDeleteQueue = new LinkedList<Rectangle>();
        _collisionListeners = new LinkedList<GlobalCollisionListener>();
        _intersectionListeners = new LinkedList<GlobalIntersectionListener>();
    }

    /**
     * Adds a rectangle to the addition queue. The rectangle will not be added to the world until the next step();
     * @param newRec the new rectangle
     */
    public void add(Rectangle newRec) {
        _rectanglesAddQueue.add(newRec);
    }

    /**
     * Adds a rectangle to the deletion queue. The rectangle will not be removed from the world until the next step();
     * @param oldRec the old rectangle
     */
    public void remove(Rectangle oldRec) {
        _rectanglesDeleteQueue.add(oldRec);
    }

    /**
     * Gets the current list of rectangles in this world
     * @return the list of rectangles in this world
     */
    public PriorityQueue<Rectangle> getRectangles() {
        return _rectangles;
    }

    /**
     * adds and removes rectangles in queue
     * updates the rectangles' in the world velocity based on gravity
     * iterates each rectangle's position based on it's current velocity
     * detects and resolves collisions between rectangles
     * @param delta the time delta since the last step in seconds
     */
    public void step(float delta) {
        runRectangleQueues();
        CollisionQueue collisionQueue;
        List<Rectangle> r1Befores = new ArrayList<Rectangle>(_rectangles.size());
        while(r1Befores.size() < _rectangles.size()) r1Befores.add(null);

        for(int iteration = 0;iteration < _iterations;iteration++) {
            int rectId = 0;
            for(Rectangle r1:_rectangles) {
                if (!r1.isStatic() && r1.isSolid()) { // for each non static rectangle
                    if(iteration == 0) {
                        r1Befores.set(rectId, r1.getCopy()); // keep a copy of the position before movement
                        r1.travel(_gravity, delta);
                    }
                    collisionQueue = new CollisionQueue();
                    for (Rectangle r2 : _rectangles) { // check for collisions
                        if (r2 != r1 && r1.intersects(r2)) { // add to collision queue based on location
                            if(Math.abs(r1.getVelocity().getX()) > Math.abs(r1.getVelocity().getY()))
                                collisionQueue.add(r2, -r1.getVelocity().getX()*r2.getCenterX());
                            else
                                collisionQueue.add(r2, -r1.getVelocity().getY()*r2.getCenterY());
                        }
                    }
                    while (collisionQueue.size() > 0) // process each collision
                        resolveCollision(r1Befores.get(rectId), r1, collisionQueue.remove(), delta);
                } // end if not static
            } // outer loops of rectangles
            rectId++;
        }
    }

    /**
     * resolves a collision between 2 rectangles.
     * @param r1Before the position of the moving rectangle before the step
     * @param r1 the moving rectangle
     * @param r2 the rectangle getting hit
     * @param delta the time delta since the last step in seconds
     */
    private void resolveCollision(Rectangle r1Before, Rectangle r1, Rectangle r2, float delta) {
        r1.callIntersectionListeners(r2);
        if((r1.isResolveWithStaticOnly() && !r2.isStatic())||(r2.isResolveWithStaticOnly() && !r1.isStatic()))
            return;
        Side collisionDirection = r1Before.getSide(r2);
        if(r1.intersects(r2)) { // !!!! ---> R1 is moving rectangle, R2 is the one it's hitting <--- !!!!
            if(r2.isSolid()) { // if it needs resolution (not a ghost)
                r1.applyFriction(r2.getFriction() * delta);
                if (!r2.isStatic())
                    r2.applyFriction(r1.getFriction() * delta);

                if(collisionDirection == Side.East) { // move r1 out of collision
                    r1.move(new Vector(r2.getX2() - r1.getX1(), 0));
                    r1.getVelocity().setX(r1.getVelocity().getX() - (r1.getX1() - r2.getX2()));
                } else if(collisionDirection == Side.West) {
                    r1.move(new Vector(r2.getX1() - r1.getX2(), 0));
                    r1.getVelocity().setX(r1.getVelocity().getX() - (r1.getX2() - r2.getX1()));
                } else if(collisionDirection == Side.North) {
                    r1.move(new Vector(0, r2.getY1() - r1.getY2()));
                    r1.getVelocity().setY(r1.getVelocity().getY() - (r2.getY2() - r1.getY1()));
                } else if(collisionDirection == Side.South) {
                    r1.move(new Vector(0, r2.getY2() - r1.getY1()));
                    r1.getVelocity().setY(r1.getVelocity().getY() - (r2.getY1() - r1.getY2()));
                }

                if(r2.isStatic()) { // update velocity after collision
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
            callGlobalCollisionListeners(r1, r2); // call any added listeners
            r1.callCollisionListeners(r2, collisionDirection.getOpposite());
            r2.callCollisionListeners(r1, collisionDirection);
        }

        if(collisionDirection.isVertical()) { // update the rectangles' floor and wall values
            r2.setFloor(collisionDirection);
            r1.setFloor(collisionDirection.getOpposite());
        } else {
            r2.setWall(collisionDirection);
            r1.setWall(collisionDirection.getOpposite());
        }
    }

    /**
     * changes this world's gravity
     * @param gravity the new y acceleration
     */
    public void setGravity(float gravity) {
        _gravity = new Vector(0, gravity);
    }

    /**
     * adds and removes rectanges to this world based on the current queue
     */
    private void runRectangleQueues() {
        while (!_rectanglesAddQueue.isEmpty())
            _rectangles.add(_rectanglesAddQueue.remove(0));

        while (!_rectanglesDeleteQueue.isEmpty())
            _rectangles.remove(_rectanglesDeleteQueue.remove(0));
    }

    /**
     * calls all global listeners for a given collection
     * @param rect1 one of the rectangles
     * @param rect2 the other
     */
    private void callGlobalCollisionListeners(Rectangle rect1, Rectangle rect2) {
        for (GlobalCollisionListener listener : _collisionListeners) { // call listeners
            listener.globalCollision(rect1, rect2);
        }
    }

    /**
     * adds a new global collision listener
     * @param listener the new listener
     */
    public void addGlobalCollisionListener(GlobalCollisionListener listener) {
        _collisionListeners.add(listener);
    }

    /**
     * removes a collision listener
     * @param listener the old collision listener
     */
    public void removeGlobalCollisionListener(GlobalCollisionListener listener) {
        _collisionListeners.remove(listener);
    }

    /**
     * calls all global listeners for a given intersection
     * @param rect1 one of the rectangles
     * @param rect2 the other
     */
    private void callGlobalIntersectionListeners(Rectangle rect1, Rectangle rect2) {
        for (GlobalIntersectionListener listener : _intersectionListeners) { // call listeners
            listener.globalIntersection(rect1, rect2);
        }
    }

    /**
     * adds a new global intersection listener
     * @param listener the new listener
     */
    public void addGlobalIntersectionListener(GlobalIntersectionListener listener) {
        _intersectionListeners.add(listener);
    }

    /**
     * removes a intersection listener
     * @param listener the old intersection listener
     */
    public void removeGlobalIntersectionListener(GlobalIntersectionListener listener) {
        _intersectionListeners.remove(listener);
    }

    public int getIterations() {
        return _iterations;
    }

    public void setIterations(int iterations) {
        _iterations = iterations;
    }

    public float getGravity() {
        return _gravity.getY();
    }
}
