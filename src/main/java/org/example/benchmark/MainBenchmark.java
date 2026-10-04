package org.example.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainBenchmark {

    public static void main(String[] args) {
        try {
            File resultsDir = new File("results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }

            // 1. Primary workloads (W1, W2, W3 head, W3 middle, W4)
            List<BenchmarkResult> results = BenchmarkRunner.runAll();
            BenchmarkRunner.saveCsv(results, "results/results.csv");
            System.out.println("Saved main results to results/results.csv");

            // 2. Bonus Task A: Memory Benchmark
            List<MemoryBenchmark.MemoryRecord> memRecords = MemoryBenchmark.run();
            MemoryBenchmark.saveCsv(memRecords, "results/memory_footprint.csv");
            System.out.println("Saved memory footprint to results/memory_footprint.csv");

            // 3. Bonus Task B: Floyd's buildHeap Benchmark
            List<FloydBenchmark.FloydRecord> floydRecords = FloydBenchmark.run();
            FloydBenchmark.saveCsv(floydRecords, "results/floyd_vs_insert.csv");
            System.out.println("Saved Floyd comparison to results/floyd_vs_insert.csv");

            System.out.println("=== All benchmarks completed successfully! ===");
        } catch (IOException e) {
            System.err.println("Benchmark failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
