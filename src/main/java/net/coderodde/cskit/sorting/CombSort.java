package net.coderodde.cskit.sorting;

/**
 * Defines the comb sort algorithm. This one is an unstable sort.
 *
 * @author Rodion Efremov
 * @version 1.618 (8.12.2013)
 */
public class CombSort<E extends Comparable<? super E>>
implements ObjectSortingAlgorithm<E> {

    private static final float SHRINK_FACTOR = 1.3f;

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
        final int N = to - from + 1;
        int gap = N;
        boolean swapped = false;

        while (gap > 1 || swapped) {
            if (gap > 1) {
                gap = (int)((float) gap / SHRINK_FACTOR);
            }

            swapped = false;

            for (int i = from; gap + i < N; ++i) {
                if (array[i].compareTo(array[i + gap]) > 0) {
                    E tmp = array[i];
                    array[i] = array[i + gap];
                    array[i + gap] = tmp;
                    swapped = true;
                }
            }
        }
    }

    private void descendingSort(E[] array, int from, int to) {
        final int N = to - from + 1;
        int gap = N;
        boolean swapped = false;

        while (gap > 1 || swapped) {
            if (gap > 1) {
                gap = (int)((float) gap / SHRINK_FACTOR);
            }

            swapped = false;

            for (int i = from; gap + i < N; ++i) {
                if (array[i].compareTo(array[i + gap]) < 0) {
                    E tmp = array[i];
                    array[i] = array[i + gap];
                    array[i + gap] = tmp;
                    swapped = true;
                }
            }
        }
    }

}
