package org.example.structures;

import org.example.metrics.OpCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {

    private MinHeap heap;
    private OpCounter counter;

    @BeforeEach
    void setUp() {
        counter = new OpCounter();
        heap = new MinHeap(4, counter);
    }

    @Test
    @DisplayName("Empty heap edge cases")
    void testEmptyHeap() {
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());
    }

    @Test
    @DisplayName("Single element insert and extract")
    void testSingleElement() {
        heap.insert(42);
        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
        assertEquals(42, heap.peekMin());
        assertTrue(heap.verifyHeapProperty());

        int extracted = heap.extractMin();
        assertEquals(42, extracted);
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    @DisplayName("Heap property maintained after every insert and extractMin")
    void testHeapPropertyAfterEveryOperation() {
        Random rng = new Random(777);
        int n = 100;
        for (int i = 0; i < n; i++) {
            heap.insert(rng.nextInt(1000));
            assertTrue(heap.verifyHeapProperty(), "Heap property violated after insert at size " + heap.size());
        }

        while (!heap.isEmpty()) {
            heap.extractMin();
            assertTrue(heap.verifyHeapProperty(), "Heap property violated after extractMin at size " + heap.size());
        }
    }

    @Test
    @DisplayName("Sorted output: n extractMin calls produce non-decreasing sequence")
    void testSortedOutput() {
        Random rng = new Random(888);
        int n = 500;
        for (int i = 0; i < n; i++) {
            heap.insert(rng.nextInt(10000));
        }

        int prev = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int current = heap.extractMin();
            assertTrue(current >= prev, "Values not in non-decreasing order: " + prev + " > " + current);
            prev = current;
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Duplicate values handling")
    void testDuplicateValues() {
        heap.insert(5);
        heap.insert(3);
        heap.insert(5);
        heap.insert(2);
        heap.insert(3);
        heap.insert(2);

        assertTrue(heap.verifyHeapProperty());
        assertEquals(2, heap.extractMin());
        assertEquals(2, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Randomized correctness test against java.util.PriorityQueue")
    void testRandomizedAgainstJavaPriorityQueue() {
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        MinHeap actual = new MinHeap();
        Random rng = new Random(999);

        for (int op = 0; op < 5000; op++) {
            int action = rng.nextInt(3);
            int val = rng.nextInt(1000);

            if (action == 0 || expected.isEmpty()) {
                expected.add(val);
                actual.insert(val);
            } else if (action == 1) {
                assertEquals(expected.peek().intValue(), actual.peekMin());
            } else {
                assertEquals(expected.poll().intValue(), actual.extractMin());
            }

            assertEquals(expected.size(), actual.size());
            assertTrue(actual.verifyHeapProperty());
        }
    }

    @Test
    @DisplayName("Bonus Task B: Floyd's buildHeap builds valid heap in O(n)")
    void testFloydBuildHeap() {
        int[] data = {15, 3, 2, 8, 12, 1, 9, 7, 4, 10, 6};
        MinHeap floydHeap = MinHeap.buildHeap(data, new OpCounter());
        assertTrue(floydHeap.verifyHeapProperty());
        assertEquals(data.length, floydHeap.size());

        int prev = Integer.MIN_VALUE;
        while (!floydHeap.isEmpty()) {
            int val = floydHeap.extractMin();
            assertTrue(val >= prev);
            prev = val;
        }
    }
}
