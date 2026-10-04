package org.example.structures;

import org.example.metrics.OpCounter;

/**
 * Array-based dynamic list storing primitive ints.
 * Automatically doubles its capacity when full.
 */
public class DynamicArray implements IntList {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] data;
    private int size;
    private OpCounter counter;

    public DynamicArray() {
        this(DEFAULT_CAPACITY, new OpCounter());
    }

    public DynamicArray(int initialCapacity) {
        this(initialCapacity, new OpCounter());
    }

    public DynamicArray(OpCounter counter) {
        this(DEFAULT_CAPACITY, counter);
    }

    public DynamicArray(int initialCapacity, OpCounter counter) {
        if (initialCapacity < 1) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        this.data = new int[initialCapacity];
        this.size = 0;
        this.counter = counter != null ? counter : new OpCounter();
    }

    @Override
    public void add(int element) {
        ensureCapacity(size + 1);
        data[size] = element;
        counter.move();
        size++;
    }

    @Override
    public void add(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        ensureCapacity(size + 1);
        for (int i = size - 1; i >= index; i--) {
            counter.step(); // read data[i]
            data[i + 1] = data[i];
            counter.move(); // shift element
        }
        data[index] = element;
        counter.move();
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        counter.step(); // read data[index]
        int removedValue = data[index];
        for (int i = index + 1; i < size; i++) {
            counter.step(); // read data[i]
            data[i - 1] = data[i];
            counter.move(); // shift element
        }
        size--;
        return removedValue;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        counter.step(); // read array cell
        return data[index];
    }

    @Override
    public boolean contains(int element) {
        for (int i = 0; i < size; i++) {
            counter.step();    // read data[i]
            counter.compare(); // compare data[i] with target
            if (data[i] == element) {
                return true;
            }
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
        size = 0;
    }

    public int getCapacity() {
        return data.length;
    }

    @Override
    public OpCounter getCounter() {
        return counter;
    }

    @Override
    public void setCounter(OpCounter counter) {
        this.counter = counter != null ? counter : new OpCounter();
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int newCapacity = data.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            int[] newData = new int[newCapacity];
            for (int i = 0; i < size; i++) {
                counter.step(); // read old cell
                newData[i] = data[i];
                counter.move(); // copy into new array
            }
            data = newData;
        }
    }
}
