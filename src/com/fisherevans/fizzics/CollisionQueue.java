package com.fisherevans.fizzics;

import com.fisherevans.fizzics.components.Rectangle;

/**
 * A simple priority queue used to sort the collision resolution importance.
 * @author Fisher Evans
 * Date: 12/19/2013
 */
public class CollisionQueue {
    private Node _head = null;
    private int _size = 0;

    /**
     * adds a rectangle to the collision queue with the given prioruty
     * @param rect the rectangle of the collision
     * @param priority the priority of the collision
     */
    public void add(Rectangle rect, float priority) {
        if (_size == 0 || _head == null)
            _head = new Node(rect, priority);
        else
            _head.add(rect, priority);

        _size++;
    }

    /**
     * remove the more important rectangle from the queue
     * @return the most important rectangle of all collisions
     */
    public Rectangle remove() {
        if (_head == null) {
            _size = 0;
            return null;
        } else {
            Rectangle rect = _head.getRect();
            _head = _head.getNext();
            _size--;
            return rect;
        }
    }

    /**
     * @return the number of rectangles in this queue
     */
    public int size() {
        return _size;
    }

    /**
     * A simple linked list node for this priority queue
     */
    private class Node {
        private float _priority;
        private Rectangle _rect;
        private Node _next;

        /**
         * creates the node with an "null" next node
         * @param rect the rectangle to hold
         * @param priority the priority of the rectangle
         */
        public Node(Rectangle rect, float priority) {
            _priority = priority;
            _rect = rect;
            _next = null;
        }

        /**
         * inserts the given rectangle before this node if the priority is higher,
         * other wise calls add(rect, priority) on this next node.
         * if next node is null, adds this rectangle as next.
         * @param rect the new rect to add to the queue
         * @param priority the priority of this rectngle
         */
        public void add(Rectangle rect, float priority) {
            if (priority >= _priority) {
                Node newNode = new Node(_rect, _priority);
                newNode.setNext(_next);
                _rect = rect;
                _priority = priority;
                _next = newNode;
            } else {
                if (_next != null) {
                    _next.add(rect, priority);
                } else {
                    _next = new Node(rect, priority);
                }
            }
        }

        /**
         * @return the rectangle this node holds
         */
        public Rectangle getRect() {
            return _rect;
        }

        /**
         * returns the next node in this queue
         * @return the next node
         */
        public Node getNext() {
            return _next;
        }

        /**
         * sets the next node this node should point to in the queue
         * @param next the new node
         */
        public void setNext(Node next) {
            _next = next;
        }
    }
}
