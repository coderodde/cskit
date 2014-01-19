package net.coderodde.cskit.sorting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.coderodde.cskit.ds.pq.BinaryHeap;
import net.coderodde.cskit.ds.pq.PriorityQueue;

/**
 * Implements the heap sort.
 *
 * @author Rodion Efremov
 * @version 1.618033 (18.1.2014)
 */
public class HeapSort<E extends Comparable<? super E>>
implements ObjectSortingAlgorithm<E> {

    private Map<E, List<E>> MAP;
    private PriorityQueue<E, E> HEAP;

    public HeapSort(PriorityQueue<E, E> heap) {
        this.HEAP = heap.newInstance();
        this.MAP = new HashMap<E, List<E>>();
    }

    public HeapSort() {
        this(new BinaryHeap<E, E>());
    }

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
        HEAP.clear();
        MAP.clear();

        for (int i = from; i <= to; ++i) {
            if (MAP.containsKey(array[i])) {
                MAP.get(array[i]).add(array[i]);
            } else {
                List<E> list = new LinkedList<E>();
                list.add(array[i]);
                MAP.put(array[i], list);
                HEAP.insert(array[i], array[i]);
            }
        }

        int index = from;

//        System.out.println((to - from + 1) + ":" + HEAP.size());

        List<E> list = null;

        while (index <= to) {
            if (list == null) {
                list = MAP.get(HEAP.extractMinimum());
            }

            for (E element : list) {
                array[index++] = element;
            }

            list = null;
        }
    }

    private void descendingSort(E[] array, int from, int to) {
        HEAP.clear();

        for (int i = from; i <= to; ++i) {
            HEAP.insert(array[i], array[i]);
        }

        int index = to;

        while (index >= from) {
            array[index--] = HEAP.extractMinimum();
        }
    }
}
