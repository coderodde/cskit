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
        RunQueue queue = new RunScanner<E>().scan(array, from, to);
        E[] buffer = array.clone();
        Run last = queue.last();

        while (queue.size() > 1) {
            Run left = queue.first();
            Run right = queue.second();

            if (left == last) {
                queue.bounce();
                continue;
            }

            int l = left.from;
            int r = right.from;
            int i = l;
            final int LMAX = left.to + 1;
            final int RMAX = right.to + 1;

            while (l < LMAX && r < RMAX) {
                buffer[i++] = array[r].compareTo(array[i]) < 0 ?
                             array[r++] :
                             array[l++];
            }

            while (l < LMAX) buffer[i++] = array[l++];
            while (r < RMAX) buffer[i++] = array[r++];

            i = left.from;
            final int e = right.to + 1;

            while (i < e) {
                array[i] = buffer[i];
                ++i;
            }

            if (right == last) {
                queue.merge();
                queue.bounce();
                last = queue.last();
            } else {
                queue.merge();
                queue.bounce();
            }
        }
    }

    private void descendingSort(E[] array, int from, int to) {
        RunQueue queue = new RunScanner<E>().scan(array, to, from);
    }
}
