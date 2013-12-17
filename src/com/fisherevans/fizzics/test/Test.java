package com.fisherevans.fizzics.test;

import com.fisherevans.fizzics.CollisionListener;
import com.fisherevans.fizzics.Rectangle;
import com.fisherevans.fizzics.Vector;
import com.fisherevans.fizzics.World;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Test extends JPanel implements CollisionListener {
    public static int HEIGHT = 800;
    public static int WIDTH = 800;

    public static float SCALE = 40f;

    private JFrame _frame;

    private long _lastPaint;

    private World _world;
    private com.fisherevans.fizzics.Rectangle _player, _static;

    public Test() {
        super();
        _frame = new JFrame();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        _frame.add(this);
        _frame.setVisible(true);
        _frame.pack();
        _frame.setDefaultCloseOperation(_frame.EXIT_ON_CLOSE);

        _world = new World(new Vector(0, -10));
        _world.addCollisionListener(this);

        _player = new Rectangle(15, 10, 2, 2);
        _player.setVelocity(new Vector(-15, 3));
        _player.setRestitution(0.4f);
        _world.addRectangle(_player);

        _player = new Rectangle(2, 10, 2, 2);
        _player.setVelocity(new Vector(30, 3));
        _player.setRestitution(0.4f);
        _world.addRectangle(_player);

        _player = new Rectangle(9, 20, 2, 2);
        _player.setVelocity(new Vector(1, -20));
        _player.setRestitution(0.4f);
        _world.addRectangle(_player);

        _player = new Rectangle(6, 7, 2, 2);
        _player.setStatic(true);
        _player.setCollidable(false);
        _world.addRectangle(_player);

        _world.addRectangle(new Rectangle(10, 7, 2, 2, true));
        _world.addRectangle(new Rectangle(10, 4, 2, 2, true));
        _world.addRectangle(new Rectangle(3, 7, 2, 2, true));
        _world.addRectangle(new Rectangle(7, 3, 2, 2, true));
        _world.addRectangle(new Rectangle(4, 3, 2, 2, true));

        _lastPaint = System.currentTimeMillis();
    }

    public void paintComponent(Graphics g)
    {
        long sysTime = System.currentTimeMillis();
        float delta = (sysTime-_lastPaint)/1000f;
        _lastPaint = sysTime;

        g.clearRect(0, 0, WIDTH, HEIGHT);

        _world.step(delta);

        g.setColor(new Color(230, 230, 255));
        for (Rectangle r : _world.getRectangles()) {
            g.fillRect((int) (r.getX1() * SCALE), HEIGHT - ((int) ((r.getY1()) * SCALE)), (int) (r.getWidth() * SCALE), (int) (r.getHeight() * SCALE));
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
    public void collision(Rectangle rect1, Rectangle rect2) {
        System.out.println(rect1 + " banged " + rect2);
    }
}
