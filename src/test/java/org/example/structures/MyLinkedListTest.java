package org.example.structures;

import org.example.metrics.OpCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {

    private MyLinkedList list;
    private OpCounter counter;

    @BeforeEach
    void setUp() {
        counter = new OpCounter();
        list = new MyLinkedList(counter);
    }

    @Test
    void testEmptyList() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertNull(list.getHead());
        assertNull(list.getTail());
        assertFalse(list.contains(5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 99));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 99));
    }

    @Test
    void testSingleElement() {
        list.add(10);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(10, list.get(0));
        assertEquals(10, list.getHead().val);
        assertEquals(10, list.getTail().val);
        assertTrue(list.contains(10));
        assertFalse(list.contains(20));

        int removed = list.remove(0);
        assertEquals(10, removed);
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertNull(list.getHead());
        assertNull(list.getTail());
    }

    @Test
    void testAddAndRemoveBoundaries() {
        list.add(10);
        list.add(20);
        list.add(30);

        list.add(0, 5);
        assertEquals(5, list.get(0));
        assertEquals(10, list.get(1));
        assertEquals(4, list.size());

        list.add(2, 15);
        assertEquals(15, list.get(2));
        assertEquals(5, list.size());

        list.add(list.size(), 40);
        assertEquals(40, list.get(list.size() - 1));
        assertEquals(6, list.size());

        assertEquals(5, list.remove(0));
        assertEquals(10, list.get(0));

        assertEquals(15, list.remove(1));
        assertEquals(20, list.get(1));

        int lastIdx = list.size() - 1;
        assertEquals(40, list.remove(lastIdx));
        assertEquals(30, list.get(list.size() - 1));
    }

    @Test
    void testDuplicates() {
        list.add(5);
        list.add(10);
        list.add(5);
        list.add(20);

        assertTrue(list.contains(5));
        assertTrue(list.contains(10));
        assertTrue(list.contains(20));
        assertFalse(list.contains(30));
    }

    @Test
    void testOpCounter() {
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        counter.reset();
        int val = list.get(7);
        assertEquals(7, val);
        assertEquals(7, counter.getSteps());

        counter.reset();
        list.add(0, 999);
        assertTrue(counter.getMoves() > 0);
    }

    @Test
    void testRandomizedAgainstJavaLinkedList() {
        LinkedList<Integer> expected = new LinkedList<>();
        MyLinkedList actual = new MyLinkedList();
        Random rng = new Random(54321);

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
