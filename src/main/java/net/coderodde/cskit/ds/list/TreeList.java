package net.coderodde.cskit.ds.list;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * This class implements an AVL-tree based list, providing efficient access
 * to elements.
 *
 * @author Rodion Efremov
 * @version 1.618033 (11.1.2014)
 */
public class TreeList<E>
extends AbstractList<E>
implements Deque<E>, Serializable, Cloneable {

    public static boolean DEBUG_MSG = true;

    private static final int DEFAULT_DEGREE = 128;

    /**
     * This class implements a node in the AVL-tree containing a
     * contiguous sublist with at most <tt>degree</tt> elements.
     *
     * @param <E> the element type.
     */
    static class Node<E> {

        /**
         * The left subtree.
         */
        Node<E> left;

        /**
         * The right subtree.
         */
        Node<E> right;

        /**
         * The parent node.
         */
        Node<E> parent;

        /**
         * The height of this node.
         */
        int height;

        /**
         * The element amount of the left subtree.
         */
        int leftCount;

        /**
         * The array for storing elements.
         */
        Object[] array;

        /**
         * The leftmost index of the element array.
         */
        int first;

        /**
         * The rightmost index of the element array.
         */
        int last;

        Node(final int degree) {
            this.array = new Object[degree];
            this.last = -1;
        }

        int size() {
            return last - first + 1;
        }

        /**
         * Appends an element to this node's array.
         *
         * @param element element to append.
         */
        void add(E element) {
            if (last == array.length - 1) {
                final int left = Math.max((array.length - size()) >> 1, 1);

                for (int i = first; i <= last; ++i) {
                    array[i - left] = array[i];
                }

                for (int i = last - left + 2, j = 1; j < left; ++j, ++i) {
                    array[i] = null;
                }

                first -= left;
                last -= left;
            }

            array[++last] = element;
        }

        /**
         * Inserts an element into this node's array.
         *
         * @param index the index to insert at.
         * @param element the element to insert.
         */
        void add(int index, E element) {
            final int elementsBefore = index;
            final int elementsAfter = last - first + 1 - index; // 16 - 12 = 3

            if (elementsBefore < elementsAfter) {
                if (first > 0) {
                    // Shift left.
                    for (int i = first, j = 0; j < elementsBefore; ++j, ++i) {
                        array[i - 1] = array[i];
                    };

                    --first;
                } else {
                    // Shift right.
                    for (int i = last, j = 0; j < elementsAfter; ++j, --i) {
                        array[i + 1] = array[i];
                    }

                    ++last;
                }
            } else {
                // elementsBefore >= elements
                if (last < array.length - 1) {
                    // Shift right.
                    for (int i = last, j = 0; j < elementsAfter; ++j, --i) {
                        array[i + 1] = array[i];
                    }

                    ++last;
                } else {
                    // Shift left.
                    for (int i = first, j = 0; j < elementsBefore; ++j, ++i) {
                        array[i - 1] = array[i];
                    }

                    --first;
                }
            }

            // Do insert.
            array[index + first] = element;
        }

        E remove(int index) {
            E old = (E) array[index + first];

            final int elementsBefore = index;
            final int elementsAfter = last - index - first;

            if (elementsBefore < elementsAfter) {
                // Shift left part to right.
                for (int i = index + first - 1; i >= first; --i) {
                    array[i + 1] = array[i];
                }

                array[first++] = null;
            } else {
                // elementsBefore >= elementsAfter
                // Shift right part to left.
                for (int i = index + first; i < last; ++i) {
                    array[i] = array[i + 1];
                }

                array[last--] = null;
            }

            return old;
        }

        void clear() {
            for (int i = first; i <= last; ++i) {
                array[i] = null;
            }

            first = 0;
            last = 0;
        }

        /**
         * Assumes that this node is full.
         *
         * @return the right node.
         */
        Node<E> split() {
            // TODO: make this run faster.
            final int degree = this.array.length;

            Node<E> newNode = new Node<E>(degree);

            final int leftElements = degree >> 1;
            final int leftSkip = (degree - leftElements) >> 1;
            final int rightElements = degree - leftElements;
            final int rightSkip = (degree - rightElements) >> 1;

            this.first = leftSkip;
            this.last = leftSkip + leftElements - 1;
            newNode.first = rightSkip;
            newNode.last = rightSkip + rightElements - 1;

            int limit = rightSkip + rightElements;

            // Load the right block of this to newNode.
            for (int i = rightSkip, j = leftElements;
                    i < limit;
                    ++i, ++j) {
                newNode.array[i] = this.array[j];
                this.array[j] = null;
            }

            if (leftSkip > 0) {
                for (int i = leftElements - 1; i >= 0; --i) {
                    this.array[i + leftSkip] = this.array[i];
                    this.array[i] = null;
                }
            }

            return newNode;
        }

        Node<E> min() {
            Node<E> e = this;

            while (e.left != null) {
                e = e.left;
            }

            return e;
        }

        Node<E> max() {
            Node<E> e = this;

            while (e.right != null) {
                e = e.right;
            }

            return e;
        }

        Node<E> successor() {
            if (right != null) {
                return right.min();
            }

            Node<E> n = this;

            while (n.parent != null && n.parent.right == n) {
                n = n.parent;
            }

            return n.parent;
        }

        Node<E> predecessor() {
            if (left != null) {
                return left.max();
            }

            Node<E> n = this;

            while (n.parent != null && n.parent.left == n) {
                n = n.parent;
            }

            return n.parent;
        }
    }

    private final int degree;
    private int size;
    private Node<E> root;
    private Node<E> firstNode;
    private Node<E> lastNode;
    private long modCount;

    public TreeList(final int degree) {
        checkDegree(degree);
        this.degree = degree;
        Node<E> n = new Node<E>(degree);
        firstNode = n;
        lastNode = n;
        root = n;
    }

    public TreeList() {
        this(DEFAULT_DEGREE);
    }

    @Override
    public boolean add(E e) {
        if (lastNode.size() == degree) {
            Node<E> newNode = new Node<E>(degree);
            newNode.add(e);
            newNode.parent = lastNode;
            lastNode.right = newNode;
            lastNode = newNode;
            fixAfterInsertion(newNode.parent);
        } else {
            lastNode.add(e);
        }

        ++size;
        ++modCount;
        return true;
    }

    @Override
    public void addFirst(E e) {
        if (firstNode.size() == degree) {
            Node<E> newNode = new Node<E>(degree);
            newNode.add(e);
            newNode.parent = firstNode;
            firstNode.left = newNode;
            firstNode = newNode;
            fixAfterInsertion(newNode.parent);
        } else {
            firstNode.add(0, e);
        }

        updateLeftCounters(firstNode, 1);
        ++modCount;
        ++size;
    }

    @Override
    public void addLast(E e) {
        if (lastNode.size() == degree) {
            Node<E> newNode = new Node<E>(degree);
            newNode.add(e);
            newNode.parent = lastNode;
            lastNode.right = newNode;
            lastNode = newNode;
            fixAfterInsertion(newNode.parent);
        } else {
            lastNode.add(e);
        }

        ++modCount;
        ++size;
    }

    @Override
    public void add(int index, E element) {
        Node<E> n = root;

        for (;;) {
            if (index < n.leftCount) {
                n = n.left;
            } else if (index >= n.leftCount + n.size()) {
                index -= n.leftCount + n.size();
                n = n.right;
            } else {
                index -= n.leftCount;
                break;
            }
        }

        if (n.size() == degree) {
            // Split node 'n'.
            Node<E> newNode = n.split();

            if (index < n.size()) {
                n.add(index, element);
            } else {
                newNode.add(index - n.size(), element);
            }

            if (n.right == null) {
                n.right = newNode;
                newNode.parent = n;

                if (lastNode == n) {
                    lastNode = newNode;
                }

                updateLeftCounters(n, 1);
                fixAfterInsertion(n);
            } else {
                Node<E> successor = n.right.min();
                successor.left = newNode;
                newNode.parent = successor;
                updateLeftCounters(newNode, newNode.size());
                fixAfterInsertion(successor);
            }
        } else {
            n.add(index, element);
            updateLeftCounters(n, 1);
        }

        ++modCount;
        ++size;
    }

    @Override
    public E get(int index) {
        Node<E> n = root;

        for (;;) {
            if (index < n.leftCount) {
                n = n.left;
            } else if (index >= n.leftCount + n.size()) {
                index -= n.leftCount + n.size();
                n = n.right;
            } else {
                return (E) n.array[index - n.leftCount];
            }
        }
    }

    @Override
    public E set(int index, E element) {
        Node<E> n = root;

        for(;;) {
            if (index < n.leftCount) {
                n = n.left;
            } else if (index >= n.leftCount + n.size()) {
                index -= n.leftCount + n.size();
                n = n.right;
            } else {
                break;
            }
        }

        final int indx = index + n.first - n.leftCount;
        E ret = (E) n.array[indx];
        n.array[indx] = element;
        return ret;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
        root.left = null;
        root.right = null;
        root.leftCount = 0;
        root.clear();
        firstNode = root;
        lastNode = root;
        ++modCount;
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException("Removing from empty TreeList.");
        }

        if (firstNode.size() == 0) {
            System.out.println("Fail!");
            System.out.println("first: " + firstNode.first);
            System.out.println("last:  " + firstNode.last);
        }

        E element = firstNode.remove(0);
        updateLeftCounters(firstNode, -1);

        if (firstNode.size() == 0) {
            Node<E> removedNode = removeImpl(firstNode);
            fixAfterDeletion(removedNode);
            firstNode = removedNode.parent;
        }

        --size;
        ++modCount;
        return element;
    }

    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("Removing from empty TreeList.");
        }

        E element = lastNode.remove(lastNode.size() - 1);

        if (lastNode.size() == 0) {
            Node<E> removedNode = removeImpl(lastNode);
            fixAfterDeletion(removedNode);
            lastNode = removedNode.parent;
        }

        --size;
        ++modCount;
        return element;
    }

    @Override
    public E remove(int index) {
        Node<E> n = root;

        for (;;) {
            if (index < n.leftCount) {
                n = n.left;
            } else if (index >= n.leftCount + n.size()) {
                index -= n.leftCount + n.size();
                n = n.right;
            } else {
                break;
            }
        }

        E removedElement = n.remove(index - n.leftCount);

        if (n.size() == 0) {
            Node<E> removedNode = removeImpl(n);
            fixAfterDeletion(removedNode);
            updateLeftCounters(removedNode, -1);
        } else {
            updateLeftCounters(n, -1);
        }

        --size;
        ++modCount;
        return removedElement;
    }

    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }

        return (E) firstNode.array[firstNode.first];
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }

        return (E) lastNode.array[lastNode.last];
    }

    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "Reading the head of an empty list."
                    );
        }

        return (E) firstNode.array[firstNode.first];
    }

    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "Reading the tail of an empty list."
                    );
        }

        return (E) lastNode.array[lastNode.last];
    }

    @Override
    public E peekFirst() {
        if (size == 0) {
            return null;
        }

        return (E) firstNode.array[firstNode.first];
    }

    @Override
    public E peekLast() {
        if (size == 0) {
            return null;
        }

        return (E) lastNode.array[lastNode.last];
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean offer(E e) {
        add(e);
        return true;
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return size == 0 ? null : (E) firstNode.array[firstNode.first];
    }

    @Override
    public E element() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "Reading the head of an empty list."
                    );
        }

        return (E) firstNode.array[firstNode.first];
    }

    @Override
    public E peek() {
        return size == 0 ? null : (E) firstNode.array[firstNode.first];
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        if (size == 0) {
            throw new NoSuchElementException("Popping from an empty list.");
        }

        return removeFirst();
    }

    @Override
    public Iterator<E> iterator() {
        return new AscendingListIterator(this.firstNode, 0, 0);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new AscendingListIterator(this.lastNode,
                                         this.lastNode.size(),
                                         this.size) {
            @Override
            public boolean hasNext() {
                return super.hasPrevious();
            }

            @Override
            public E next() {
                return super.previous();
            }

            @Override
            public void remove() {
                super.remove();
            }
        };
    }

    /**
     * Checks all of the AVL-tree invariants.
     *
     * @return <tt>true</tt> if this is a valid AVL-tree, <tt>false</tt>
     * otherwise.
     */
    public boolean isHealthy() {
        return !hasCycles()
                && heightFieldsOK()
                && isBalanced()
                && isWellIndexed();
    }

    private boolean hasCycles() {
        return hasCycles(root, new HashSet<Node<E>>());
    }

    private boolean heightFieldsOK() {
        if (root == null) {
            return true;
        }

        int h = checkHeight(root);

        if (h != root.height) {
            if (DEBUG_MSG) {
                System.err.println("DEBUG: root's actual height is " + h
                        + ", recorded: " + root.height);
            }

            return false;
        }

        return checkHeight(root) == root.height;
    }

    private boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isWellIndexed() {
        if (size == 0) {
            return true;
        }

        int left = countLeft(root.left);
        boolean leftOk = root.leftCount == left;

        if (leftOk == false) {
            if (DEBUG_MSG) {
                System.err.println("Root's actual left count is "
                        + left + ", recorded " + root.leftCount);
            }
        }

        boolean rightOk = true;

        if (root.right != null) {
            int right = countLeft(root.right.left);

            if (right != root.right.leftCount) {
                rightOk = false;

                if (DEBUG_MSG) {
                    System.err.println("Root's right node's actual left count "
                            + "is " + right + ", recorded "
                            + root.right.leftCount);
                }
            }
        }

        return leftOk && rightOk;
    }

    private int checkHeight(Node<E> e) {
        if (e == null) {
            return -1;
        }

        int l = checkHeight(e.left);

        if (l == Integer.MIN_VALUE) {
            return l;
        }

        int r = checkHeight(e.right);

        if (r == Integer.MIN_VALUE) {
            return r;
        }

        int h = Math.max(l, r) + 1;

        if (h != e.height) {
            return Integer.MIN_VALUE;
        } else {
            return h;
        }
    }

    private boolean isBalanced(Node<E> e) {
        if (e == null) {
            return true;
        }

        if (Math.abs(h(e.left) - h(e.right)) > 1) {
            System.out.println("Disbalanced tree.");
            return false;
        }

        if (isBalanced(e.left) == false) {
            System.out.println("Disbalanced tree.");
            return false;
        }

        if (isBalanced(e.right) == false) {
            System.out.println("Disbalanced tree.");
            return false;
        }

        return true;
    }

    private int countLeft(Node<E> e) {
        if (e == null) {
            return 0;
        }

        int l;
        int r;

        if ((l = countLeft(e.left)) != e.leftCount) {
            System.out.print("Broken left counter:: ");
            System.out.println("Counted: " + l + ", recorded: " + e.leftCount);
            return Integer.MIN_VALUE;
        }

        if ((r = countLeft(e.right)) == Integer.MIN_VALUE) {
            System.out.println("Broken left counter II.");
            return Integer.MIN_VALUE;
        }

        return l + r + e.size();
    }

    private boolean hasCycles(Node<E> e, HashSet<Node<E>> set) {
        if (e == null) {
            return false;
        }

        if (set.contains(e)) {
            if (DEBUG_MSG) {
                System.err.println("DEBUG: This TreeList contains cycles!");
            }

            return true;
        }

        set.add(e);

        if (hasCycles(e.left, set)) {
            return true;
        }

        if (hasCycles(e.right, set)) {
            return true;
        }

        return false;
    }

    private void checkDegree(final int degree) {
        if (degree < 2) {
            throw new IllegalArgumentException("Invalid degree: " + degree);
        }
    }

    private int h(Node<E> node) {
        if (node == null) {
            return -1;
        }

        return Math.max(h(node.left), h(node.right)) + 1;
    }

    /**
     * The left rotation of a tree node.
     *
     * @param e the unbalanced node.
     *
     * @return the new root of a balanced subtree.
     */
    private Node<E> leftRotate(Node<E> e) {
        Node<E> n = e.right;
        n.parent = e.parent;
        e.parent = n;
        e.right = n.left;
        n.left = e;

        if (e.right != null) {
            e.right.parent = e;
        }

        e.height = Math.max(h(e.left), h(e.right)) + 1;
        n.height = Math.max(h(n.left), h(n.right)) + 1;

        n.leftCount += e.leftCount + e.size();
        return n;
    }

    /**
     * The right rotation of a tree node.
     *
     * @param e the unbalanced node.
     *
     * @return the new root of a balanced subtree.
     */
    private Node<E> rightRotate(Node<E> e) {
        Node<E> n = e.left;
        n.parent = e.parent;
        e.parent = n;
        e.left = n.right;
        n.right = e;

        if (e.left != null) {
            e.left.parent = e;
        }

        e.height = Math.max(h(e.left), h(e.right)) + 1;
        n.height = Math.max(h(n.left), h(n.right)) + 1;

        e.leftCount -= n.leftCount + n.size();
        return n;
    }

    /**
     * The left/right rotation of a tree node.
     *
     * @param e the unbalanced node.
     *
     * @return the new root of a balanced subtree.
     */
    private Node<E> leftRightRotate(Node<E> e) {
        Node<E> ee = e.left;
        e.left = leftRotate(ee);
        return rightRotate(e);
    }

    /**
     * The right/left rotation of a tree node.
     *
     * @param e the unbalanced node.
     *
     * @return the new root of a balanced subtree.
     */
    private Node<E> rightLeftRotate(Node<E> e) {
        Node<E> ee = e.right;
        e.right = rightRotate(ee);
        return leftRotate(e);
    }

    /**
     * Fixes the tree invariant after inserting a node.
     *
     * @param e the lowest node that may be unbalanced.
     */
    private void fixAfterInsertion(Node<E> e) {
        while (e != null) {
            if (h(e.left) == h(e.right) + 2) {
                Node<E> p = e.parent;
                Node<E> subroot =
                        (h(e.left.left) >= h(e.left.right)) ?
                            rightRotate(e) :
                            leftRightRotate(e);

                if (p == null) {
                    root = subroot;
                } else if (p.left == e) {
                    p.left = subroot;
                } else {
                    p.right = subroot;
                }

                if (p != null) {
                    p.height = Math.max(h(p.left), h(p.right)) + 1;
                }

                return;
            } else if (h(e.left) + 2 == h(e.right)) {
                Node<E> p = e.parent;
                Node<E> subroot =
                        (h(e.right.right) >= h(e.right.left)) ?
                            leftRotate(e) :
                            rightLeftRotate(e);

                if (p == null) {
                    root = subroot;
                } else if (p.left == e) {
                    p.left = subroot;
                } else {
                    p.right = subroot;
                }

                if (p != null) {
                    p.height = Math.max(h(p.left), h(p.right)) + 1;
                }

                return;
            }

            e.height = Math.max(h(e.left), h(e.right)) + 1;
            e = e.parent;
        }
    }

    /**
     * Fixes the tree after deleting the node.
     *
     * @param e the lowest node that may be unbalanced.
     */
    private void fixAfterDeletion(Node<E> e) {
        while (e != null) {
            if (h(e.left) == h(e.right) + 2) {
                Node<E> p = e.parent;
                Node<E> subroot =
                        (h(e.left.left) >= h(e.left.right)) ?
                            rightRotate(e) :
                            leftRightRotate(e);

                if (p == null) {
                    root = subroot;
                } else if (p.left == e) {
                    p.left = subroot;
                } else {
                    p.right = subroot;
                }

                if (p != null) {
                    p.height = Math.max(h(p.left), h(p.right)) + 1;
                }
            } else if (h(e.left) + 2 == h(e.right)) {
                Node<E> p = e.parent;
                Node<E> subroot =
                        (h(e.right.right) >= h(e.right.left)) ?
                            leftRotate(e) :
                            rightLeftRotate(e);

                if (p == null) {
                    root = subroot;
                } else if (p.left == e) {
                    p.left = subroot;
                } else {
                    p.right = subroot;
                }

                if (p != null) {
                    p.height = Math.max(h(p.left), h(p.right)) + 1;
                }
            }

            e.height = Math.max(h(e.left), h(e.right)) + 1;
            e = e.parent;
        }
    }

    /**
     * Removes a node from the tree without balancing it.
     *
     * @param e the node to remove.
     *
     * @return the actual node removed.
     */
    private Node<E> removeImpl(Node<E> e) {
        if (e.left == null && e.right == null) {
            // No children.
            Node<E> p = e.parent;

            if (p == null) {
                // e is root.
                return e;
            }

            if (e == p.left) {
                p.left = null;
                p.leftCount = 0;
            } else {
                p.right = null;
            }

            return e;
        }

        if (e.left == null || e.right == null) {
            // One child.
            Node<E> child = e.left != null ? e.left : e.right;
            Node<E> p = e.parent;
            child.parent = p;

            if (p == null) {
                root = child;
                return e;
            }

            if (e == p.left) {
                p.left = child;
            } else {
                p.right = child;
            }

            return e;
        }

        // Two children.
        Node<E> successor = e.right.min();

        e.array = successor.array;
        e.first = successor.first;
        e.last = successor.last;

        Node<E> child = successor.right;
        Node<E> p = successor.parent;

        if (p.left == successor) {
            p.left = child;
        } else {
            p.right = child;
        }

        if (child != null) {
            child.parent = p;
        }

        return successor;
    }

    private void updateLeftCounters(Node<E> from, int delta) {
        while (from != null) {
            if (from.parent != null && from.parent.left == from) {
                from.parent.leftCount += delta;
            }

            from = from.parent;
        }
    }

    private class AscendingListIterator implements ListIterator<E> {
        private long expectedModCount = TreeList.this.modCount;
        private Node<E> currentNode = TreeList.this.firstNode;
        private int currentIndex;
        private int totalIndex;

        AscendingListIterator(Node<E> initialNode,
                              int initialCurrentIndex,
                              int initialTotalIndex) {
            this.currentNode = initialNode;
            this.currentIndex = initialCurrentIndex;
            this.totalIndex = initialTotalIndex;
        }

        @Override
        public boolean hasNext() {
            checkModCount();
            return totalIndex < TreeList.this.size;
        }

        @Override
        public E next() {
            checkModCount();

            if (currentIndex == currentNode.size()) {
                currentIndex = 0;
                currentNode = currentNode.successor();
            }

            ++totalIndex;
            return (E) currentNode.array[currentIndex++];
        }

        @Override
        public boolean hasPrevious() {
            checkModCount();
            return totalIndex > 0;
        }

        @Override
        public E previous() {
            checkModCount();

            if (currentIndex == 0) {
                currentNode = currentNode.predecessor();
                currentIndex = currentNode.size();
            }

            --totalIndex;
            return (E) currentNode.array[--currentIndex];
        }

        @Override
        public int nextIndex() {
            checkModCount();
            return totalIndex;
        }

        @Override
        public int previousIndex() {
            checkModCount();
            return totalIndex - 1;
        }

        @Override
        public void remove() {
            checkModCount();
            ++expectedModCount;
            TreeList.this.remove(totalIndex);
        }

        @Override
        public void set(E e) {
            checkModCount();
            currentNode.array[currentIndex] = e;
        }

        @Override
        public void add(E e) {
            checkModCount();
            ++expectedModCount;
            TreeList.this.add(totalIndex, e);
        }

        private void checkModCount() {
            if (this.expectedModCount != TreeList.this.modCount) {
                throw new ConcurrentModificationException(
                        "This TreeList is modified while iterating."
                        );
            }
        }
    }
}
