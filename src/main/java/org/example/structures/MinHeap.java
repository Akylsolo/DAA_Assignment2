package org.example.structures;

import org.example.metrics.OpCounter;

/**
 * Array-based binary min-heap storing primitive ints.
 * Satisfies the heap property: heap[parent] <= heap[child].
 */
public class MinHeap {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] heap;
    private int size;
    private OpCounter counter;

    public MinHeap() {
        this(DEFAULT_CAPACITY, new OpCounter());
    }

    public MinHeap(int initialCapacity) {
        this(initialCapacity, new OpCounter());
    }

    public MinHeap(OpCounter counter) {
        this(DEFAULT_CAPACITY, counter);
    }

    public MinHeap(int initialCapacity, OpCounter counter) {
        if (initialCapacity < 1) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        this.heap = new int[initialCapacity];
        this.size = 0;
        this.counter = counter != null ? counter : new OpCounter();
    }

    /**
     * Inserts an element into the min-heap and restores heap property using bubble-up.
     */
    public void insert(int element) {
        ensureCapacity(size + 1);
        heap[size] = element;
        counter.move();
        bubbleUp(size);
        size++;
    }

    /**
     * Returns the minimum element in O(1) without removing it.
     */
    public int peekMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        counter.step(); // read root
        return heap[0];
    }

    /**
     * Removes and returns the minimum element, restoring heap property via bubble-down.
     */
    public int extractMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        counter.step();
        int minVal = heap[0];

        int lastVal = heap[size - 1];
        counter.step();
        heap[0] = lastVal;
        counter.move();
        size--;

        if (size > 0) {
            bubbleDown(0);
        }
        return minVal;
    }

    /**
     * Restores min-heap property by bubbling an element up towards the root.
     */
    private void bubbleUp(int index) {
        int curr = index;
        while (curr > 0) {
            int parent = (curr - 1) / 2;
            counter.step(); // read curr
            counter.step(); // read parent
            counter.compare();
            if (heap[curr] < heap[parent]) {
                swap(curr, parent);
                curr = parent;
            } else {
                break;
            }
        }
    }

    /**
     * Restores min-heap property by bubbling an element down towards the leaves.
     */
    public void bubbleDown(int index) {
        int curr = index;
        while (true) {
            int left = 2 * curr + 1;
            int right = 2 * curr + 2;
            int smallest = curr;

            if (left < size) {
                counter.step(); // read smallest
                counter.step(); // read left
                counter.compare();
                if (heap[left] < heap[smallest]) {
                    smallest = left;
                }
            }

            if (right < size) {
                counter.step(); // read smallest
                counter.step(); // read right
                counter.compare();
                if (heap[right] < heap[smallest]) {
                    smallest = right;
                }
            }

            if (smallest != curr) {
                swap(curr, smallest);
                curr = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        int tmp = heap[i];
        counter.step();
        heap[i] = heap[j];
        counter.move();
        heap[j] = tmp;
        counter.move();
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > heap.length) {
            int newCapacity = heap.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            int[] newHeap = new int[newCapacity];
            for (int i = 0; i < size; i++) {
                counter.step();
                newHeap[i] = heap[i];
                counter.move();
            }
            heap = newHeap;
        }
    }

    /**
     * Bonus Task B: Floyd's bottom-up buildHeap algorithm in O(n).
     */
    public static MinHeap buildHeap(int[] array, OpCounter counter) {
        OpCounter cnt = counter != null ? counter : new OpCounter();
        MinHeap minHeap = new MinHeap(array.length, cnt);
        minHeap.heap = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            cnt.step();
            minHeap.heap[i] = array[i];
            cnt.move();
        }
        minHeap.size = array.length;
        // Heapify from the last non-leaf node down to the root
        for (int i = (minHeap.size / 2) - 1; i >= 0; i--) {
            minHeap.bubbleDown(i);
        }
        return minHeap;
    }

    /**
     * Builds a heap by calling insert() n times in O(n log n) for comparison with Floyd's method.
     */
    public static MinHeap buildByInsertions(int[] array, OpCounter counter) {
        OpCounter cnt = counter != null ? counter : new OpCounter();
        MinHeap minHeap = new MinHeap(array.length, cnt);
        for (int val : array) {
            minHeap.insert(val);
        }
        return minHeap;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
    }

    public int getCapacity() {
        return heap.length;
    }

    public int[] getArrayCopy() {
        int[] copy = new int[size];
        System.arraycopy(heap, 0, copy, 0, size);
        return copy;
    }

    public boolean verifyHeapProperty() {
        for (int i = 0; i < size; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < size && heap[i] > heap[left]) {
                return false;
            }
            if (right < size && heap[i] > heap[right]) {
                return false;
            }
        }
        return true;
    }

    public OpCounter getCounter() {
        return counter;
    }

    public void setCounter(OpCounter counter) {
        this.counter = counter != null ? counter : new OpCounter();
    }
}
