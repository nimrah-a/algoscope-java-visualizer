package com.algoscope;

import com.algoscope.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Dependency-free regression suite, runnable with java -ea. */
public final class EngineTest {
    private static int passed;

    public static void main(String[] args) {
        testSorts();
        testSearch();
        testFrames();
        testBenchmarks();
        testDatasets();
        System.out.println("PASS: " + passed + " AlgoScope regression assertions");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        passed++;
    }

    private static void testSorts() {
        int[][] cases = {{}, {5}, {5, 5, 5}, {3, 1, 2}, {-4, 9, 0, -4, 2}, {9, 8, 7, 6, 5, 4, 3, 2, 1}};
        Random random = new Random(321);
        for (Algorithm alg : Algorithm.values()) {
            if (alg.isSearch()) continue;
            for (int round = 0; round < 36; round++) {
                int[] original;
                if (round < cases.length) original = cases[round].clone();
                else {
                    original = new int[random.nextInt(60)];
                    for (int i = 0; i < original.length; i++) original[i] = random.nextInt(40) - 15;
                }
                int[] before = original.clone();
                int[] sorted = original.clone();
                Arrays.sort(sorted);
                for (boolean record : new boolean[]{true, false}) {
                    RunResult result = new Engine().run(alg, original, 0, record);
                    check(Arrays.equals(sorted, result.result()), alg + " incorrect result");
                    check(Arrays.equals(original, before), alg + " mutated caller input");
                    check(result.lastFrame().comparisons() >= 0, "negative comparisons");
                    check(result.frames().size() >= 1, "missing frames");
                }
            }
        }
    }

    private static void testSearch() {
        int[] a = {-3, 0, 0, 4, 9, 14};
        RunResult found = new Engine().run(Algorithm.BINARY, a, 9, true);
        check(found.foundIndex() == 4, "wrong search index");
        check(found.lastFrame().message().contains("Found"), "missing success message");
        check(new Engine().run(Algorithm.BINARY, a, 10, true).foundIndex() == -1, "missing value incorrectly found");
        check(new Engine().run(Algorithm.BINARY, new int[0], 2, true).foundIndex() == -1, "empty array search");
        check(new Engine().run(Algorithm.BINARY, new int[]{5}, 5, true).foundIndex() == 0, "singleton search");
        boolean rejected = false;
        try { new Engine().run(Algorithm.BINARY, new int[]{2, 1}, 1, true); }
        catch (IllegalArgumentException ex) { rejected = true; }
        check(rejected, "unsorted binary search was accepted");
    }

    private static void testFrames() {
        RunResult r = new Engine().run(Algorithm.BUBBLE, new int[]{3, 2, 1}, 0, true);
        check(r.frames().size() > 3, "too few animation frames");
        int[] copy = r.frames().get(0).values();
        copy[0] = 10000;
        check(r.frames().get(0).values()[0] == 3, "frame not immutable");
        int[] result = r.result();
        result[0] = 9000;
        check(r.result()[0] == 1, "result not immutable");
        long last = 0;
        for (Frame frame : r.frames()) {
            check(frame.comparisons() >= last, "comparisons decreased");
            last = frame.comparisons();
        }
    }

    private static void testBenchmarks() {
        int[] a = {4, 1, 7, 2, 0, 2};
        List<Benchmark.Entry> list = Benchmark.compare(a);
        check(list.size() == 5, "expected five sorts");
        check(Arrays.equals(a, new int[]{4, 1, 7, 2, 0, 2}), "benchmark mutated caller input");
        for (Benchmark.Entry e : list) check(e.comparisons() >= 0 && e.writes() >= 0, "invalid metrics");
    }

    private static void testDatasets() {
        for (Datasets.Shape shape : Datasets.Shape.values()) {
            int[] a = Datasets.create(35, shape, 88);
            check(a.length == 35, "wrong size");
            check(Arrays.equals(a, Datasets.create(35, shape, 88)), "seed not reproducible");
        }
        int[] ascending = Datasets.create(35, Datasets.Shape.ASCENDING, 88);
        int[] descending = Datasets.create(35, Datasets.Shape.DESCENDING, 88);
        check(ascending[0] <= ascending[34], "ascending incorrect");
        check(descending[0] >= descending[34], "descending incorrect");
    }

    private EngineTest() { }
}
