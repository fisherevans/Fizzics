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
    public static final int SIZE = 1000;

    public static int HEIGHT = SIZE;
    public static int WIDTH = SIZE;

    public static float SCALE = SIZE / 50;

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

        _world = new World(-50);
        _world.setIterations(3);
        _world.addGlobalCollisionListener(this);

        _world.add(new Rectangle(1, 1, 48, 1, true));
        _world.add(new Rectangle(1, 2, 1, 47, true));
        _world.add(new Rectangle(48, 2, 1, 47, true));


        _player = new Rectangle(9.5f, 50, 1f, 2);
        _world.add(_player);

        for(int count = 0;count < 250;count++) {
            Rectangle r2 = new Rectangle((float)(Math.random()*43 + 3), (float)(Math.random()*43 + 3), 1.5f, 1.5f, false);
            r2.setVelocity(new Vector((float)(Math.random()*10-5), (float)(Math.random()*10-5)));
            //Rectangle r2 = new Rectangle(count+4, count*1.6f+4, 1.5f, 1.5f);
            //r2.setRestitution(0.9f);
            _world.add(r2);
        }

        _lastPaint = System.currentTimeMillis();
    }

    public void paintComponent(Graphics g)
    {
        long sysTime = System.currentTimeMillis();
        float delta = (sysTime-_lastPaint)/1000f;
        _lastPaint = sysTime;

        g.setColor(new Color(26, 26, 26));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        if (_player.getBottomLeft().getY() < -50) {
            _player.setBottomLeft(new Vector(9.5f, 8));
            _player.getVelocity().scale(-1);
        }

        if(_canJump) {
            if (_up && _player.getFloor() == Side.South) {
                _player.getVelocity().setY(30);
                _canJump = false;
            }/* else if(_up && _player.getWall() == Side.East) { // wall jumps
                _player.setVelocity(new Vector(-10f, 10f));
                _canJump = false;
            } else if(_up && _player.getWall() == Side.West) {
                _player.setVelocity(new Vector(10f, 10f));
                _canJump = false;
            } */
        }

        float accel = _player.getFloor() == Side.South ? 100 : 16.35f;
        if (_right && !_left)
            _player.getAcceleration().setX(_player.getVelocity().getX() < 10 ? accel : -accel);
        else if (_left && !_right)
            _player.getAcceleration().setX(_player.getVelocity().getX() > -10 ? -accel : accel);
        else {
            _player.getAcceleration().setX(0);
            _player.getVelocity().setX(_player.getVelocity().getX() - accel*delta*_player.getVelocity().getX()/8f);
        }

        _world.step(delta);
        // System.out.println(delta);
        // _world.step(0.0017f);
        //_world.step(0.017f);

        // g.setColor(new Color(200, 225, 255));
        int color = 200;
        g.setColor(new Color(color, color, color));
        for (Rectangle r : _world.getRectangles()) {
            if(r == _player)
                g.setColor(new Color(0, 93, 242));
            else if(r.isStatic())
                g.setColor(new Color(90,90,90));
            else
                g.setColor(new Color(242, 169, 0));
            g.fillRect((int) (r.getX1() * SCALE), // x
                       (int) (HEIGHT - r.getY1() * SCALE), // y
                       (int) (r.getWidth() * SCALE), // width
                       (int) (r.getHeight() * SCALE)); // height
            g.setColor(Color.black);
            g.drawRect((int) (r.getX1() * SCALE), // x
                    (int) (HEIGHT - r.getY1() * SCALE), // y
                    (int) (r.getWidth() * SCALE - 1), // width
                    (int) (r.getHeight() * SCALE - 1)); // height
        }
        
        g.setColor(Color.white);
        g.drawString(String.format("Wall:%s", _player.getWall()), 10, 20);
        g.drawString(String.format("Floor:%s", _player.getFloor()), 10, 40);
        g.drawString(String.format("Vel:%s", _player.getVelocity()), 10, 60);
        g.drawString(String.format("Acc:%s", _player.getAcceleration()), 10, 80);
        g.drawString(String.format("Delta:%sms", delta * 1000), 10, 100);
        g.drawString(String.format("Rects:%s", _world.getRectangles().size()), 10, 120);
    }

    public static void main(String arg[]){
        final Test test = new Test();
        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    test.repaint();
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
