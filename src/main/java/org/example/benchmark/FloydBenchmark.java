package org.example.benchmark;

import org.example.metrics.OpCounter;
import org.example.structures.MinHeap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FloydBenchmark {

    public static class FloydRecord {
        public final String method;
        public final int n;
        public final double timeMs;
        public final long steps;
        public final long moves;
        public final long comparisons;

        public FloydRecord(String method, int n, double timeMs, long steps, long moves, long comparisons) {
            this.method = method;
            this.n = n;
            this.timeMs = timeMs;
            this.steps = steps;
            this.moves = moves;
            this.comparisons = comparisons;
        }

        public String toCsvRow() {
            return String.format(java.util.Locale.US, "%s,%d,%.4f,%d,%d,%d",
                    method, n, timeMs, steps, moves, comparisons);
        }
    }

    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURE_RUNS = 5;

    public static List<FloydRecord> run() {
        List<FloydRecord> records = new ArrayList<>();
        System.out.println("=== Starting Floyd buildHeap vs n Inserts Benchmark (Bonus Task B) ===");

        for (int n : SIZES) {
            System.out.println("Comparing Floyd vs Inserts for n = " + n + "...");
            records.add(benchmarkMethod("Floyd_O(n)", n));
            records.add(benchmarkMethod("Sequential_O(n_log_n)", n));
        }

        return records;
    }

    private static int[] generateData(int n) {
        Random rng = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rng.nextInt(1_000_000);
        }
        return data;
    }

    private static FloydRecord benchmarkMethod(String method, int n) {
        int[] data = generateData(n);

        // Warmup
        for (int w = 0; w < WARMUP_RUNS; w++) {
            OpCounter cnt = new OpCounter();
            if ("Floyd_O(n)".equals(method)) {
                MinHeap.buildHeap(data, cnt);
            } else {
                MinHeap.buildByInsertions(data, cnt);
            }
        }

        // Measure
        double[] times = new double[MEASURE_RUNS];
        long finalSteps = 0, finalMoves = 0, finalComparisons = 0;

        for (int r = 0; r < MEASURE_RUNS; r++) {
            OpCounter cnt = new OpCounter();
            long start = System.nanoTime();
            if ("Floyd_O(n)".equals(method)) {
                MinHeap.buildHeap(data, cnt);
            } else {
                MinHeap.buildByInsertions(data, cnt);
            }
            long elapsed = System.nanoTime() - start;

            times[r] = elapsed / 1_000_000.0;
            finalSteps = cnt.getSteps();
            finalMoves = cnt.getMoves();
            finalComparisons = cnt.getComparisons();
        }

        Arrays.sort(times);
        double medianTime = times[MEASURE_RUNS / 2];

        return new FloydRecord(method, n, medianTime, finalSteps, finalMoves, finalComparisons);
    }

    public static void saveCsv(List<FloydRecord> records, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("method,n,time_ms,steps,moves,comparisons");
            for (FloydRecord r : records) {
                writer.println(r.toCsvRow());
            }
        }
    }
}
