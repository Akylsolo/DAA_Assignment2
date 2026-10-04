package org.example.structures;

import org.example.metrics.OpCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {

    private DynamicArray array;
    private OpCounter counter;

    @BeforeEach
    void setUp() {
        counter = new OpCounter();
        array = new DynamicArray(4, counter);
    }

    @Test
    void testEmptyStructure() {
        assertTrue(array.isEmpty());
        assertEquals(0, array.size());
        assertFalse(array.contains(10));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 5));
    }

    @Test
    void testSingleElement() {
        array.add(42);
        assertFalse(array.isEmpty());
        assertEquals(1, array.size());
        assertEquals(42, array.get(0));
        assertTrue(array.contains(42));
        assertFalse(array.contains(100));

        int removed = array.remove(0);
        assertEquals(42, removed);
        assertTrue(array.isEmpty());
        assertEquals(0, array.size());
    }

    @Test
    void testDynamicResizing() {
        assertEquals(4, array.getCapacity());
        array.add(1);
        array.add(2);
        array.add(3);
        array.add(4);
        assertEquals(4, array.getCapacity());
        array.add(5);
        assertEquals(8, array.getCapacity());
        assertEquals(5, array.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, array.get(i));
        }
    }

    @Test
    void testAddAndRemoveBoundaries() {
        array.add(10);
        array.add(20);
        array.add(30);

        array.add(0, 5);
        assertEquals(5, array.get(0));
        assertEquals(10, array.get(1));
        assertEquals(4, array.size());

        array.add(2, 15);
        assertEquals(15, array.get(2));
        assertEquals(5, array.size());

        array.add(array.size(), 40);
        assertEquals(40, array.get(array.size() - 1));
        assertEquals(6, array.size());

        assertEquals(5, array.remove(0));
        assertEquals(10, array.get(0));

        assertEquals(15, array.remove(1));
        assertEquals(20, array.get(1));

        int lastIdx = array.size() - 1;
        assertEquals(40, array.remove(lastIdx));
        assertEquals(30, array.get(array.size() - 1));
    }

    @Test
    void testDuplicatesAndContains() {
        array.add(7);
        array.add(8);
        array.add(7);
        array.add(9);

        assertTrue(array.contains(7));
        assertTrue(array.contains(8));
        assertTrue(array.contains(9));
        assertFalse(array.contains(10));
    }

    @Test
    void testOpCounterTracking() {
        counter.reset();
        array.add(100);
        assertTrue(counter.getMoves() > 0);

        counter.reset();
        array.get(0);
        assertEquals(1, counter.getSteps());

        counter.reset();
        boolean found = array.contains(100);
        assertTrue(found);
        assertEquals(1, counter.getSteps());
        assertEquals(1, counter.getComparisons());
    }

    @Test
    void testRandomizedAgainstJavaList() {
        ArrayList<Integer> expected = new ArrayList<>();
        DynamicArray actual = new DynamicArray(10);
        Random rng = new Random(12345);

        for (int op = 0; op < 5000; op++) {
            int action = rng.nextInt(5);
            int val = rng.nextInt(1000);

            switch (action) {
                case 0 -> {
                    expected.add(val);
                    actual.add(val);
                }
                case 1 -> {
                    int idx = rng.nextInt(expected.size() + 1);
                    expected.add(idx, val);
                    actual.add(idx, val);
                }
                case 2 -> {
                    if (!expected.isEmpty()) {
                        int idx = rng.nextInt(expected.size());
                        assertEquals(expected.get(idx).intValue(), actual.get(idx));
                    }
                }
                case 3 -> {
                    if (!expected.isEmpty()) {
                        int idx = rng.nextInt(expected.size());
                        int expRemoved = expected.remove(idx);
                        int actRemoved = actual.remove(idx);
                        assertEquals(expRemoved, actRemoved);
                    }
                }
                case 4 -> {
                    int searchVal = rng.nextInt(1000);
                    assertEquals(expected.contains(searchVal), actual.contains(searchVal));
                }
            }
            assertEquals(expected.size(), actual.size());
        }

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).intValue(), actual.get(i));
        }
    }
}
