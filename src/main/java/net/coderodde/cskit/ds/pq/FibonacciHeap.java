package net.coderodde.cskit.ds.pq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import static net.coderodde.cskit.Utilities.checkNotNull;

/**
 * This class implements Fibonacci heap.
 *
 * @author Rodion Efremov
 * @version 1.618033 (28.12.2013)
 */
public class FibonacciHeap<E, W extends Comparable<? super W>>
implements PriorityQueue<E, W>{

    private static final int DEFAULT_MAP_CAPACITY = 256;

    private static class Node<E, W extends Comparable<? super W>> {
        private final E datum;
        private Node<E, W> parent;
        private Node<E, W> left = this;
        private Node<E, W> right = this;
        private Node<E, W> child;
        private int degree;
        private boolean marked;
        private W priority;

        Node(E element, W priority) {
            this.datum = element;
            this.priority = priority;
        }
    }

    private static final double LOG_PHI = Math.log((1 + Math.sqrt(5)) / 2);

    private Map<E, Node<E, W>> map;
    private Node<E, W> minimumNode;
    private int size;

    public FibonacciHeap(int defaultMapCapacity) {
        map = new HashMap<E, Node<E, W>>(defaultMapCapacity);
    }

    public FibonacciHeap() {
        this(DEFAULT_MAP_CAPACITY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(E e, W priority) {
        checkNotNull(e, "Null elements not allowed in this Fibonacci heap");
        Node<E, W> node = new Node<E, W>(e, priority);

        if (minimumNode != null) {
            node.left = minimumNode;
            node.right = minimumNode.right;
            minimumNode.right = node;
            node.right.left = node;

            if (priority.compareTo(minimumNode.priority) < 0) {
                minimumNode = node;
            }
        } else {
            minimumNode = node;
        }

        ++size;
        map.put(e, node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decreasePriority(E e, W newPriority) {
        Node<E, W> x = map.get(e);

        if (e == null) {
            throw new NoSuchElementException(
                    "Node " + e.toString() + " is not contained in "
                    + "this Fibonacci heap."
                    );
        }

        if (x.priority.compareTo(newPriority) <= 0) {
            // newPriority is worse than the current priority of x.
            return;
        }

        x.priority = newPriority;
        Node<E, W> y = x.parent;

        if (y != null && x.priority.compareTo(y.priority) < 0) {
            cut(x, y);
            cascadingCut(y);
        }

        if (x.priority.compareTo(minimumNode.priority) < 0) {
            minimumNode = x;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E min() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "Trying to read the minimum element from an empty " +
                    "Fibonacci heap."
                    );
        }

        return minimumNode.datum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E extractMinimum() {
        if (isEmpty()) {
            throw new NoSuchElementException(
                    "Trying to extract from an empty Fibonacci heap."
                    );
        }

        Node<E, W> z = minimumNode;

        if (z != null) {
            int numKids = z.degree;
            Node<E, W> x = z.child;
            Node<E, W> tmpRight;

            while (numKids > 0) {
                tmpRight = x.right;

                x.left.right = x.right;
                x.right.left = x.left;

                x.left = minimumNode;
                x.right = minimumNode.right;
                minimumNode.right = x;
                x.right.left = x;

                x.parent = null;
                x = tmpRight;
                numKids--;
            }

            z.left.right = z.right;
            z.right.left = z.left;

            if (z == z.right) {
                minimumNode = null;
            } else {
                minimumNode = z.right;
                consolidate();
            }

            size--;
        }

        map.remove(z.datum);
        return z.datum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        minimumNode = null;
        map.clear();
        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(E element) {
        return map.containsKey(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PriorityQueue<E, W> newInstance() {
        return new FibonacciHeap<E, W>(this.map.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public W getPriority(E element) {
        if (map.containsKey(element) == false) {
            return null;
        }

        return map.get(element).priority;
    }

    private void consolidate() {
        int arraySize = ((int) Math.floor(Math.log(size) / LOG_PHI)) + 1;
        List<Node<E, W>> array = new ArrayList<Node<E, W>>(arraySize);

        for (int i = 0; i < arraySize; ++i) {
            array.add(null);
        }

        int numberOfRoots = 0;
        Node<E, W> x = minimumNode;

        if (x != null) {
            ++numberOfRoots;
            x = x.right;

            while (x != minimumNode) {
                ++numberOfRoots;
                x = x.right;
            }
        }

        while (numberOfRoots > 0) {
            int degree = x.degree;
            Node<E, W> next = x.right;

            for (;;) {
                Node<E, W> y = array.get(degree);

                if (y == null) {
                    break;
                }

                if (x.priority.compareTo(y.priority) > 0) {
                    Node<E, W> tmp = y;
                    y = x;
                    x = tmp;
                }

                link(y, x);
                array.set(degree, null);
                degree++;
            }

            array.set(degree, x);
            x = next;
            numberOfRoots--;
        }

        minimumNode = null;

        for (Node<E, W> y : array) {
            if (y == null) {
                continue;
            }

            if (minimumNode != null) {
                y.left.right = y.right;
                y.right.left = y.left;

                y.left = minimumNode;
                y.right = minimumNode.right;
                minimumNode.right = y;
                y.right.left = y;

                if (y.priority.compareTo(minimumNode.priority) < 0) {
                    minimumNode = y;
                }
            } else {
                minimumNode = y;
            }
        }
    }

    private void link(Node<E, W> y, Node<E, W> x) {
        y.left.right = y.right;
        y.right.left = y.left;

        y.parent = x;

        if (x.child == null) {
            x.child = y;
            y.right = y;
            y.left = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right = y;
            y.right.left = y;
        }

        ++x.degree;

        y.marked = false;
    }

    private void cut(Node<E, W> x, Node<E, W> y) {
        x.left.right = x.right;
        x.right.left = x.left;
        y.degree--;

        if (y.child == x) {
            y.child = x.right;
        }

        if (y.degree == 0) {
            y.child = null;
        }

        x.left = minimumNode;
        x.right = minimumNode.right;
        minimumNode.right = x;
        x.right.left = x;

        x.parent = null;
        x.marked = false;
    }

    private void cascadingCut(Node<E, W> y) {
        Node<E, W> z = y.parent;

        if (z != null) {
            if (y.marked == false) {
                y.marked = true;
            } else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }
}
