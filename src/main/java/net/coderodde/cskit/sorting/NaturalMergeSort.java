package net.coderodde.cskit.sorting;

/**
 * This class implements natural merge sort. Stability guaranteed.
 *
 * @author Rodion Efremov
 * @version 1.618-alpha
 */
public class NaturalMergeSort<E extends Comparable<? super E>>
implements ObjectSortingAlgorithm<E> {

    @Override
    public void sort(E[] array) {
        sort(array, new Range(0, array.length - 1));
    }

    @Override
    public void sort(E[] array, Range r) {
        if (r.from <= r.to) {
            ascendingSort(array, r.from, r.to);
        } else {
            descendingSort(array, r.to, r.from);
        }
    }

    private void ascendingSort(E[] array, int from, int to) {

    }

    private void descendingSort(E[] array, int from, int to) {
        
    }
}
