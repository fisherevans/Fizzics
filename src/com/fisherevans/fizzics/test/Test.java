package com.fisherevans.fizzics.test;

import com.fisherevans.fizzics.GlobalCollisionListener;
import com.fisherevans.fizzics.Rectangle;
import com.fisherevans.fizzics.Vector;
import com.fisherevans.fizzics.World;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Test extends JPanel implements GlobalCollisionListener {
    public static int HEIGHT = 400;
    public static int WIDTH = 400;

    public static float SCALE = 20;

    private JFrame _frame;

    private long _lastPaint;

    private World _world;

    public Test() {
        super();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        _frame = new JFrame();
        _frame.add(this);
        _frame.setVisible(true);
        _frame.pack();
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        _world = new World(new Vector(0, -10));
        _world.addGlobalCollisionListener(this);

        _world.addRectangle(new Rectangle(5, 15, 10, 1, true));
        _world.addRectangle(new Rectangle(16, 4, 1, 10, true));
        _world.addRectangle(new Rectangle(5, 2, 10, 1, true));
        _world.addRectangle(new Rectangle(3, 4, 1, 8, true));

        Rectangle rect;

        rect = new Rectangle(6, 17, 3, 1);
        rect.setVelocity(new Vector(2, 0));
        rect.setRestitution(0.8f);
        // _world.addRectangle(rect);

        rect = new Rectangle(11, 17, 1, 4);
        rect.setVelocity(new Vector(1, 0));
        rect.setRestitution(0.8f);
        // _world.addRectangle(rect);

        rect = new Rectangle(11, 4, 3, 3);
        rect.setVelocity(new Vector(20, 20));
        rect.setRestitution(0.8f);
        _world.addRectangle(rect);

        rect = new Rectangle(8, 10, 2, 2);
        rect.setVelocity(new Vector(15, 20));
        rect.setRestitution(0.8f);
        _world.addRectangle(rect);

        _lastPaint = System.currentTimeMillis();
    }

    public void paintComponent(Graphics g)
    {
        long sysTime = System.currentTimeMillis();
        float delta = (sysTime-_lastPaint)/1000f;
        _lastPaint = sysTime;

        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // _world.step(delta);
        // System.out.println(delta);
        _world.step(0.017f);

        g.setColor(new Color(200, 225, 255));
        for (Rectangle r : _world.getRectangles()) {
            g.fillRect((int) (r.getX1() * SCALE), // x
                       (int) (HEIGHT - r.getY1() * SCALE), // y
                       (int) (r.getWidth() * SCALE), // width
                       (int) (r.getHeight() * SCALE)); // height
        }
    }

    public static void main(String arg[]){
        final Test test = new Test();
        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    test.repaint();
                    try {
                        Thread.sleep(17);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        gameThread.start();
    }

    @Override
    public void globalCollision(Rectangle rect1, Rectangle rect2) {
        System.out.println(rect1 + " banged " + rect2);
    }
}
