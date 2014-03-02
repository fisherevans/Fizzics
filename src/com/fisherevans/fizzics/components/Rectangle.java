package com.fisherevans.fizzics.components;

import java.util.ArrayList;
import java.util.List;

import com.fisherevans.fizzics.listeners.CollisionListener;
import com.fisherevans.fizzics.listeners.IntersectionListener;

/**
 * @author Fisher Evans
 * Date: 12/16/13
 */
public class Rectangle {
    private Vector _bottomLeft, _velocity, _acceleration;
    private float _width, _height;

    private float _restitution = 0f;
    private float _friction = 0f;
    private boolean _static = false;
    private boolean _solid = true;
    private boolean _resolveWithStaticOnly = false;

    private Side _wall = null;
    private Side _floor = null;

    private Object _object;

    private List<CollisionListener> _collisionListeners = null;

    private List<IntersectionListener> _intersectionListeners = null;

    /**
     * creates a new rectangle with the given properties
     * By default:
     * velocity - the start velocity of the rectangle -  0, 0
     * acceleration - the start acceleration of this rectangle - 0, 0
     * restitution - the restitution of this rectangle (for velocity resolutions of collisions) - 0
     * friction - the friction this rectangle applies to other rectangles sliding on it - 0
     * isStatic - if the static shouldn't move - false
     * solid - if it should resolve collisions - true
     * @param bottomLeftX the bottom left x of this rect
     * @param bottomLeftY the bottom left y of this rect
     * @param width the width of this rect
     * @param height the height of this rect
     */
    public Rectangle(float bottomLeftX, float bottomLeftY, float width, float height) {
        this(new Vector(bottomLeftX, bottomLeftY), width, height, false);
    }

    /**
     * creates a new rectangle with the given properties
     * By default:
     * velocity - the start velocity of the rectangle -  0, 0
     * acceleration - the start acceleration of this rectangle - 0, 0
     * restitution - the restitution of this rectangle (for velocity resolutions of collisions) - 0
     * friction - the friction this rectangle applies to other rectangles sliding on it - 0
     * solid - if it should resolve collisions - true
     * @param bottomLeftX the bottom left x of this rect
     * @param bottomLeftY the bottom left y of this rect
     * @param width the width of this rect
     * @param height the height of this rect
     * @param isStatic if the static shouldn't move
     */
    public Rectangle(float bottomLeftX, float bottomLeftY, float width, float height, boolean isStatic) {
        this(new Vector(bottomLeftX, bottomLeftY), width, height, isStatic);
    }

    /**
     * creates a new rectangle with the given properties
     * By default:
     * velocity - the start velocity of the rectangle -  0, 0
     * acceleration - the start acceleration of this rectangle - 0, 0
     * restitution - the restitution of this rectangle (for velocity resolutions of collisions) - 0
     * friction - the friction this rectangle applies to other rectangles sliding on it - 0
     * solid - if it should resolve collisions - true
     * @param bottomLeft the bottom left point of this rect
     * @param width the width of this rect
     * @param height the height of this rect
     * @param isStatic if the static shouldn't move
     */
    public Rectangle(Vector bottomLeft, float width, float height, boolean isStatic) {
        _bottomLeft = bottomLeft;
        _width = width;
        _height = height;
        _static = isStatic;
        _velocity = new Vector(0, 0);
        _acceleration = new Vector(0, 0);
    }

    /**
     * creates a new rectangle with the given properties
     * @param bottomLeft the bottom left point of this rect
     * @param velocity the start velocity of the rectangle
     * @param acceleration the start acceleration of this rectangle
     * @param width the width of this rect
     * @param height the height of this rect
     * @param restitution the restitution of this rectangle (for velocity resolutions of collisions)
     * @param friction the friction this rectangle applies to other rectangles sliding on it
     * @param isStatic if the static shouldn't move
     * @param solid if it should resolve collisions
     */
    public Rectangle(Vector bottomLeft, Vector velocity, Vector acceleration, float width, float height, float restitution, float friction, boolean isStatic, boolean solid) {
        _bottomLeft = bottomLeft;
        _velocity = velocity;
        _acceleration = acceleration;
        _width = width;
        _height = height;
        _restitution = restitution;
        _friction = friction;
        _static = isStatic;
        _solid = solid;
    }

    public Rectangle(String definition) throws Exception {
        String[] props = definition.split(",");
        _bottomLeft = new Vector(Float.parseFloat(props[0]), Float.parseFloat(props[1]));
        _velocity = new Vector(Float.parseFloat(props[2]), Float.parseFloat(props[3]));
        _acceleration = new Vector(Float.parseFloat(props[4]), Float.parseFloat(props[5]));
        _width = Float.parseFloat(props[6]);
        _height = Float.parseFloat(props[7]);
        _restitution = Float.parseFloat(props[8]);
        _friction = Float.parseFloat(props[9]);
        _static = props[10].equals("true");
        _solid = props[11].equals("true");
    }

