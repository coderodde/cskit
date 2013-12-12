package net.coderodde.cskit.sorting;

/**
 * This class implements so called "Batcher's method".
 *
 * @author Rodion Efremov
 * @version 1.618 (12.12.2013)
 */
public class BatchersSort<E extends Comparable<? super E>>
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
        final int n = to - from + 1;
        final int t = (int) Math.ceil(Math.log(n) / Math.log(2.0));

        for (int p = (int) Math.pow(2, t - 1); p > 0; p >>>= 1) {
            int r = 0;
            int d = p;

            for (int q = (int) Math.pow(2, t - 1); q >= p; q >>>= 1) {
                for (int i = 0; i < n - d; ++i) {
                    if ((i & p) == r) {
                        if (array[i].compareTo(array[i + d]) > 0) {
                            E tmp = array[i + d];
                            array[i + d] = array[i];
                            array[i] = tmp;
                        }
                    }
                }

                d = q - p;
                r = p;
            }
        }
    }

    private void descendingSort(E[] array, int to, int from) {
        final int n = to - from + 1;
        final int t = (int) Math.ceil(Math.log(n) / Math.log(2.0));

        for (int p = (int) Math.pow(2, t - 1); p > 0; p >>>= 1) {
            int r = 0;
            int d = p;

            for (int q = (int) Math.pow(2, t - 1); q >= p; q >>>= 1) {
                for (int i = 0; i < n - d; ++i) {
                    if ((i & p) == r) {
                        if (array[i].compareTo(array[i + d]) < 0) {
                            E tmp = array[i + d];
                            array[i + d] = array[i];
                            array[i] = tmp;
                        }
                    }
                }

                d = q - p;
                r = p;
            }
        }
    }
}
