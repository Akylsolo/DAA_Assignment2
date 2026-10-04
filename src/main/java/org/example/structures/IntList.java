package org.example.structures;

import org.example.metrics.OpCounter;

/**
 * Common list interface for primitive int data structures.
 */
public interface IntList {
    void add(int element);

    void add(int index, int element);

    int remove(int index);

    int get(int index);

    boolean contains(int element);

    int size();

    boolean isEmpty();

    void clear();

    OpCounter getCounter();

    void setCounter(OpCounter counter);
}
