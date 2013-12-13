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

        E[] destination = null;
        E[] source = null;

        final int NPASSES = (int) Math.ceil(Math.log(1.0 * queue.size())
                                          / Math.log(2.0));

        if ((NPASSES & 1) == 0) {
            destination = buffer;
            source = array;
        } else {
            destination = array;
            source = buffer;
        }

        // While there are more than one run, do merging:
        while (queue.size() > 1) {
            Run left = queue.first();

            if (left == last) {
                for (int i = left.from; i <= left.to; ++i) {
                    destination[i] = source[i];
                }

                E[] tmp = destination;
                destination = source;
                source = tmp;
                queue.bounce();
                continue;
            }

            Run right = queue.second();

            int l = left.from;
            int r = right.from;
            int i = l;

            final int LMAX = left.to + 1;
            final int RMAX = right.to + 1;

            while (l < LMAX && r < RMAX) {
                destination[i++] = source[r].compareTo(source[l]) < 0 ?
                        source[r++] :
                        source[l++];
            }

            while (l < LMAX) destination[i++] = source[l++];
            while (r < RMAX) destination[i++] = source[r++];

            if (right == last) {
                E[] tmp = destination;
                destination = source;
                source = tmp;
            }

            queue.merge();
            queue.bounce();
        }
    }

    private void descendingSort(E[] array, int from, int to) {
        RunQueue queue = new RunScanner<E>().scan(array, to, from);
    }
}
