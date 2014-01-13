package com.fisherevans.fizzics.components;

import com.fisherevans.fizzics.World;

import java.util.Comparator;

/**
 * Author: Fisher Evans
 * Date: 12/26/13
 */
public class RectangleSorter implements Comparator<Rectangle> {
    private World _world;

    public RectangleSorter(World world) {
        _world = world;
    }

    @Override
    public int compare(Rectangle r1, Rectangle r2) {
        return (int) ((r1.getY1() - r2.getY2())*(_world.getGravity()));
    }
}
