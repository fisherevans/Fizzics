package com.fisherevans.fizzics;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Rectangle {
    private Vector _topLeft, _velocity;
    private float _width, _height;

    private float _restitiution = 0.8f;
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
    if (getX1() > p.getX())
      return false;
    if (getX2() < p.getX())
      return false;
    if (getY1() > p.getY())
      return false;
    if (getY2() < p.getY())
      return false;
    return true;
    }

    public boolean intersects(Rectangle rec) {
    if (getX2() < rec.getX1())
      return false;
    if (getX1() > rec.getX2())
      return false;
    if (getY2() < rec.getY1())
      return false;
    if (getY1() > rec.getY2())
      return false;
    return true;
    }

    public void move(Vector m) {
        _topLeft.add(m);
    }

  public float getCenterX() {
    return _topLeft.getX() + _width / 2f;
  }

  public float getCenterY() {
    return _topLeft.getY() + _height / 2f;
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

    public float getRestitiution() {
        return _restitiution;
    }

    public void setRestitiution(float restitiution) {
        _restitiution = restitiution;
    }

    public Rectangle getCopy() {
        Rectangle rect =  new Rectangle(_topLeft.getCopy(), _width, _height);
        rect.setVelocity(_velocity.getCopy());
        return rect;
    }
}
