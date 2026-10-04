package org.example.benchmark;

import org.example.metrics.OpCounter;
import org.example.structures.DynamicArray;
import org.example.structures.IntList;
import org.example.structures.MinHeap;
import org.example.structures.MyLinkedList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BenchmarkRunner {

    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURE_RUNS = 5;

    public static List<BenchmarkResult> runAll() {
        List<BenchmarkResult> results = new ArrayList<>();

        System.out.println("=== Starting Workload Benchmarks ===");

        for (int n : SIZES) {
            System.out.println("Running benchmarks for n = " + n + "...");
            // W1 - Random Access
            results.add(benchmarkW1("DynamicArray", n));
            results.add(benchmarkW1("MyLinkedList", n));

            // W2 - Search
            results.add(benchmarkW2("DynamicArray", n));
            results.add(benchmarkW2("MyLinkedList", n));

            // W3 - Insert & Remove (head)
            results.add(benchmarkW3("DynamicArray", n, "head"));
            results.add(benchmarkW3("MyLinkedList", n, "head"));

            // W3 - Insert & Remove (middle)
            results.add(benchmarkW3("DynamicArray", n, "middle"));
            results.add(benchmarkW3("MyLinkedList", n, "middle"));

            // W4 - Priority Processing (MinHeap)
            results.add(benchmarkW4(n));
        }

        return results;
    }

    private static IntList createList(String structure, OpCounter counter) {
        if ("DynamicArray".equalsIgnoreCase(structure)) {
            return new DynamicArray(counter);
        } else if ("MyLinkedList".equalsIgnoreCase(structure)) {
            return new MyLinkedList(counter);
        }
        throw new IllegalArgumentException("Unknown structure: " + structure);
    }

    private static int[] generateData(int n) {
        Random rng = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rng.nextInt(1_000_000);
        }
        return data;
    }

    // W1: Fill with n values, perform 10,000 get(index) calls with a random index
    private static BenchmarkResult benchmarkW1(String structure, int n) {
        int[] initialData = generateData(n);
        int numQueries = 10_000;
        Random rngQuery = new Random(42);
        int[] queryIndices = new int[numQueries];
        for (int i = 0; i < numQueries; i++) {
            queryIndices[i] = rngQuery.nextInt(n);
        }

        // Warmup
        for (int w = 0; w < WARMUP_RUNS; w++) {
            IntList list = createList(structure, new OpCounter());
            for (int val : initialData) list.add(val);
            long sink = 0;
            for (int idx : queryIndices) {
                sink += list.get(idx);
            }
            if (sink == 42) System.out.print("");
        }

        // Measure
        double[] times = new double[MEASURE_RUNS];
        long finalSteps = 0, finalMoves = 0, finalComparisons = 0;

        for (int r = 0; r < MEASURE_RUNS; r++) {
            IntList list = createList(structure, new OpCounter());
            for (int val : initialData) list.add(val);

            OpCounter counter = new OpCounter();
            list.setCounter(counter);

            long start = System.nanoTime();
            long sink = 0;
            for (int idx : queryIndices) {
                sink += list.get(idx);
            }
            long elapsed = System.nanoTime() - start;
            if (sink == 42) System.out.print("");

            times[r] = elapsed / 1_000_000.0;
            finalSteps = counter.getSteps();
            finalMoves = counter.getMoves();
            finalComparisons = counter.getComparisons();
        }

        Arrays.sort(times);
        double medianTime = times[MEASURE_RUNS / 2];

        return new BenchmarkResult("W1", "-", structure, n, medianTime, finalSteps, finalMoves, finalComparisons);
    }

    // W2: Perform 1,000 contains(x) queries, half present and half not
    private static BenchmarkResult benchmarkW2(String structure, int n) {
        int[] initialData = generateData(n);
        int numQueries = 1_000;
        int half = numQueries / 2;
        int[] queries = new int[numQueries];

        Random rngQ = new Random(42);
        // Half present
        for (int i = 0; i < half; i++) {
            int randomIdx = rngQ.nextInt(n);
            queries[i] = initialData[randomIdx];
        }
        // Half not present (negative integers, since initialData has positive numbers)
        for (int i = half; i < numQueries; i++) {
            queries[i] = -1 - rngQ.nextInt(1_000_000);
        }

        // Warmup
        for (int w = 0; w < WARMUP_RUNS; w++) {
            IntList list = createList(structure, new OpCounter());
            for (int val : initialData) list.add(val);
            int hits = 0;
            for (int q : queries) {
                if (list.contains(q)) hits++;
            }
            if (hits == -1) System.out.print("");
        }

        // Measure
        double[] times = new double[MEASURE_RUNS];
        long finalSteps = 0, finalMoves = 0, finalComparisons = 0;

        for (int r = 0; r < MEASURE_RUNS; r++) {
            IntList list = createList(structure, new OpCounter());
            for (int val : initialData) list.add(val);

            OpCounter counter = new OpCounter();
            list.setCounter(counter);

            long start = System.nanoTime();
            int hits = 0;
            for (int q : queries) {
                if (list.contains(q)) hits++;
            }
            long elapsed = System.nanoTime() - start;
            if (hits == -1) System.out.print("");

            times[r] = elapsed / 1_000_000.0;
            finalSteps = counter.getSteps();
            finalMoves = counter.getMoves();
            finalComparisons = counter.getComparisons();
        }

        Arrays.sort(times);
        double medianTime = times[MEASURE_RUNS / 2];

        return new BenchmarkResult("W2", "-", structure, n, medianTime, finalSteps, finalMoves, finalComparisons);
    }

    // W3: Insert & Remove (1,000 insertions and 1,000 removals at head or middle)
    private static BenchmarkResult benchmarkW3(String structure, int n, String variant) {
        int[] initialData = generateData(n);
        int opCount = 1_000;
        int[] insertValues = new int[opCount];
        Random rngIns = new Random(42);
        for (int i = 0; i < opCount; i++) {
            insertValues[i] = rngIns.nextInt(1_000_000);
        }

        // Warmup
        for (int w = 0; w < WARMUP_RUNS; w++) {
            IntList list = createList(structure, new OpCounter());
            for (int val : initialData) list.add(val);

            if ("head".equalsIgnoreCase(variant)) {
                for (int val : insertValues) list.add(0, val);
                for (int i = 0; i < opCount; i++) list.remove(0);
            } else {
                for (int val : insertValues) list.add(list.size() / 2, val);
                for (int i = 0; i < opCount; i++) list.remove(list.size() / 2);
            }
        }

        // Measure
        double[] times = new double[MEASURE_RUNS];
        long finalSteps = 0, finalMoves = 0, finalComparisons = 0;

        for (int r = 0; r < MEASURE_RUNS; r++) {
            IntList list = createList(structure, new OpCounter());
            for (int val : initialData) list.add(val);

            OpCounter counter = new OpCounter();
            list.setCounter(counter);

            long start = System.nanoTime();
            if ("head".equalsIgnoreCase(variant)) {
                for (int val : insertValues) {
                    list.add(0, val);
                }
                for (int i = 0; i < opCount; i++) {
                    list.remove(0);
                }
            } else {
                for (int val : insertValues) {
                    list.add(list.size() / 2, val);
                }
                for (int i = 0; i < opCount; i++) {
                    list.remove(list.size() / 2);
                }
            }
            long elapsed = System.nanoTime() - start;

            times[r] = elapsed / 1_000_000.0;
            finalSteps = counter.getSteps();
            finalMoves = counter.getMoves();
            finalComparisons = counter.getComparisons();
        }

        Arrays.sort(times);
        double medianTime = times[MEASURE_RUNS / 2];

        return new BenchmarkResult("W3", variant, structure, n, medianTime, finalSteps, finalMoves, finalComparisons);
    }

    // W4: Priority Processing: MinHeap. Insert n values, then call extractMin() n times, check non-decreasing
    private static BenchmarkResult benchmarkW4(int n) {
        int[] initialData = generateData(n);

        // Warmup
        for (int w = 0; w < WARMUP_RUNS; w++) {
            MinHeap heap = new MinHeap(new OpCounter());
            for (int val : initialData) {
                heap.insert(val);
            }
            int prev = Integer.MIN_VALUE;
            while (!heap.isEmpty()) {
                int min = heap.extractMin();
                if (min < prev) throw new AssertionError("Heap sort order violated");
                prev = min;
            }
        }

        // Measure
        double[] times = new double[MEASURE_RUNS];
        long finalSteps = 0, finalMoves = 0, finalComparisons = 0;

        for (int r = 0; r < MEASURE_RUNS; r++) {
            OpCounter counter = new OpCounter();
            MinHeap heap = new MinHeap(counter);

            long start = System.nanoTime();
            for (int val : initialData) {
                heap.insert(val);
            }
            int prev = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                int min = heap.extractMin();
                if (min < prev) throw new AssertionError("Heap sort order violated");
                prev = min;
            }
            long elapsed = System.nanoTime() - start;

            times[r] = elapsed / 1_000_000.0;
            finalSteps = counter.getSteps();
            finalMoves = counter.getMoves();
            finalComparisons = counter.getComparisons();
        }

        Arrays.sort(times);
        double medianTime = times[MEASURE_RUNS / 2];

        return new BenchmarkResult("W4", "-", "MinHeap", n, medianTime, finalSteps, finalMoves, finalComparisons);
    }

    public static void saveCsv(List<BenchmarkResult> results, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            for (BenchmarkResult res : results) {
                writer.println(res.toCsvRow());
            }
        }
    }
}
