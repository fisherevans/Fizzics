package com.fisherevans.fizzics;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Vector {
    private float _x, _y;

    public Vector(float x, float y) {
        _x = x;
        _y = y;
    }

    public Vector add(Vector vector) {
        _x += vector.getX();
        _y += vector.getY();
        return this;
    }

    public Vector subtract(Vector vector) {
        _x -= vector.getX();
        _y -= vector.getY();
        return this;
    }

    public Vector multiply(Vector vector) {
        _x *= vector.getX();
        _y *= vector.getY();
        return this;
    }

    public Vector scale(float magnitude) {
        _x *= magnitude;
        _y *= magnitude;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(_x*_x + _y*_y);
    }

    public float dot(Vector vector) {
        return _x*vector.getX() + _y*vector.getY();
    }

    public Vector normalize() {
        float startLength = length();
        _x /= startLength;
        _y /= startLength;
        return this;
    }

    public float getAngle() {
        return (float) Math.atan2(_y, _x);
    }

    public float getX() {
        return _x;
    }

    public void setX(float x) {
        _x = x;
    }

    public float getY() {
        return _y;
    }

    public void setY(float y) {
        _y = y;
    }

    public Vector getCopy() {
        return new Vector(_x, _y);
    }

    public static Vector fromAngle(float angle, float length) {
        float x = (float) Math.cos(angle)*length;
        float y = (float) Math.sin(angle)*length;
        return new Vector(x, y);
    }
}
