package org.example.metrics;

public class OpCounter {
    private long steps;
    private long moves;
    private long comparisons;

    public OpCounter() {
        this.steps = 0;
        this.moves = 0;
        this.comparisons = 0;
    }

    public void step() {
        this.steps++;
    }

    public void addSteps(long delta) {
        this.steps += delta;
    }

    public void move() {
        this.moves++;
    }

    public void addMoves(long delta) {
        this.moves += delta;
    }

    public void compare() {
        this.comparisons++;
    }

    public void addComparisons(long delta) {
        this.comparisons += delta;
    }

    public long getSteps() {
        return steps;
    }

    public long getMoves() {
        return moves;
    }

    public long getComparisons() {
        return comparisons;
    }

    public void reset() {
        this.steps = 0;
        this.moves = 0;
        this.comparisons = 0;
    }

    @Override
    public String toString() {
        return "OpCounter{" +
                "steps=" + steps +
                ", moves=" + moves +
                ", comparisons=" + comparisons +
                '}';
    }
}
