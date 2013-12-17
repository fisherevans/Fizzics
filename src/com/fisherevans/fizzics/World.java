package com.fisherevans.fizzics;

import java.util.ArrayList;
import java.util.List;

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

		Rectangle beforeStep; // allocate loop variables
        float dx, dy, shift;
        for(Rectangle rectangle:_rectangles) {
            if(!rectangle.isStatic()) { // for each non static rectangle
				beforeStep = rectangle.getCopy(); // keep a cop of the position before movement
				rectangle.move(rectangle.getVelocity().add(gravityVector).getCopy().scale(delta));
                for(Rectangle otherRectangle:_rectangles) { // check for collisions
                    if (otherRectangle != rectangle && rectangle.intersects(otherRectangle)) {
                        dx = otherRectangle.getCenterX() - beforeStep.getCenterX(); // get the center offset of the two colliding rects
                        dy = otherRectangle.getCenterY() - beforeStep.getCenterY();
                        if (Math.abs(dx) > Math.abs(dy)) { // side to side
                            shift = (rectangle.getWidth() + otherRectangle.getWidth()) / 2f; // min distance between to centers
                            if (dx > 0) { // left
                                shift = otherRectangle.getCenterX() - rectangle.getCenterX() - shift;
                            } else { // right
                                shift = shift - rectangle.getCenterX() + otherRectangle.getCenterX();
                            }
                            rectangle.move(new Vector(shift, 0));
                            rectangle.getVelocity().multiply(new Vector(-1f * rectangle.getRestitution(), 1f));
                        } else { // up and down
                            shift = (rectangle.getHeight() + otherRectangle.getHeight()) / 2f; // min distance between to centers
                            if (dy > 0) { // bottom
                                shift = otherRectangle.getCenterY() - rectangle.getCenterY() - shift;
                            } else { // top
                                shift = shift - rectangle.getCenterY() + otherRectangle.getCenterY();
                            }
                            rectangle.move(new Vector(0, shift));
                            rectangle.getVelocity().multiply(new Vector(1f, -1f * rectangle.getRestitution()));
                        }
					} // end if not same as outer and does collide
				} // inner loop of rectangles
			} // end if not static
		} // outer loops of rectangles
    }

    public List<Rectangle> getRectangles() {
        return _rectangles;
    }
}