    public String getStringDefinition() {
        return String.format("%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%s,%s",
                _bottomLeft.getX(), _bottomLeft.getY(),
                _velocity.getX(), _velocity.getY(),
                _acceleration.getX(), _acceleration.getY(),
                _width, _height,
                _restitution, _friction,
                _static ? "true" : " false",
                _solid ? "true" : " false");
    }

    /**
     * checks whether a given point is inside this rectangle
     * @param p the point to check
     * @return true if the point is INSIDE the rectangle, false if on border or outside of it
     */
    public boolean contains(Vector p) {
        return p.getX() > getX1()
            && p.getX() < getX2()
            && p.getY() < getY1()
            && p.getY() > getY2();
    }

    /**
     * checks whether another rectangle intersects this rectangle
     * @param rec the rectangle to check
     * @return true if they intersect
     */
    public boolean intersects(Rectangle rec) {
        return inProximity(rec, 0);
    }

    /**
     * checks if another rectangle is within a certain proximity of this rectangle
     * @param rec the rectangle to check
     * @param proximity the area around this rectangle to check for intersection
     * @return true if the rectangle is in proximity
     */
    public boolean inProximity(Rectangle rec, float proximity) {
        if (getX2() + proximity <= rec.getX1()) return false;
        if (getX1() - proximity >= rec.getX2()) return false;
        if (getY2() - proximity >= rec.getY1()) return false;
        if (getY1() + proximity <= rec.getY2()) return false;
        return true;
    }

    /**
     * get the side another rectangle resides on of this rectangle - cannot be intersecting
     * @param rec the rectangle to check
     * @return the side the given rectangle is on
     */
    public Side getSide(Rectangle rec) {
        float xDiff = getCenterX() - rec.getCenterX();
        float yDiff = getCenterY() - rec.getCenterY();
        if (Math.abs(xDiff) - (getWidth() + rec.getWidth()) / 2f > Math.abs(yDiff) - (getHeight() + rec.getHeight()) / 2f) {
            if (xDiff < 0)
                return Side.West;
            else
                return Side.East;
        } else {
            if (yDiff < 0)
                return Side.South;
            else
                return Side.North;
        }
    }

    /**
     * gets the area of the intersection of this and the given rectangle. must intersect
     * @param rect the rectangle to check
     * @return the area of the intersection
     */
    public float getIntersectionArea(Rectangle rect) {
        float width, height;
        
        if(getX2() > rect.getX1()) width = getX2() - rect.getX1();
        else width = rect.getX2() - getX1();
        
        if (getY2() < rect.getY1()) height = getY1() - rect.getY2();
        else height = rect.getY1() - getY2();
        
        return width * height;
    }

    /**
     * move this rectangle a given amount
     * @param m the vector to move this rectangle
     */
    public void move(Vector m) {
        _bottomLeft.add(m);
    }

    /**
     * given gravity and a time delta, traverse this rectangle through space
     * @param gravity the gravity to apply to this rectangle
     * @param delta the time delta to iterate by
     */
    public void travel(Vector gravity, float delta) {
        _velocity.add(_acceleration.getCopy().add(gravity).scale(delta));
        move(_velocity.getCopy().scale(delta));
        _floor = null;
        _wall = null;
    }

    /**
     * apply an amount of friction to this rectangle slowing it down
     * @param frictionShift the amount to slow both the x and y velocities by
     */
    public void applyFriction(float frictionShift) {
        if (Math.abs(_velocity.getX()) < frictionShift) _velocity.setX(0);
        else _velocity.setX(_velocity.getX() - (Math.signum(_velocity.getX()) * frictionShift));

        if (Math.abs(_velocity.getY()) < frictionShift) _velocity.setY(0);
        else _velocity.setY(_velocity.getY() - (Math.signum(_velocity.getY()) * frictionShift));
    }

    /**
     * get the x value of the center of this rectangle
     * @return the x value of the center
     */
    public float getCenterX() {
        return _bottomLeft.getX() + _width / 2f;
    }

    /**
     * get the y value of the center of this rectangle
     * @return the y value of the center
     */
    public float getCenterY() {
        return _bottomLeft.getY() + _height / 2f;
    }

    /**
     * @return the x value of the top left of this rectangle
     */
    public float getX1() {
        return _bottomLeft.getX();
    }

    /**
     * @return the x value of the bottom right of this rectangle
     */
    public float getX2() {
        return _bottomLeft.getX() + _width;
    }

