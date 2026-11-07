package com.algoscope.core;

import java.util.Arrays;
import java.util.Random;

public final class Datasets {
    public enum Shape {
        RANDOM("Random"), ASCENDING("Ascending"), DESCENDING("Descending"), NEARLY_SORTED("Nearly sorted"), DUPLICATES("Many duplicates");
        private final String label;
        Shape(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    public static int[] create(int size, Shape shape, long seed) {
        if (size < 1 || size > 300) throw new IllegalArgumentException("Size must be 1–300");
        Random random = new Random(seed);
        int[] a = new int[size];
        for (int i = 0; i < size; i++) a[i] = 8 + random.nextInt(92);
        switch (shape) {
            case RANDOM -> { }
            case ASCENDING -> Arrays.sort(a);
            case DESCENDING -> {
                Arrays.sort(a);
                for (int i = 0; i < a.length / 2; i++) {
                    int tmp = a[i]; a[i] = a[a.length - 1 - i]; a[a.length - 1 - i] = tmp;
                }
            }
            case NEARLY_SORTED -> {
                Arrays.sort(a);
                for (int i = 0; i < Math.max(1, size / 12); i++) {
                    int x = random.nextInt(size), y = random.nextInt(size);
                    int tmp = a[x]; a[x] = a[y]; a[y] = tmp;
                }
            }
            case DUPLICATES -> {
                for (int i = 0; i < size; i++) a[i] = (1 + random.nextInt(5)) * 18;
            }
        }
        return a;
    }

    private Datasets() { }
}
