package org.example.benchmark;

import org.example.structures.DynamicArray;
import org.example.structures.MinHeap;
import org.example.structures.MyLinkedList;
import org.openjdk.jol.info.GraphLayout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MemoryBenchmark {

    public static class MemoryRecord {
        public final String structure;
        public final int n;
        public final long bytes;
        public final double mb;

        public MemoryRecord(String structure, int n, long bytes) {
            this.structure = structure;
            this.n = n;
            this.bytes = bytes;
            this.mb = bytes / (1024.0 * 1024.0);
        }

        public String toCsvRow() {
            return String.format(java.util.Locale.US, "%s,%d,%d,%.6f", structure, n, bytes, mb);
        }
    }

    private static final int[] SIZES = {100, 1000, 10000, 100000};

    public static List<MemoryRecord> run() {
        List<MemoryRecord> records = new ArrayList<>();
        System.out.println("=== Starting JOL Memory Benchmark (Bonus Task A) ===");

        for (int n : SIZES) {
            System.out.println("Measuring memory for n = " + n + "...");

            // DynamicArray
            DynamicArray da = new DynamicArray(n);
            for (int i = 0; i < n; i++) da.add(i);
            long daBytes = GraphLayout.parseInstance(da).totalSize();
            records.add(new MemoryRecord("DynamicArray", n, daBytes));

            // MyLinkedList
            MyLinkedList ll = new MyLinkedList();
            for (int i = 0; i < n; i++) ll.add(i);
            long llBytes = GraphLayout.parseInstance(ll).totalSize();
            records.add(new MemoryRecord("MyLinkedList", n, llBytes));

            // MinHeap
            MinHeap mh = new MinHeap(n);
            for (int i = 0; i < n; i++) mh.insert(i);
            long mhBytes = GraphLayout.parseInstance(mh).totalSize();
            records.add(new MemoryRecord("MinHeap", n, mhBytes));
        }

        return records;
    }

    public static void saveCsv(List<MemoryRecord> records, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("structure,n,bytes,mb");
            for (MemoryRecord r : records) {
                writer.println(r.toCsvRow());
            }
        }
    }
}