    /**
     * @return the y value of the top left of this rectangle
     */
    public float getY1() {
        return _bottomLeft.getY() + _height;
    }

    /**
     * @return the y value of the bottom right of this rectangle
     */
    public float getY2() {
        return _bottomLeft.getY();
    }

    /**
     * adds a local collision listener to this rectangle
     * @param newListener the new listener
     */
    public void addCollisionListener(CollisionListener newListener) {
        if(_collisionListeners == null)
            _collisionListeners = new ArrayList<CollisionListener>(5);
        _collisionListeners.add(newListener);
    }

    /**
     * removes a local collision listener from this rectangle
     * @param oldListener the old listener
     */
    public void removeCollisionListener(CollisionListener oldListener) {
        if (_collisionListeners != null)
            _collisionListeners.remove(oldListener);
    }

    /**
     * calls all local collision listeners tied to this rectangle
     * @param incoming the rectangle that hit it
     * @param fromDirection the direction from which it was hit.
     */
    public void callCollisionListeners(Rectangle incoming, Side fromDirection) {
        if (_collisionListeners == null)
            return;
        for (CollisionListener listener : _collisionListeners)
            listener.collision(this, incoming, fromDirection);
    }

    /**
     * adds a local intersection listener to this rectangle
     * @param newListener the new listener
     */
    public void addIntersectionListener(IntersectionListener newListener) {
        if(_intersectionListeners == null)
            _intersectionListeners = new ArrayList<IntersectionListener>(5);
        _intersectionListeners.add(newListener);
    }

    /**
     * removes a local intersection listener from this rectangle
     * @param oldListener the old listener
     */
    public void removeIntersectionListener(IntersectionListener oldListener) {
        if (_intersectionListeners != null)
            _intersectionListeners.remove(oldListener);
    }

    /**
     * calls all local intersection listeners tied to this rectangle
     * @param incoming the rectangle that hit it
     */
    public void callIntersectionListeners(Rectangle incoming) {
        if (_intersectionListeners == null)
            return;
        for (IntersectionListener listener : _intersectionListeners)
            listener.intersection(this, incoming);
    }

    /**
     * creates a new instance of this rectangle which is identical
     * @return the copy
     */
    public Rectangle getCopy() {
        Rectangle rect = new Rectangle(getBottomLeft().getCopy(),
                getVelocity().getCopy(), getAcceleration().getCopy(),
                getWidth(), getHeight(), getRestitution(), getFriction(),
                isStatic(), isSolid());
        return rect;
    }

    @Override
    public String toString() {
        return String.format("[X:%4.2f, Y:%4.2f, W:%4.2f, H:%4.2f, R:%4.2f, S:%s, C:%s, F:%4.2f]",
                _bottomLeft.getX(), _bottomLeft.getY(), _width, _height,
                _restitution, _static ? "T" : "F", _solid ? "T" : "F", _friction);
    }

    public Vector getBottomLeft() {
        return _bottomLeft;
    }

    public void setBottomLeft(Vector bottomLeft) {
        _bottomLeft = bottomLeft;
    }

    public Vector getVelocity() {
        return _velocity;
    }

    public void setVelocity(Vector velocity) {
        _velocity = velocity;
    }

    public float getWidth() {
        return _width;
    }

    public void setWidth(float width) {
        _width = width;
    }

    public float getHeight() {
        return _height;
    }

    public void setHeight(float height) {
        _height = height;
    }

    public boolean isStatic() {
        return _static;
    }

    public void setStatic(boolean isStatic) {
        _static = isStatic;
    }

    public float getRestitution() {
        return _restitution;
    }

    public void setRestitution(float restitution) {
        _restitution = restitution;
    }

    public void setSolid(boolean isSolid) {
        _solid = isSolid;
    }

    public boolean isSolid() {
        return _solid;
    }

    public float getFriction() {
        return _friction;
    }

    public void setFriction(float friction) {
        _friction = friction;
    }

    public Side getWall() {
        return _wall;
    }

    public void setWall(Side wall) {
        _wall = wall;
    }

    public Side getFloor() {
        return _floor;
    }

    public void setFloor(Side floor) {
        _floor = floor;
    }

    public Vector getAcceleration() {
        return _acceleration;
    }

    public void setAcceleration(Vector acceleration) {
        _acceleration = acceleration;
    }

    public boolean isResolveWithStaticOnly() {
        return _resolveWithStaticOnly;
    }

    public void setResolveWithStaticOnly(boolean resolveWithStaticOnly) {
        _resolveWithStaticOnly = resolveWithStaticOnly;
    }

    public Object getObject() {
        return _object;
    }

    public void setObject(Object object) {
        _object = object;
    }
}
