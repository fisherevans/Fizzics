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
    public static final int SIZE = 200;

    public static int HEIGHT = SIZE;
    public static int WIDTH = SIZE;

    public static float SCALE = SIZE / 20;

    private boolean _up = false, _left = false, _right = false;

    private JFrame _frame;

    private long _lastPaint;

    private World _world;

    private Rectangle _player;

    private boolean _canJump = true;

    public Test() {
        super();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        _frame = new JFrame();
        _frame.add(this);
        _frame.setVisible(true);
        _frame.pack();
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.addKeyListener(this);

        _world = new World(-25);
        _world.addGlobalCollisionListener(this);

        _world.addRectangle(new Rectangle(5, 15, 10, 1, true));
        _world.addRectangle(new Rectangle(16, 4, 1, 10, true));
        _world.addRectangle(new Rectangle(3, 2, 13, 1, true));
        _world.addRectangle(new Rectangle(3, 4, 1, 8, true));


        _player = new Rectangle(9.5f, 12, 1f, 2);
        _world.addRectangle(_player);

        Rectangle rect = new Rectangle(8f, 10, 1, 1);
        rect.setSolid(true);
        rect.setStatic(true);
        rect.addListener(new CollisionListener() {
            @Override
            public void collision(Rectangle thisRectangle, Rectangle incomingRectangle, Side fromDirection) {
                if (fromDirection == Side.South) {
                    _world.removeRectangle(thisRectangle);
                }
            }
        });
        _world.addRectangle(rect);

        _world.addRectangle(new Rectangle(6, 6, 1, 1, true));
        _world.addRectangle(new Rectangle(7, 6, 1, 1, true));
        _world.addRectangle(new Rectangle(8, 6, 1, 1, true));

        _world.addRectangle(new Rectangle(12, 6, 1, 1, true));
        _world.addRectangle(new Rectangle(12, 7, 1, 1, true));
        _world.addRectangle(new Rectangle(12, 8, 1, 1, true));

        Rectangle r2 = new Rectangle(9.5f, 8, 1.5f, 1.5f);
        r2.setRestitution(0.9f);
        _world.addRectangle(r2);

        _lastPaint = System.currentTimeMillis();
    }

    public void paintComponent(Graphics g)
    {
        long sysTime = System.currentTimeMillis();
        float delta = (sysTime-_lastPaint)/1000f;
        _lastPaint = sysTime;

        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        if (_player.getBottomLeft().getY() < -50) {
            _player.setBottomLeft(new Vector(9.5f, 8));
            _player.getVelocity().scale(-1);
        }

        if(_canJump) {
            if (_up && _player.getFloor() == Side.South) {
                _player.getVelocity().setY(15);
                _canJump = false;
            } else if(_up && _player.getWall() == Side.East) {
                _player.setVelocity(new Vector(-10f, 10f));
                _canJump = false;
            } else if(_up && _player.getWall() == Side.West) {
                _player.setVelocity(new Vector(10f, 10f));
                _canJump = false;
            }
        }

        float accel = _player.getFloor() == Side.South ? 100 : 16.35f;
        if (_right && !_left)
            _player.getAcceleration().setX(_player.getVelocity().getX() < 10 ? accel : -accel);
        else if (_left && !_right)
            _player.getAcceleration().setX(_player.getVelocity().getX() > -10 ? -accel : accel);
        else {
            if (Math.abs(_player.getVelocity().getX()) > 0.5) {
                _player.getAcceleration().setX(_player.getVelocity().getX() > 0 ? -accel : accel);
            } else {
                _player.getAcceleration().setX(-_player.getVelocity().getX()*20f);
            }
        }

        // _world.step(delta);
        // System.out.println(delta);
        // _world.step(0.0017f);
        _world.step(0.017f);

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
        g.drawString(String.format("Wall:%s, Floor:%s, Vel:%s, Acc:%s", _player.getWall(), _player.getFloor(), _player.getVelocity(), _player.getAcceleration()), 10, 20);
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
        //System.out.println(rect1 + " banged " + rect2);
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
            _canJump = true;
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
