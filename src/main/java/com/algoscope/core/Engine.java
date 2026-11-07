package com.algoscope.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Pure algorithm runner; frame recording is optional for low-overhead benchmarks. */
public final class Engine {
    private int[] a;
    private List<Frame> frames;
    private boolean recording;
    private long comparisons;
    private long writes;

    public RunResult run(Algorithm algorithm, int[] input, int target, boolean recordFrames) {
        Objects.requireNonNull(algorithm, "algorithm");
        Objects.requireNonNull(input, "input");
        this.a = input.clone();
        this.frames = new ArrayList<>();
        this.recording = recordFrames;
        this.comparisons = 0;
        this.writes = 0;
        if (algorithm.isSearch() && !isSorted(a)) {
            throw new IllegalArgumentException("Binary search requires ascending sorted values");
        }
        snapshot(-1, -1, -1, "Ready: " + algorithm.label());
        int found = -1;
        long start = System.nanoTime();
        switch (algorithm) {
            case BUBBLE -> bubble();
            case INSERTION -> insertion();
            case SELECTION -> selection();
            case MERGE -> mergeSort(0, a.length - 1);
            case QUICK -> quickSort(0, a.length - 1);
            case BINARY -> found = binarySearch(target);
        }
        long elapsed = System.nanoTime() - start;
        snapshot(found, -1, algorithm.isSearch() ? -1 : 0,
                algorithm.isSearch() ? (found >= 0 ? "Found " + target + " at index " + found : "Target " + target + " not found")
                        : "Sorting complete");
        // Benchmarks still return a minimal final frame, without recording intermediate operations.
        if (!recording) frames.add(new Frame(a, found, -1, algorithm.isSearch() ? -1 : 0,
                comparisons, writes, "Completed"));
        return new RunResult(algorithm, frames, a, found, elapsed);
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) if (arr[i - 1] > arr[i]) return false;
        return true;
    }

    private void snapshot(int i, int j, int sortedFrom, String message) {
        if (recording) frames.add(new Frame(a, i, j, sortedFrom, comparisons, writes, message));
    }

    private int compare(int i, int j) {
        comparisons++;
        snapshot(i, j, -1, "Compare indices " + i + " and " + j);
        return Integer.compare(a[i], a[j]);
    }

    private void swap(int i, int j) {
        if (i == j) return;
        int temp = a[i]; a[i] = a[j]; a[j] = temp;
        writes += 2;
        snapshot(i, j, -1, "Swap indices " + i + " and " + j);
    }

    private void write(int index, int value, String message) {
        a[index] = value;
        writes++;
        snapshot(index, -1, -1, message);
    }

    private void bubble() {
        for (int end = a.length - 1; end > 0; end--) {
            boolean swapped = false;
            for (int i = 0; i < end; i++) {
                if (compare(i, i + 1) > 0) { swap(i, i + 1); swapped = true; }
            }
            snapshot(-1, -1, end, "Sorted suffix begins at " + end);
            if (!swapped) break;
        }
    }

    private void insertion() {
        for (int i = 1; i < a.length; i++) {
            int value = a[i];
            int j = i - 1;
            while (j >= 0) {
                comparisons++;
                snapshot(j, j + 1, -1, "Compare with insertion value " + value);
                if (a[j] <= value) break;
                write(j + 1, a[j], "Shift value right");
                j--;
            }
            write(j + 1, value, "Insert value " + value);
        }
    }

    private void selection() {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) {
                if (compare(j, min) < 0) min = j;
            }
            swap(i, min);
            snapshot(i, -1, -1, "Placed minimum at index " + i);
        }
    }

    private void mergeSort(int lo, int hi) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(lo, mid);
        mergeSort(mid + 1, hi);
        int[] left = new int[mid - lo + 1];
        int[] right = new int[hi - mid];
        System.arraycopy(a, lo, left, 0, left.length);
        System.arraycopy(a, mid + 1, right, 0, right.length);
        int i = 0, j = 0, k = lo;
        while (i < left.length && j < right.length) {
            comparisons++;
            snapshot(lo + i, mid + 1 + j, -1, "Compare merge candidates");
            if (left[i] <= right[j]) write(k++, left[i++], "Merge: write left value");
            else write(k++, right[j++], "Merge: write right value");
        }
        while (i < left.length) write(k++, left[i++], "Merge: copy remaining left");
        while (j < right.length) write(k++, right[j++], "Merge: copy remaining right");
    }

    private void quickSort(int lo, int hi) {
        if (lo >= hi) return;
        int pivot = a[hi];
        int boundary = lo;
        for (int j = lo; j < hi; j++) {
            comparisons++;
            snapshot(j, hi, -1, "Compare to pivot " + pivot);
            if (a[j] <= pivot) swap(boundary++, j);
        }
        swap(boundary, hi);
        snapshot(boundary, -1, -1, "Pivot placed at index " + boundary);
        quickSort(lo, boundary - 1);
        quickSort(boundary + 1, hi);
    }

    private int binarySearch(int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            comparisons++;
            snapshot(mid, -1, -1, "Check index " + mid + " for " + target + " [" + lo + ", " + hi + "]");
            if (a[mid] == target) return mid;
            if (a[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }
}
