package org.example.metrics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OpCounterTest {

    @Test
    void testInitialValues() {
        OpCounter counter = new OpCounter();
        assertEquals(0, counter.getSteps());
        assertEquals(0, counter.getMoves());
        assertEquals(0, counter.getComparisons());
    }

    @Test
    void testIncrementAndReset() {
        OpCounter counter = new OpCounter();
        counter.step();
        counter.addSteps(4);
        counter.move();
        counter.addMoves(2);
        counter.compare();
        counter.addComparisons(9);

        assertEquals(5, counter.getSteps());
        assertEquals(3, counter.getMoves());
        assertEquals(10, counter.getComparisons());

        counter.reset();
        assertEquals(0, counter.getSteps());
        assertEquals(0, counter.getMoves());
        assertEquals(0, counter.getComparisons());
    }
}
