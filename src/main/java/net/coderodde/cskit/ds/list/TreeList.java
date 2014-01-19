package net.coderodde.cskit.ds.list;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
implements Deque<E>, Serializable, Cloneable{

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

        void add(E element) {
            if (last == array.length - 1) {
                final int left = Math.max((array.length - size()) >> 1, 1);
                for (int i = first; i <= last; ++i) {
                    array[i - 1] = array[i];
                }
                array[last] = element;
                --first;
            } else {
                array[++last] = element;
            }
        }

        void set(int index, E element) {
            final int elementsBefore = first;
            final int elementsAfter = last - index + 1;

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
            array[index] = element;
        }

        E remove(int index) {
            E old = (E) array[index + first];

            final int elementsBefore = index - first;
            final int elementsAfter = last - index;

            if (elementsBefore < elementsAfter) {
                // Shift right.
                for (int i = index - 1; i >= first; --i) {
                    array[i + 1] = array[i];
                }

                array[first++] = null;
            } else {
                // elementsBefore >= elementsAfter
                // Shift left.
                for (int i = index; i < last; ++i) {
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

        E last() {
            return (E) array[last];
        }

        Node<E> min() {
            Node<E> e = this;

            while (e.left != null) {
                e = e.left;
            }

            return e;
        }
    }

    private final int degree;
    private int size;
    private Node<E> root;
    private Node<E> firstNode;
    private Node<E> lastNode;

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
            firstNode.set(0, e);
        }

        updateLeftCounters(firstNode, 1);
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

        E element = firstNode.remove(0);

        if (firstNode.size() == 0) {
            Node<E> removedNode = removeImpl(firstNode);
            fixAfterDeletion(removedNode);
            firstNode = removedNode.parent;
            updateLeftCounters(firstNode, -1);
        }

        --size;
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

        return removedElement;
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E getFirst() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E getLast() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E peekFirst() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E peekLast() {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E poll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E element() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void push(E e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E pop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<E> descendingIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
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

        return checkHeight(root) == root.height;
    }

    private boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isWellIndexed() {
        if (size == 0) {
            return true;
        }

        boolean leftOk = root.leftCount == countLeft(root.left);
        boolean rightOk = true;

        if (root.right != null) {
            rightOk = (root.right.leftCount == countLeft(root.right.left));
        }
//        boolean rightOk = (root.right != null)
//                         ? root.right.leftCount == countLeft(root.right.left) :
//                         true;

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
//            System.out.println("Counted: " + r + ", recorded: " + e.leftCount);
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
            System.out.println(":( Found a cycle!");
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
        if (degree < 1) {
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
        --size;

        if (e.left == null && e.right == null) {
            // No children.
            Node<E> p = e.parent;

            if (p == null) {
                // e is root.
                root = null;
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
            Node<E> p = child.parent;
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
        Node<E> child = successor.right;
        Node<E> p = successor.parent;
        e.array = successor.array;
        e.first = successor.first;
        e.last = successor.last;

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
}
