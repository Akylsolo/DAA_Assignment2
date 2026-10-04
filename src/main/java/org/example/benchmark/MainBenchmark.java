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

            List<BenchmarkResult> results = BenchmarkRunner.runAll();
            BenchmarkRunner.saveCsv(results, "results/results.csv");

            List<MemoryBenchmark.MemoryRecord> memRecords = MemoryBenchmark.run();
            MemoryBenchmark.saveCsv(memRecords, "results/memory_footprint.csv");

            List<FloydBenchmark.FloydRecord> floydRecords = FloydBenchmark.run();
            FloydBenchmark.saveCsv(floydRecords, "results/floyd_vs_insert.csv");

            System.out.println("Benchmarks completed successfully.");
        } catch (IOException e) {
            System.err.println("Benchmark failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
