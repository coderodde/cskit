package net.coderodde.cskit.sorting;

/**
 * This class defines runs, which are ascending or strictly
 * descending contiguous subsequences.
 *
 * @author Rodion Efremov
 * @version 1.618 (9.12.2013)
 */
public class Run {
    /**
     * The least index of a run.
     */
    public int from;

    /**
     * The greatest index of a run.
     */
    public int to;

    public Run() {
        this(0, 0);
    }

    public Run(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[" + from + ", " + to + "]";
    }
}
