package com.fisherevans.fizzics.test;

import com.fisherevans.fizzics.Vector;
import com.fisherevans.fizzics.World;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Test extends JFrame {
    public static int HEIGHT = 800;
    public static int WIDTH = 800;

    public static float SCALE = 20f;

    private long _lastPaint;

    private World _world;
    private com.fisherevans.fizzics.Rectangle _player;

    public Test() {
        super();
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        _world = new World(new Vector(0, -8));
        _player = new com.fisherevans.fizzics.Rectangle(10, 20, 5, 5);
        _player.setVelocity(new Vector(5, 10));
        _world.addRectangle(_player);
        _lastPaint = System.currentTimeMillis();
    }

    public void paint(Graphics g)
    {
        long sysTime = System.currentTimeMillis();
        float delta = (sysTime-_lastPaint)/1000f;
        _lastPaint = sysTime;

        g.clearRect(0, 0, WIDTH, HEIGHT);

        _world.step(delta);

        g.setColor(Color.red);
        g.fillRect((int)(_player.getX1()*SCALE), HEIGHT-((int)((_player.getY1()+_player.getHeight())*SCALE)), (int)(_player.getWidth()*SCALE), (int)(_player.getHeight()*SCALE));
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
