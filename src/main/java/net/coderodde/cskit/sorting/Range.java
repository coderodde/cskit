package net.coderodde.cskit.sorting;

/**
 * This class defines a contiguous subsequence.
 *
 * @author Rodion Efremov
 * @version 1.618
 */
public final class Range {
    public int from;
    public int to;

    public Range(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public Range() {
        this(0, 0);
    }
}
