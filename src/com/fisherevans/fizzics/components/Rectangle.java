package com.fisherevans.fizzics.components;

import java.util.ArrayList;
import java.util.List;

import com.fisherevans.fizzics.listeners.CollisionListener;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Rectangle {
    private Vector _bottomLeft, _velocity, _externalVelocity;
    private float _width, _height;
    private float _restitution = 0f;
    private boolean _isStatic = false;
    private boolean _isCollidable = true;
    private float _friction = 15f;
    private Side _onWall = null;
    private Side _onFloor = null;

    private List<CollisionListener> _listeners = null;

    public Rectangle(float bottomLeftX, float bottomLeftY, float width, float height) {
        this(new Vector(bottomLeftX, bottomLeftY), width, height, false);
    }

    public Rectangle(float bottomLeftX, float bottomLeftY, float width, float height, boolean isStatic) {
        this(new Vector(bottomLeftX, bottomLeftY), width, height, isStatic);
    }

    public Rectangle(Vector bottomLeft, float width, float height, boolean isStatic) {
        _bottomLeft = bottomLeft;
        _width = width;
        _height = height;
        _isStatic = isStatic;
        _velocity = new Vector(0, 0);
        _externalVelocity = new Vector(0, 0);
    }

    public Rectangle(Vector bottomLeft, float width, float height, Vector velocity, float restitution, float friction, boolean isStatic, boolean isCollidable) {
        _bottomLeft = bottomLeft;
        _velocity = velocity;
        _width = width;
        _height = height;
        _restitution = restitution;
        _isStatic = isStatic;
        _isCollidable = isCollidable;
        _friction = friction;
    }

    public boolean contains(Vector p) {
        return p.getX() > getX1()
            && p.getX() < getX2()
            && p.getY() < getY1()
            && p.getY() > getY2();
    }

    public boolean intersects(Rectangle rec) {
        if (getX2() <= rec.getX1())
            return false;
        if (getX1() >= rec.getX2())
            return false;
        if (getY2() >= rec.getY1())
            return false;
        if (getY1() <= rec.getY2())
            return false;
        return true;
    }

    public void move(Vector m) {
        _bottomLeft.add(m);
    }

    public void travel(float delta) {
        move(_velocity.getCopy().add(_externalVelocity).scale(delta));
        _onFloor = null;
        _onWall = null;
    }

    public void applyFriction(float frictionShift) {
        if (Math.abs(_velocity.getX()) < frictionShift) _velocity.setX(0);
        else _velocity.setX(_velocity.getX() - (Math.signum(_velocity.getX()) * frictionShift));

        if (Math.abs(_velocity.getY()) < frictionShift) _velocity.setY(0);
        else _velocity.setY(_velocity.getY() - (Math.signum(_velocity.getY()) * frictionShift));
    }

  public float getCenterX() {
        return _bottomLeft.getX() + _width / 2f;
  }

  public float getCenterY() {
        return _bottomLeft.getY() + _height / 2f;
  }

    public float getX1() {
        return _bottomLeft.getX();
    }

    public float getX2() {
        return _bottomLeft.getX() + _width;
    }

    public float getY1() {
        return _bottomLeft.getY() + _height;
    }

    public float getY2() {
        return _bottomLeft.getY();
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
        return _isStatic;
    }

    public void setStatic(boolean isStatic) {
        _isStatic = isStatic;
    }

    public float getRestitution() {
        return _restitution;
    }

    public void setRestitution(float restitiution) {
        _restitution = restitiution;
    }

    public void setCollidable(boolean isCollidable) {
        _isCollidable = isCollidable;
    }

    public boolean isCollidable() {
        return _isCollidable;
    }

    public float getFriction() {
        return _friction;
    }

    public void setFriction(float friction) {
        _friction = friction;
    }

    public Vector getExternalVelocity() {
        return _externalVelocity;
    }

    public void setExternalVelocity(Vector externalVelocity) {
        _externalVelocity = externalVelocity;
    }

    public Side getOnWall() {
        return _onWall;
    }

    public void setOnWall(Side onWall) {
        _onWall = onWall;
    }

    public Side getOnFloor() {
        return _onFloor;
    }

    public void setOnFloor(Side onFloor) {
        _onFloor = onFloor;
    }

    public void addListener(CollisionListener newListener) {
        if(_listeners == null)
            _listeners = new ArrayList<CollisionListener>(5);
        _listeners.add(newListener);
    }

    public void removeListener(CollisionListener oldListener) {
        if (_listeners != null)
            _listeners.remove(oldListener);
    }

    public void callCollisionListners(Rectangle incomming, Side fromDirection) {
        if (_listeners == null)
            return;
        for (CollisionListener listener : _listeners)
            listener.collision(this, incomming, fromDirection);
    }

    public Rectangle getCopy() {
        Rectangle rect = new Rectangle(getBottomLeft().getCopy(), getWidth(), getHeight(), isStatic());
        rect.setVelocity(getVelocity().getCopy());
        rect.setCollidable(isCollidable());
        rect.setRestitution(getRestitution());
        rect.setExternalVelocity(getExternalVelocity());
        return rect;
    }

    @Override
    public String toString() {
        return String.format("[X:%4.2f, Y:%4.2f, W:%4.2f, H:%4.2f, R:%4.2f, S:%s, C:%s, F:%4.2f]",
                _bottomLeft.getX(), _bottomLeft.getY(), _width, _height,
                _restitution, _isStatic ? "T" : "F", _isCollidable ? "T" : "F", _friction);
    }
}
