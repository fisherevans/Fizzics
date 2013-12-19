package com.fisherevans.priorityQueue;

import java.util.LinkedList;

public class FPriorityQueue<T> {
    private Node<T> _head = null;
    private int _size = 0;

    public void add(T object, float priority) {
        if (_size == 0 || _head == null)
            _head = new Node<T>(object, priority);
        else
            _head.add(object, priority);

        _size++;
    }

    public T remove() {
        if (_head == null) {
            _size = 0;
            return null;
        } else {
            T object = _head.getObject();
            _head = _head.getNext();
            _size--;
            return object;
        }
    }

    public int size() {
        return _size;
    }

    private class Node<T> {
        private float _priority;
        private T _object;
        private Node<T> _next;

        public Node(T object, float priority) {
            _priority = priority;
            _object = object;
            _next = null;
        }

        public void add(T object, float priority) {
            if (priority >= _priority) {
                Node<T> newNode = new Node<T>(_object, _priority);
                newNode.setNext(_next);
                _object = object;
                _priority = priority;
                _next = newNode;
            } else {
                if (_next != null) {
                    _next.add(object, priority);
                } else {
                    _next = new Node<T>(object, priority);
                }
            }
        }

        public T getObject() {
            return _object;
        }

        public Node<T> getNext() {
            return _next;
        }

        public void setNext(Node<T> next) {
            _next = next;
        }
    }
}
