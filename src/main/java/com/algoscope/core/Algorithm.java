package com.algoscope.core;

/** Algorithms available in AlgoScope, with reference asymptotic complexities. */
public enum Algorithm {
    BUBBLE("Bubble Sort", "O(n)", "O(n²)", "O(1)", "Adjacent comparisons and swaps; stable."),
    INSERTION("Insertion Sort", "O(n)", "O(n²)", "O(1)", "Builds a sorted prefix; stable."),
    SELECTION("Selection Sort", "O(n²)", "O(n²)", "O(1)", "Selects the next smallest value; not stable."),
    MERGE("Merge Sort", "O(n log n)", "O(n log n)", "O(n)", "Divide-and-conquer merge; stable."),
    QUICK("Quick Sort", "O(n log n)", "O(n²)", "O(log n) average", "In-place Lomuto partition; worst case can be quadratic."),
    BINARY("Binary Search", "O(1)", "O(log n)", "O(1)", "Searches an ascending sorted array; reports an index or -1.");

    private final String label, best, worst, space, summary;

    Algorithm(String label, String best, String worst, String space, String summary) {
        this.label = label;
        this.best = best;
        this.worst = worst;
        this.space = space;
        this.summary = summary;
    }

    public String label() { return label; }
    public String best() { return best; }
    public String worst() { return worst; }
    public String space() { return space; }
    public String summary() { return summary; }
    public boolean isSearch() { return this == BINARY; }
    @Override public String toString() { return label; }
}
