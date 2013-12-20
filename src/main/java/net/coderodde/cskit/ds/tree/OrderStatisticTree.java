package net.coderodde.cskit.ds.tree;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class implements an order-statistic tree. Essentially, this is counted
 * AVL-tree.
 *
 * @author rodionefremov
 * @version 1.6180 (19.12.2013)
 */
public class OrderStatisticTree<K extends Comparable<? super K>, V>
        implements Iterable<K> {

    /**
     * An entry (a node) in this tree.
     *
     * @param <K> the type of keys.
     * @param <V> the type of values.
     */
    public static class Entry<K, V> {

        /**
         * The key of this entry.
         */
        private K key;
        /**
         * The value of this entry.
         */
        private V value;
        /**
         * Amount of all key-value -mappings in the left subtree of this entry.
         */
        private int count;
        /**
         * This field is the height of this entry. Grows upwards.
         */
        private int h;
        /**
         * The parent entry of this entry.
         */
        private Entry<K, V> parent;
        /**
         * The left entry (subtree).
         */
        private Entry<K, V> left;
        /**
         * The right entry.
         */
        private Entry<K, V> right;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns the key of this entry.
         *
         * @return the key of this entry.
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the value of this entry.
         *
         * @return the value of this entry.
         */
        public V getValue() {
            return value;
        }

        public Entry<K, V> min() {
            Entry<K, V> e = this;

            while (e.left != null) {
                e = e.left;
            }

            return e;
        }

        /**
         * Returns the successor entry if one exists, and
         * <code>null</code> if there is no such.
         *
         * @return the successor entry or <code>null</code>.
         */
        public Entry<K, V> next() {
            Entry<K, V> e = this;

            if (e.right != null) {
                e = e.right;

                while (e.left != null) {
                    e = e.left;
                }

                return e;
            }

            while (e.parent != null && e.parent.right == e) {
                e = e.parent;
            }

            if (e.parent == null) {
                return null;
            }

            return e.parent;
        }
    }

    private Entry<K, V> root;
    private int size;
    private long modCount;

    /**
     * Clears this tree.
     */
    public void clear() {
        modCount++;
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retrieves the size of this tree.
     *
     * @return the size of this tree.
     */
    public int size() {
        return size;
    }

    public boolean containsKey(K key) {
        Entry<K, V> e = root;
        int cmp;

        while (e != null) {
            if ((cmp = key.compareTo(e.key)) < 0) {
                e = e.left;
            } else if (cmp > 0) {
                e = e.right;
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the value associated with <tt>key</tt>. Runs in logarithmic time.
     *
     * @param key the key.
     * @return the value associated with <tt>key</tt>.
     */
    public V get(K key) {
        Entry<K, V> e = root;
        int cmp;

        while (e != null) {
            if ((cmp = key.compareTo(e.key)) < 0) {
                e = e.left;
            } else if (cmp > 0) {
                e = e.right;
            } else {
                return e.value;
            }
        }

        return null;
    }

    /**
     * Gets the entry with ith smallest key. Runs in logarithmic time.
     *
     * @param i the index of the node, by in-order.
     * @return an entry if i is within range, or <tt>null</tt> otherwise.
     */
    public Entry<K, V> entryAt(int i) {
        if (i < 0 || i >= size) {
            return null;
        }

        int save = i;
        Entry<K, V> e = root;

        for (;;) {
            if (i < e.count) {
                e = e.left;
            } else if (i > e.count) {
                i -= e.count + 1;
                e = e.right;
            } else {
                return e;
            }
        }
    }

    /**
     * Returns the rank of a key, or <tt>-1</tt> if there is no such key in the
     * tree.
     *
     * @param key the key to rank.
     * @return the rank of key.
     */
    public int getRankOf(K key) {
        int cmp;
        int counter = 0;
        Entry<K, V> e = root;

        for (;;) {
            if ((cmp = e.key.compareTo(key)) > 0) {
                e = e.left;
            } else if (cmp < 0) {
                counter += e.count + 1;
                e = e.right;
            } else if (e == null) {
                return -1;
            } else {
                return e.count + counter;
            }
        }
    }

    /**
     * Associates <tt>key</tt> with <tt>value</tt>. If <tt>key</tt> is already
     * present in the tree, its old value is overwritten by <tt>value</tt>. Runs
     * in logarithmic time.
     *
     * @param key the key.
     * @param value the value.
     * @return the old value for <tt>key</tt>, or <tt>null</tt>, if no such.
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("'key' is null.");
        }

        modCount++;

        Entry<K, V> e = new Entry<K, V>(key, value);

        if (root == null) {
            root = e;
            size = 1;
            return null;
        }

        Entry<K, V> x = root;
        Entry<K, V> p = null;
        int cmp;

        while (x != null) {
            p = x;

            if ((cmp = e.key.compareTo(x.key)) < 0) {
                x = x.left;
            } else if (cmp > 0) {
                x = x.right;
            } else {
                V old = x.value;
                x.value = value;
                return old;
            }
        }

        e.parent = p;

        if (e.key.compareTo(p.key) < 0) {
            p.left = e;
            p.count = 1;
        } else {
            p.right = e;
        }
        Entry<K, V> tmp = p.parent;
        Entry<K, V> tmpLo = p;

        // Update the counters.
        while (tmp != null) {
            if (tmp.left == tmpLo) {
                tmp.count++;
            }

            tmpLo = tmp;
            tmp = tmp.parent;
        }

        size++;

        while (p != null) {
            if (h(p.left) == h(p.right) + 2) {
                Entry<K, V> pp = p.parent;
                Entry<K, V> subroot =
                        (h(p.left.left) >= h(p.left.right)
                        ? rightRotate(p)
                        : leftRightRotate(p));

                if (pp == null) {
                    root = subroot;
                } else if (pp.left == p) {
                    pp.left = subroot;
                } else {
                    pp.right = subroot;
                }

                if (pp != null) {
                    pp.h = Math.max(h(pp.left), h(pp.right)) + 1;
                }

                return null;
            } else if (h(p.left) + 2 == h(p.right)) {
                Entry<K, V> pp = p.parent;
                Entry<K, V> subroot =
                        (h(p.right.right) >= h(p.right.left)
                        ? leftRotate(p)
                        : rightLeftRotate(p));

                if (pp == null) {
                    root = subroot;
                } else if (pp.left == p) {
                    pp.left = subroot;
                } else {
                    pp.right = subroot;
                }


                if (pp != null) {
                    pp.h = Math.max(h(pp.left), h(pp.right)) + 1;
                }

                return null;
            }

            p.h = Math.max(h(p.left), h(p.right)) + 1;
            p = p.parent;
        }

        return null;
    }

    /**
     * Removes a mapping with key <tt>key</tt> from this tree. Runs in
     * logarithmic time.
     *
     * @param key the key of a node.
     *
     * @return the value associated with <tt>key</tt> or null, if there was no
     * entries in the tree with key <tt>key</tt>.
     */
    public V remove(K key) {
        if (size == 0) {
            throw new NoSuchElementException("Removing from an empty tree.");
        }

        modCount++;
        Entry<K, V> e = root;
        int cmp;

        while (e != null) {
            if ((cmp = e.key.compareTo(key)) > 0) {
                e = e.left;
            } else if (cmp < 0) {
                e = e.right;
            } else {
                V old = e.value;
                e = removeImpl(e);
                balanceAfterRemoval(e);
                return old;
            }
        }

        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new KeyIterator();
    }

    private void balanceAfterRemoval(Entry<K, V> e) {
        Entry<K, V> p = e.parent;

        while (p != null) {

            Entry<K, V> subroot;
            Entry<K, V> pp = p.parent;
            boolean left = (pp == null || pp.left == p);

            if (h(p.left) == h(p.right) + 2) {
                if (h(p.left.left) < h(p.left.right)) {
                    subroot = leftRightRotate(p);
                } else {
                    subroot = rightRotate(p);
                }
            } else if (h(p.left) + 2 == h(p.right)) {
                if (h(p.right.right) < h(p.right.left)) {
                    subroot = rightLeftRotate(p); //?
                } else {
                    subroot = leftRotate(p);
                }
            } else {
                p.h = Math.max(h(p.left), h(p.right)) + 1;
                p = p.parent;
                continue;
            }

            if (p == root) {
                root = subroot;
                return;
            }

            if (left) {
                pp.left = subroot;
            } else {
                pp.right = subroot;
            }

            p = pp;
        }
    }


private class KeyIterator implements Iterator<K> {

    private long expectedModCount = OrderStatisticTree.this.modCount;
    private Entry<K, V> entry = OrderStatisticTree.this.root.min();
    private Entry<K, V> lastReturned = null;
    private int iterated = 0;

    @Override
    public boolean hasNext() {
        checkModCount();
        return entry != null;
    }

    @Override
    public K next() {
        checkModCount();
        lastReturned = entry;
        entry = entry.next();
        return lastReturned.getKey();
    }

    @Override
    public void remove() {
        checkModCount();

        if (lastReturned == null) {
            throw new NoSuchElementException(
                    "Trying to remove an element twice.");
        }
        
        lastReturned = removeImpl(lastReturned);
        balanceAfterRemoval(lastReturned);
        lastReturned = null;
    }

    private void checkModCount() {
        if (expectedModCount != OrderStatisticTree.this.modCount) {
            throw new ConcurrentModificationException(
                    "Concurrent modification detected.");
        }
    }
}
/**
 * Returns the height of an argument node or -1, if <tt>e</tt> is null.
 *
 * @param e the node to measure.
 * @return the height of <tt>e</tt> or -1, if <tt>e</tt> is null.
 */
private int h(Entry<K, V> e) {
        return e != null ? e.h : -1;
    }

    /**
     * The left rotation of a tree node.
     *
     * @param e the disbalanced node.
     * @return the new root of a balanced subtree.
     */
    private Entry<K, V> leftRotate(Entry<K, V> e) {
        Entry<K, V> ee = e.right;
        ee.parent = e.parent;
        e.parent = ee;
        e.right = ee.left;
        ee.left = e;

        if (e.right != null) {
            e.right.parent = e;
        }

        e.h = Math.max(h(e.left), h(e.right)) + 1;
        ee.h = Math.max(h(ee.left), h(ee.right)) + 1;

        ee.count += e.count + 1;
        return ee;
    }

    /**
     * The right rotation of a tree node.
     *
     * @param e the disbalanced node.
     * @return the new root of a balanced subtree.
     */
    private Entry<K, V> rightRotate(Entry<K, V> e) {
        Entry<K, V> ee = e.left;
        ee.parent = e.parent;
        e.parent = ee;
        e.left = ee.right;
        ee.right = e;

        if (e.left != null) {
            e.left.parent = e;
        }

        e.h = Math.max(h(e.left), h(e.right)) + 1;
        ee.h = Math.max(h(ee.left), h(ee.right)) + 1;

        e.count -= ee.count + 1;
        return ee;
    }

    /**
     * The left/right rotation of a tree node.
     *
     * @param e the disbalanced node.
     * @return the new root of a balanced subtree.
     */
    private Entry<K, V> leftRightRotate(Entry<K, V> e) {
        Entry<K, V> ee = e.left;
        e.left = leftRotate(ee);
        return rightRotate(e);
    }

    /**
     * The right/left rotation of a tree node.
     *
     * @param e the disbalanced node.
     * @return the new root of a balanced subtree.
     */
    private Entry<K, V> rightLeftRotate(Entry<K, V> e) {
        Entry<K, V> ee = e.right;
        e.right = rightRotate(ee);
        return leftRotate(e);
    }

    /**
     * Removes a node from the tree without balancing the tree.
     *
     * @param e the node to remove.
     * @return the actual node removed.
     */
    private Entry<K, V> removeImpl(Entry<K, V> e) {
        --size;

        if (e.left == null && e.right == null) {
            // The case where the removed node has no children.
            Entry<K, V> p = e.parent;

            if (p == null) {
                root = null;
                return e;
            }

            if (e == p.left) {
                p.left = null;
                p.count = 0;
            } else {
                p.right = null;
            }

            Entry<K, V> pp = p.parent;

            while (pp != null) {
                if (pp.left == p) {
                    pp.count--;
                }

                p = pp;
                pp = pp.parent;
            }

            return e;
        }
        if (e.left == null || e.right == null) {
            // Case: only one child.
            Entry<K, V> child = e.left != null ? e.left : e.right;
            Entry<K, V> p = e.parent;
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

            while (p != null) {
                if (p.left == child) {
                    p.count--;
                }

                child = p;
                p = p.parent;
            }

            return e;
        }

        // Case: two children.
        Entry<K, V> successor = e.right.min();
        e.key = successor.key;
        e.value = successor.value;
        Entry<K, V> child = successor.right;
        Entry<K, V> p = successor.parent;

        if (p.left == successor) {
            p.left = child;
        } else {
            p.right = child;
        }

        if (child != null) {
            child.parent = p;
        }

        Entry<K, V> pLo = child;

        while (p != null) {
            if (p.left == pLo) {
                p.count--;
            }

            pLo = p;
            p = p.parent;
        }

        return successor;
    }
}
