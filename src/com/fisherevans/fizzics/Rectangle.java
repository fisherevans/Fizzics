package com.fisherevans.fizzics;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Rectangle {
    private Vector _topLeft, _velocity;
    private float _width, _height;

    private boolean _static = false;

    public Rectangle(float topLeftX, float topLeftY, float width, float height) {
        this(new Vector(topLeftX, topLeftY), width, height);
    }

    public Rectangle(Vector topLeft, float width, float height) {
        _topLeft = topLeft;
        _width = width;
        _height = height;
        _velocity = new Vector(0, 0);
    }

    public boolean contains(Vector p) {
        return p.getX() > getX1()
                && p.getX() < getX2()
                && p.getY() > getY1()
                && p.getY() < getY2();
    }

    public boolean intersects(Rectangle rec) {
        return getX1() < rec.getX2()
                && getX2() > rec.getX1()
                && getY1() < rec.getY2()
                && getY2() > rec.getY1();
    }

    public void move(Vector m) {
        _topLeft.add(m);
    }

    public float getX1() {
        return _topLeft.getX();
    }

    public float getX2() {
        return _topLeft.getX() + _width;
    }

    public float getY1() {
        return _topLeft.getY();
    }

    public float getY2() {
        return _topLeft.getY() + _height;
    }

    public Vector getTopLeft() {
        return _topLeft;
    }

    public void setTopLeft(Vector topLeft) {
        _topLeft = topLeft;
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

    public void setStatic(boolean aStatic) {
        _static = aStatic;
    }
}
