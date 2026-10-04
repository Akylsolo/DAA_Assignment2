package org.example.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainBenchmark {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" DAA Assignment 2: In-Memory Workload Engine & Data Structures Benchmark");
        System.out.println(" Student: Muratbek Akyl | Group: SE-2523 | GitHub: Akylsolo");
        System.out.println("================================================================================\n");

        try {
            File resultsDir = new File("results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }

            System.out.println("[Step 1/3] Running Primary Workloads (W1, W2, W3, W4)...");
            List<BenchmarkResult> results = BenchmarkRunner.runAll();
            BenchmarkRunner.saveCsv(results, "results/results.csv");
            System.out.println(">>> Saved main benchmark results to results/results.csv\n");

            System.out.println("[Step 2/3] Running JOL Memory Footprint Benchmark (Bonus Task A)...");
            List<MemoryBenchmark.MemoryRecord> memRecords = MemoryBenchmark.run();
            MemoryBenchmark.saveCsv(memRecords, "results/memory_footprint.csv");
            System.out.println(">>> Saved memory footprint to results/memory_footprint.csv\n");

            System.out.println("[Step 3/3] Running Floyd's buildHeap Benchmark (Bonus Task B)...");
            List<FloydBenchmark.FloydRecord> floydRecords = FloydBenchmark.run();
            FloydBenchmark.saveCsv(floydRecords, "results/floyd_vs_insert.csv");
            System.out.println(">>> Saved Floyd comparison to results/floyd_vs_insert.csv\n");

            System.out.println("================================================================================");
            System.out.println(" All benchmarks completed successfully!");
            System.out.println(" Results files created:");
            System.out.println("   - results/results.csv");
            System.out.println("   - results/memory_footprint.csv");
            System.out.println("   - results/floyd_vs_insert.csv");
            System.out.println(" To regenerate plots, run: python generate_plots.py");
            System.out.println("================================================================================");
        } catch (IOException e) {
            System.err.println("Benchmark failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
