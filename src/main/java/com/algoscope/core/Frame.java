package com.algoscope.core;

import java.util.Arrays;

/** Immutable animation snapshot; highlighted indices are -1 when absent. */
public final class Frame {
    private final int[] values;
    private final int first;
    private final int second;
    private final int sortedFrom;
    private final long comparisons;
    private final long writes;
    private final String message;

    public Frame(int[] values, int first, int second, int sortedFrom,
                 long comparisons, long writes, String message) {
        this.values = Arrays.copyOf(values, values.length);
        this.first = first;
        this.second = second;
        this.sortedFrom = sortedFrom;
        this.comparisons = comparisons;
        this.writes = writes;
        this.message = message;
    }

    public int[] values() { return Arrays.copyOf(values, values.length); }
    public int first() { return first; }
    public int second() { return second; }
    public int sortedFrom() { return sortedFrom; }
    public long comparisons() { return comparisons; }
    public long writes() { return writes; }
    public String message() { return message; }
}
