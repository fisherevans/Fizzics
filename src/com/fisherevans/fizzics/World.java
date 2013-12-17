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

		Rectangle beforeStep;
        float dx, dy;
        for(Rectangle rectangle:_rectangles) {
            if(!rectangle.isStatic()) {
				beforeStep = rectangle.getCopy();
				rectangle.move(rectangle.getVelocity().add(gravityVector).getCopy().scale(delta));
                for(Rectangle otherRectangle:_rectangles) {
                    if (otherRectangle != rectangle && rectangle.intersects(otherRectangle)) {
                        System.out.println("Colission!");
                        dx = otherRectangle.getCenterX() - beforeStep.getCenterX();
                        dy = otherRectangle.getCenterY() - beforeStep.getCenterY();
                        if (Math.abs(dx) > Math.abs(dy)) {
                            if (dx > 0) {
                                System.out.println("Left");
                                rectangle.move(new Vector(dx - (rectangle.getWidth() + otherRectangle.getWidth()) / 2f, 0));
                                rectangle.getVelocity().setX(rectangle.getVelocity().getX() * -1f * rectangle.getRestitiution());
                            } else {
                                System.out.println("Right");
                            }
                        } else {
                            if (dy > 0) {
                                System.out.println("Bottom");
                            } else {
                                System.out.println("Top");
                            }
                        }
					} // end if not same as outer and does collide
				} // inner loop of rectangles
			} // end if not static
		} // outer loops of rectangles
    }
}
