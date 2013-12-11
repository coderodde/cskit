package net.coderodde.cskit.sorting;

/**
 * Defines the common API for sorting algorithms.
 *
 * @author Rodion Efremov
 * @version 1.618
 */
public interface ObjectSortingAlgorithm<E extends Comparable<? super E>> {

    /**
     * Sorts the entire array.
     *
     * @param array the array to sort.
     */
    public void sort(E[] array);

    /**
     * Sorts the subsequence defined by range <code>r</code>.
     *
     * @param array the array to sort.
     * @param r  the range to sort.
     */
    public void sort(E[] array, Range r);
}
