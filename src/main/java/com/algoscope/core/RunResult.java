package com.algoscope.core;

import java.util.List;

public final class RunResult {
    private final Algorithm algorithm;
    private final List<Frame> frames;
    private final int[] result;
    private final int foundIndex;
    private final long elapsedNanos;

    public RunResult(Algorithm algorithm, List<Frame> frames, int[] result, int foundIndex, long elapsedNanos) {
        this.algorithm = algorithm;
        this.frames = List.copyOf(frames);
        this.result = result.clone();
        this.foundIndex = foundIndex;
        this.elapsedNanos = elapsedNanos;
    }

    public Algorithm algorithm() { return algorithm; }
    public List<Frame> frames() { return frames; }
    public int[] result() { return result.clone(); }
    public int foundIndex() { return foundIndex; }
    public long elapsedNanos() { return elapsedNanos; }
    public Frame lastFrame() { return frames.get(frames.size() - 1); }
}
