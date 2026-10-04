package org.example.structures;

import org.example.metrics.OpCounter;

/**
 * Doubly-linked list implementation storing primitive ints.
 * Counts pointer updates as moves and node navigations as steps.
 */
public class MyLinkedList implements IntList {

    public static class Node {
        public int val;
        public Node prev;
        public Node next;

        public Node(int val) {
            this.val = val;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private OpCounter counter;

    public MyLinkedList() {
        this(new OpCounter());
    }

    public MyLinkedList(OpCounter counter) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.counter = counter != null ? counter : new OpCounter();
    }

    @Override
    public void add(int element) {
        Node newNode = new Node(element);
        if (size == 0) {
            head = newNode;
            counter.move();
            tail = newNode;
            counter.move();
        } else {
            tail.next = newNode;
            counter.move();
            newNode.prev = tail;
            counter.move();
            tail = newNode;
            counter.move();
        }
        size++;
    }

    @Override
    public void add(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        if (index == size) {
            add(element);
            return;
        }
        Node newNode = new Node(element);
        if (index == 0) {
            newNode.next = head;
            counter.move();
            if (head != null) {
                head.prev = newNode;
                counter.move();
            }
            head = newNode;
            counter.move();
            if (size == 0) {
                tail = newNode;
                counter.move();
            }
        } else {
            Node curr = getNode(index);
            Node prevNode = curr.prev;

            newNode.next = curr;
            counter.move();
            newNode.prev = prevNode;
            counter.move();
            if (prevNode != null) {
                prevNode.next = newNode;
                counter.move();
            }
            curr.prev = newNode;
            counter.move();
        }
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        Node curr;
        if (index == 0) {
            curr = head;
        } else if (index == size - 1) {
            curr = tail;
        } else {
            curr = getNode(index);
        }

        int removedVal = curr.val;

        if (curr.prev != null) {
            curr.prev.next = curr.next;
            counter.move();
        } else {
            head = curr.next;
            counter.move();
        }

        if (curr.next != null) {
            curr.next.prev = curr.prev;
            counter.move();
        } else {
            tail = curr.prev;
            counter.move();
        }

        size--;
        return removedVal;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        Node curr = getNode(index);
        return curr.val;
    }

    @Override
    public boolean contains(int element) {
        Node curr = head;
        while (curr != null) {
            counter.compare();
            if (curr.val == element) {
                return true;
            }
            curr = curr.next;
            counter.step(); // moving to next node
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    @Override
    public OpCounter getCounter() {
        return counter;
    }

    @Override
    public void setCounter(OpCounter counter) {
        this.counter = counter != null ? counter : new OpCounter();
    }

    private Node getNode(int index) {
        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
            counter.step();
        }
        return curr;
    }
}
