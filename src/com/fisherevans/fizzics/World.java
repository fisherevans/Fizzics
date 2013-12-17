package com.fisherevans.fizzics;

import java.util.ArrayList;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class World {
    private Vector _gravity;
    private ArrayList<Rectangle> _rectangles;

    public World(Vector gravity) {
        _gravity = gravity;
        _rectangles = new ArrayList<Rectangle>();
    }

    public void addRectangle(Rectangle newRec) {
        _rectangles.add(newRec);
    }

    public void step(float delta) {
        Vector gravityVector = _gravity.getCopy().scale(delta);

        for(Rectangle rectangle:_rectangles) {
            if(!rectangle.isStatic()) {
                rectangle.getVelocity().add(gravityVector);
                rectangle.move(rectangle.getVelocity().getCopy().scale(delta));
                for(Rectangle otherRectangle:_rectangles) {
                    if(otherRectangle != rectangle) {
                        while(rectangle.intersects(otherRectangle)) {
                            float collisionWidth = otherRectangle.getWidth() + rectangle.getWidth();
                            float collisionHeight = otherRectangle.getHeight() + rectangle.getHeight();
                            float dx = otherRectangle.getX1() - rectangle.getX1();
                            float dy = otherRectangle.getY1() - rectangle.getY1();
                            //http://gamedev.stackexchange.com/questions/29786/a-simple-2d-rectangle-collision-algorithm-that-also-determines-which-sides-that
                            if(Math.abs(dx) <= collisionWidth && Math.abs(dy) <= collisionHeight) {
                                float wy = collisionWidth*dy;
                                float hx = collisionHeight*dx;
                                System.out.print(rectangle);
                                if(wy > hx) {
                                    if(wy > -hx) { // top
                                        System.out.print("top");
                                        System.exit(0);
                                    } else { // left
                                        System.out.print("left");
                                        System.exit(0);
                                    }
                                } else {
                                    if(wy > -hx) { // right
                                        System.out.print("right");
                                        System.exit(0);
                                    } else { // bottom
                                        System.out.print("bottom");
                                        System.exit(0);
                                    }
                                }
                            }
                            /*
                            if(otherRectangle.isStatic()) {
                                float xDiff = 0, yDiff = 0;
                                if(Math.abs(rectangle.getVelocity().getX()) > Math.abs(rectangle.getVelocity().getY())) {
                                    if(rectangle.getVelocity().getX() > 0)
                                        xDiff = rectangle.getX2() - otherRectangle.getX1();
                                    else
                                        xDiff = rectangle.getX1() - otherRectangle.getX2();
                                    rectangle.getVelocity().setX(rectangle.getVelocity().getX()*-1f*rectangle.getRestitiution());
                                } else {
                                    if(rectangle.getVelocity().getY() > 0)
                                        yDiff = rectangle.getY2() - otherRectangle.getY1();
                                    else
                                        yDiff = rectangle.getY1() - otherRectangle.getY2();
                                    rectangle.getVelocity().setY(rectangle.getVelocity().getY()*-1f*rectangle.getRestitiution());
                                }
                                rectangle.getTopLeft().subtract(new Vector(xDiff, yDiff));
                            } else {

                            }
                            */
                        }
                    }
                }
            }
        }
    }
}
