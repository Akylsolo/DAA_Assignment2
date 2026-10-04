# DAA Assignment 2: Data Structures & In-Memory Workload Engine

**Author:** Muratbek Akyl  
**Group:** SE-2523  
**GitHub:** [Akylsolo](https://github.com/Akylsolo)  
**Repository:** [DAA_Assignment2](https://github.com/Akylsolo/DAA_Assignment2)

---

## Project Overview

This project implements three fundamental data structures from scratch in Java using primitive `int` storage (no boxed `Integer` or standard collections):
1. **`DynamicArray`**: Resizable array with 2x amortized growth, direct indexing, and shifts.
2. **`MyLinkedList`**: Doubly linked list with explicit pointer links and traversal mechanics.
3. **`MinHeap`**: Array-based binary min-heap with bubble-up, bubble-down, and Floyd's $O(n)$ bottom-up `buildHeap`.

All physical operations (`steps`, `moves`, `comparisons`) are tracked directly inside data structure methods via `OpCounter`.

---

## Project Structure

```text
DAA_Assignment2/
├── pom.xml
├── README.md
├── REPORT.md
├── generate_plots.py
├── results/
│   ├── results.csv
│   ├── memory_footprint.csv
│   ├── floyd_vs_insert.csv
│   └── plots/
│       ├── w1_time_vs_n.png
│       ├── w1_ops_vs_n.png
│       ├── w2_time_vs_n.png
│       ├── w2_ops_vs_n.png
│       ├── w3_head_time_vs_n.png
│       ├── w3_head_ops_vs_n.png
│       ├── w3_middle_time_vs_n.png
│       ├── w3_middle_ops_vs_n.png
│       ├── w4_time_and_ops_vs_n.png
│       ├── bonus_memory_vs_n.png
│       └── bonus_floyd_vs_insert.png
└── src/
    ├── main/java/org/example/
    │   ├── metrics/
    │   │   └── OpCounter.java
    │   ├── structures/
    │   │   ├── IntList.java
    │   │   ├── DynamicArray.java
    │   │   ├── MyLinkedList.java
    │   │   └── MinHeap.java
    │   └── benchmark/
    │       ├── BenchmarkResult.java
    │       ├── BenchmarkRunner.java
    │       ├── MemoryBenchmark.java
    │       ├── FloydBenchmark.java
    │       └── MainBenchmark.java
    └── test/java/org/example/
        ├── metrics/
        │   └── OpCounterTest.java
        └── structures/
            ├── DynamicArrayTest.java
            ├── MyLinkedListTest.java
            └── MinHeapTest.java
```

---

## Build & Run Instructions

### Prerequisites
- Java Development Kit (JDK 21+ or OpenJDK 26)
- Apache Maven 3.8+
- Python 3.9+ with `matplotlib` (for generating plots)

### 1. Compile the Project
```bash
mvn clean compile
```

### 2. Run Tests (JUnit 5)
```bash
mvn test
```
All 22 unit tests test edge cases (empty collections, single element, boundary indices, invalid arguments, duplicates) and correctness against `java.util` collections.

### 3. Run Benchmarks
To execute workloads W1-W4, JOL memory analysis (Bonus A), and Floyd's heap comparison (Bonus B), outputting all results to `results/results.csv`:
```bash
mvn exec:java -Dexec.mainClass="org.example.benchmark.MainBenchmark"
```
Or directly using `java`:
```bash
java -cp "target/classes;target/dependency/*" org.example.benchmark.MainBenchmark
```

### 4. Generate Plots
```bash
python generate_plots.py
```
Generated charts will be stored under `results/plots/`.
