package net.coderodde.cskit.sorting;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class implements counting sort for objects. Stability guaranteed.
 *
 * @author Rodion Efremov
 * @version 1.618 (9.12.2013)
 */
public class CountingSort<E extends Comparable<? super E>>
implements ObjectSortingAlgorithm<E> {

    private final Comparator<Object> reverseComparator =
            new ReverseComparator();

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
        Map<E, LinkedList<E>> map = new HashMap<E, LinkedList<E>>();

        // Build the map from an element to its instances.
        for (int i = from; i <= to; ++i) {
            if (map.containsKey(array[i]) == false) {
                LinkedList<E> l = new LinkedList<E>();
                l.addLast(array[i]);
                map.put(array[i], l);
            } else {
                map.get(array[i]).addLast(array[i]);
            }
        }

        int i = 0;
        Object[] condensator = new Object[map.size()];

        // Load unique elements to condensator.
        for (E element : map.keySet()) {
            condensator[i++] = element;
        }

        Arrays.sort(condensator);

        int index = from;

        for (int j = 0; j < condensator.length; ++j) {
            for (E element : map.get(condensator[j])) {
                array[index++] = element;
            }
        }
    }

    private void descendingSort(E[] array, int from, int to) {
        Map<E, LinkedList<E>> map = new HashMap<E, LinkedList<E>>();

        for (int i = from; i <= to; ++i) {
            if (map.containsKey(array[i]) == false) {
                LinkedList<E> l = new LinkedList<E>();
                l.addLast(array[i]);
                map.put(array[i], l);
            } else {
                map.get(array[i]).addLast(array[i]);
            }
        }

        int i = 0;
        Object[] condensator = new Object[map.size()];

        for (E element : map.keySet()) {
            condensator[i++] = element;
        }

        Arrays.sort(condensator, new ReverseComparator());

        int index = from;
    }

    private class ReverseComparator implements Comparator<Object> {

        @Override
        public int compare(Object left, Object right) {
            E eLeft = (E) left;
            E eRight = (E) right;

            return eRight.compareTo(eLeft);
        }
    }
}
