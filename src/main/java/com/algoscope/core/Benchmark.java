package com.algoscope.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Fair comparisons: every algorithm receives a fresh copy of the same input. */
public final class Benchmark {
    public record Entry(Algorithm algorithm, long comparisons, long writes, long nanos) { }

    public static List<Entry> compare(int[] input) {
        List<Entry> entries = new ArrayList<>();
        int[] expected = input.clone();
        Arrays.sort(expected);
        for (Algorithm algorithm : Algorithm.values()) {
            if (algorithm.isSearch()) continue;
            RunResult run = new Engine().run(algorithm, input, 0, false);
            if (!Arrays.equals(expected, run.result())) {
                throw new IllegalStateException(algorithm + " returned incorrect result");
            }
            Frame last = run.lastFrame();
            entries.add(new Entry(algorithm, last.comparisons(), last.writes(), run.elapsedNanos()));
        }
        return List.copyOf(entries);
    }

    private Benchmark() { }
}
