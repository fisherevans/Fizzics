package com.fisherevans.fizzics.test;

import com.fisherevans.fizzics.World;
import com.fisherevans.fizzics.components.Rectangle;
import com.fisherevans.fizzics.components.Side;
import com.fisherevans.fizzics.components.Vector;
import com.fisherevans.fizzics.listeners.CollisionListener;
import com.fisherevans.fizzics.listeners.GlobalCollisionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Author: Fisher Evans
 * Date: 12/16/13
 */
public class Test extends JPanel implements GlobalCollisionListener, KeyListener {
    public static final int SIZE = 100;

    public static int HEIGHT = SIZE;
    public static int WIDTH = SIZE;

    public static float SCALE = SIZE / 20;

    private boolean _up = false, _left = false, _right = false;

    private JFrame _frame;

    private long _lastPaint;

    private World _world;

    private Rectangle _player;

    public Test() {
        super();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        _frame = new JFrame();
        _frame.add(this);
        _frame.setVisible(true);
        _frame.pack();
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.addKeyListener(this);

        _world = new World(new Vector(0, -20));
        _world.addGlobalCollisionListener(this);

        Rectangle r = new Rectangle(5, 15, 10, 1, true);
        _world.addRectangle(r);

        r = new Rectangle(16, 4, 1, 10, true);
        r.setFriction(0);
        _world.addRectangle(r);

        r = new Rectangle(5, 2, 10, 1, true);
        // r.setFriction(0);
        _world.addRectangle(r);

        r = new Rectangle(3, 4, 1, 8, true);
        r.setFriction(0);
        _world.addRectangle(r);

        Rectangle rect;

        _player = new Rectangle(10, 10, 2, 2);
        _world.addRectangle(_player);

        rect = new Rectangle(8f, 5.5f, 1, 1);
        rect.setCollidable(true);
        rect.setStatic(true);
        rect.addListener(new CollisionListener() {
            @Override
            public void collision(Rectangle thisRectangle, Rectangle incommingRectangle, Side fromDirection) {
                System.out.println("Hit from the " + fromDirection + " by " + incommingRectangle.toString());
                if (fromDirection == Side.South) {
                    System.out.println("Killed it!");
                    _world.removeRectangle(thisRectangle);
                }
            }
        });
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

        if (_up && _player.getOnFloor() == Side.South)
            _player.getVelocity().setY(10);
        
        if (_right && !_left)
            _player.getVelocity().setX(6);
        else if (_left && !_right)
            _player.getVelocity().setX(-6);

        _world.step(delta);
        // System.out.println(delta);
        // _world.step(0.017f);

        // g.setColor(new Color(200, 225, 255));
        int color = 200;
        g.setColor(new Color(color, color, color));
        for (Rectangle r : _world.getRectangles()) {
            g.fillRect((int) (r.getX1() * SCALE), // x
                       (int) (HEIGHT - r.getY1() * SCALE), // y
                       (int) (r.getWidth() * SCALE), // width
                       (int) (r.getHeight() * SCALE)); // height
        }
        
        g.setColor(Color.black);
        g.drawString("Wall: " + _player.getOnWall() + " - Floor: " + _player.getOnFloor(), 10, 20);
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
        // System.out.println(rect1 + " banged " + rect2);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
        case KeyEvent.VK_UP:
            _up = true;
            break;
        case KeyEvent.VK_LEFT:
            _left = true;
            break;
        case KeyEvent.VK_RIGHT:
            _right = true;
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()) {
        case KeyEvent.VK_UP:
            _up = false;
            break;
        case KeyEvent.VK_LEFT:
            _left = false;
            break;
        case KeyEvent.VK_RIGHT:
            _right = false;
            break;
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {

    }
}
