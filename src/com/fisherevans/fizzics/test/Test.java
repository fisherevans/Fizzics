package com.fisherevans.fizzics.test;

import com.fisherevans.fizzics.Vector;
import com.fisherevans.fizzics.World;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Test extends JPanel {
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

        //_player = new com.fisherevans.fizzics.Rectangle(10, 25, 5, 5);
        _player = new com.fisherevans.fizzics.Rectangle(5, 7, 2, 2);
        _player.setVelocity(new Vector(15, 3));
        _player.setRestitution(0.4f);
        _world.addRectangle(_player);

        _world.addRectangle(new com.fisherevans.fizzics.Rectangle(10, 7, 2, 2, true));
        _world.addRectangle(new com.fisherevans.fizzics.Rectangle(10, 4, 2, 2, true));
        _world.addRectangle(new com.fisherevans.fizzics.Rectangle(3, 7, 2, 2, true));
        _world.addRectangle(new com.fisherevans.fizzics.Rectangle(7, 3, 2, 2, true));

        _lastPaint = System.currentTimeMillis();
    }

    public void paintComponent(Graphics g)
    {
        long sysTime = System.currentTimeMillis();
        float delta = (sysTime-_lastPaint)/1000f;
        _lastPaint = sysTime;

        g.clearRect(0, 0, WIDTH, HEIGHT);

        _world.step(delta);

        g.setColor(Color.red);
        for (com.fisherevans.fizzics.Rectangle r : _world.getRectangles()) {
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
}
