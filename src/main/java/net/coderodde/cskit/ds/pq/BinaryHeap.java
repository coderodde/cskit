package net.coderodde.cskit.ds.pq;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import net.coderodde.cskit.ds.pq.PriorityQueue;

/**
 * This class implements the binary minimum heap (a priority queue type).
 *
 * @author Rodion Efremov
 * @version 1.618 (11.12.2013)
 */
public class BinaryHeap<E, W extends Comparable<? super W>>
implements PriorityQueue<E, W> {

    private static final float LOAD_FACTOR = 1.05f;
    private static final int DEFAULT_CAPACITY = 1024;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public PriorityQueue<E, W> newInstance() {
        return new BinaryHeap<E, W>(nodeArray.length);
    }

    private static class HeapNode<E, W> {
        E element;
        W priority;
        int index;

        HeapNode(E element, W priority) {
            this.element = element;
            this.priority = priority;
        }
    }

    /**
     * The amount of elements in this heap.
     */
    private int size;
    private HeapNode<E, W>[] nodeArray;
    private Map<E, HeapNode<E, W>> map;

    /**
     * Constructs a binary heap with initial capacity <code>capacity</code>.
     *
     * @param capacity the initial capacity of this heap.
     */
    public BinaryHeap(int capacity) {
        capacity = checkCapacity(capacity);
        this.nodeArray = new HeapNode[capacity];
        this.map = new HashMap<E, HeapNode<E, W>>(capacity, LOAD_FACTOR);
    }

    /**
     * Construct a binary heap.
     */
    public BinaryHeap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Inserts an element if not already present.
     *
     * @param e the element to insert.
     * @param priority the priority of the element.
     */
    @Override
    public void insert(E e, W priority) {
        if (map.containsKey(e)) {
            return;
        }

        if (size == nodeArray.length) {
            extendArray();
        }

        int index = size;
        HeapNode<E, W> node = new HeapNode<E, W>(e, priority);

        // Sift up the node containing the input element until min-heap property
        // is arranged.
        while (index > 0) {
            int parent = (index - 1) >>> 1;
            HeapNode<E, W> p = nodeArray[parent];

            if (priority.compareTo(p.priority) >= 0) {
                break;
            }

            nodeArray[index] = p;
            p.index = index;
            index = parent;
        }

        nodeArray[index] = node;
        node.index = index;
        map.put(e, node);
        ++size;
    }

    @Override
    public void decreasePriority(E e, W newPriority) {
        HeapNode<E, W> node = map.get(e);

        if (node == null || node.priority.compareTo(newPriority) <= 0) {
            return;
        }

        node.priority = newPriority;
        int index = node.index;
        int parentIndex = (index - 1) >> 1;

        for (;;) {
            if (parentIndex >= 0
                    && nodeArray[parentIndex]
                      .priority.compareTo(node.priority) > 0) {
                nodeArray[index] = nodeArray[parentIndex];
                nodeArray[index].index = index;
                index = parentIndex;
                parentIndex = (index - 1) >> 1;
            } else {
                nodeArray[index] = node;
                node.index = index;
                return;
            }
        }
    }

    @Override
    public E min() {
        if (size == 0) {
            throw new NoSuchElementException("Reading from an empty heap.");
        }

        return nodeArray[0].element;
    }

    @Override
    public E extractMinimum() {
        if (size == 0) {
            throw new NoSuchElementException("Extracting from an empty queue.");
        }

        E element = nodeArray[0].element;
        map.remove(element);
        HeapNode<E, W> node = nodeArray[--size];
        nodeArray[size] = null;

        int nodeIndex = 0;
        int leftChildIndex = 1;
        int rightChildIndex = 2;

        for (;;) {
            int minChildIndex;

            if (leftChildIndex < size) {
                minChildIndex = leftChildIndex;
            } else {
                nodeArray[nodeIndex] = node;
                node.index = nodeIndex;
                return element;
            }

            if (rightChildIndex < size
                    && nodeArray[leftChildIndex].priority
                      .compareTo(nodeArray[rightChildIndex].priority) > 0) {
                minChildIndex = rightChildIndex;
            }

            if (node.priority
                    .compareTo(nodeArray[minChildIndex].priority) > 0) {
                nodeArray[nodeIndex] = nodeArray[minChildIndex];
                nodeArray[nodeIndex].index = nodeIndex;

                nodeIndex = minChildIndex;
                leftChildIndex = (nodeIndex << 1) + 1;
                rightChildIndex = leftChildIndex + 1;
            } else {
                nodeArray[nodeIndex] = node;
                node.index = nodeIndex;
                return element;
            }
        }
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public boolean contains(E element) {
        return map.containsKey(element);
    }

    private int checkCapacity(int capacity) {
        return capacity < 16 ? 16 : capacity;
    }

    private void extendArray() {
        int capacity = (size * 3) / 2;
        HeapNode[] array = new HeapNode[capacity];
        System.arraycopy(nodeArray, 0, array, 0, size);
        nodeArray = array;
    }
}
