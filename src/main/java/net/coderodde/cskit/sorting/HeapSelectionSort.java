package net.coderodde.cskit.sorting;

/**
 * This class implements an adaptive, unstable sorting algorithm I came to call
 * "Heap selection sort", and which runs in time <tt>O(n log(m))</tt>, where
 * <code>n</code> is the amount of elements and <code>m</code> is the amount
 * of runs in the input array.
 *
 * @author Rodion Efremov
 * @version 1.618 (14.12.2013)
 */
public class HeapSelectionSort<E extends Comparable<? super E>>
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
        E[] buffer = array.clone();

        RunHeap<E> heap = new RunScanner<E>()
                .scanAndReturnRunHeap(buffer, from, to);

        int index = 0;
        final int N = to - from + 1;

        while (index < N - 1) {
            array[index++] = heap.min();
            heap.inc();
        }

        array[index] = heap.min();
    }

    private void descendingSort(E[] array, int from, int to) {
        E[] buffer = array.clone();

        RunHeap<E> heap = new RunScanner<E>()
                .scanAndReturnRunHeap(array, to, from);

        int index = 0;
        final int N = to - from + 1;

        while (index < N) {
            array[index++] = heap.min();
            heap.inc();
        }
    }
}
