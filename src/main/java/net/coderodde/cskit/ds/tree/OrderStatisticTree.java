package net.coderodde.cskit.ds.tree;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class implements an order-statistic tree. Essentially, this is counted
 * AVL-tree.
 *
 * @author rodionefremov
 * @version 1.6180 (19.12.2013)
 */
public class OrderStatisticTree<K extends Comparable<? super K>, V>
        implements Iterable<K>, Map<K, V> {

    /**
     * An entry (a node) in this tree.
     *
     * @param <K> the type of keys.
     * @param <V> the type of values.
     */
    public static class Node<K, V> implements Map.Entry<K, V> {

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
        private Node<K, V> parent;

        /**
         * The left entry (subtree).
         */
        private Node<K, V> left;

        /**
         * The right entry.
         */
        private Node<K, V> right;

        /**
         * Constructs a new tree node with the specified data.
         *
         * @param key the key of a newly created node.
         * @param value the value of a newly created node.
         */
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns the key of this entry.
         *
         * @return the key of this entry.
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Returns the value of this entry.
         *
         * @return the value of this entry.
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Set the value and returns the old one.
         *
         * @param value the value to set.
         *
         * @return the old value.
         */
        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        /**
         * Tests whether this node equals <code>o</code>.
         *
         * @param o the object to test against.
         *
         * @return <code>true</code> if nodes equal each other,
         * <code>false</code> otherwise.
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) {
                return false;
            }

            Node<K, V> e1 = this;
            Node<K, V> e2 = (Node<K, V>) o;

            return (e1.key.equals(e2.key) && e1.value.equals(e2.value));
        }

        /**
         * Retrieves the hash code of this node.
         *
         * @return the hash code of this node.
         */
        @Override
        public int hashCode() {
            return key.hashCode() ^ (value == null ? 0 : value.hashCode());
        }

        /**
         * Returns the minimum entry of this (sub-)tree.
         *
         * @return the minimum entry of this entry.
         */
        private Node<K, V> min() {
            Node<K, V> e = this;

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
        private Node<K, V> next() {
            Node<K, V> e = this;

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

    /**
     * The root node of this tree.
     */
    private Node<K, V> root;

    /**
     * The amount of key/value - mappings (nodes) in this tree.
     */
    private int size;

    /**
     * The modification count.
     */
    private long modCount;

    /**
     * Constructs an empty tree.
     */
    public OrderStatisticTree() {}

    /**
     * Constructs a tree populated with the contents of <code>m</code>.
     *
     * @param m the map to populate this tree with.
     */
    public OrderStatisticTree(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    /**
     * Returns <code>true</code> if this tree is empty; <code>false</code>
     * otherwise.
     *
     * @return <code>true</code> if this tree is empty; <code>false</code>
     * otherwise.
     */
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

    /**
     * Clears this tree.
     */
    public void clear() {
        modCount++;
        root = null;
        size = 0;
    }

    /**
     * Returns <code>true</code> if this tree contains the specified key,
     * <code>false</code> otherwise.
     *
     * @param key the key to query.
     *
     * @return <code>true</code> if this tree contains the key,
     * <code>false</code>.
     */
    public boolean containsKey(Object o) {
        K key = (K) o;
        Node<K, V> e = root;
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
     * Returns <code>true</code> if this tree contains the specified value,
     * <code>false</code> otherwise.
     *
     * @param value the value to query.
     *
     * @return <code>true</code> if this tree contains the specified value,
     * <code>false</code> otherwise.
     */
    @Override
    public boolean containsValue(Object value) {
        if (root == null) {
            return false;
        }

        Node<K, V> e = root;
        e = e.min();

        while (e != null) {
            if (e.value.equals(value)) {
                return true;
            }

            e = e.next();
        }

        return false;
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

        Node<K, V> e = new Node<K, V>(key, value);

        if (root == null) {
            root = e;
            size = 1;
            return null;
        }

        Node<K, V> x = root;
        Node<K, V> p = null;
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
        Node<K, V> tmp = p.parent;
        Node<K, V> tmpLo = p;

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
                Node<K, V> pp = p.parent;
                Node<K, V> subroot =
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
                Node<K, V> pp = p.parent;
                Node<K, V> subroot =
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
     * Puts every key/value - mapping from <code>m</code> to this tree.
     *
     * @param m the map to add.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
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
    @Override
    public V remove(Object o) {
        if (size == 0) {
            return null;
        }

        K key = (K) o;
        modCount++;
        Node<K, V> e = root;
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

    /**
     * Gets the value associated with <tt>key</tt>. Runs in logarithmic time.
     *
     * @param key the key.
     *
     * @return the value associated with <tt>key</tt>.
     */
    @Override
    public V get(Object o) {
        K key = (K) o;
        Node<K, V> e = root;
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
     *
     * @return an entry if i is within range, or <tt>null</tt> otherwise.
     */
    public Node<K, V> entryAt(int i) {
        if (i < 0 || i >= size) {
            return null;
        }

        int save = i;
        Node<K, V> e = root;

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
     *
     * @return the rank of key.
     */
    public int rankOf(K key) {
        int cmp;
        int counter = 0;
        Node<K, V> e = root;

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
     * Retrieves the hash code of this <code>OrderedStatisticTree</code>.
     *
     * @return the hash code of this tree.
     */
    @Override
    public int hashCode() {
        if (root == null) {
            return 0;
        }

        Node<K, V> e = root.min();
        int sum = 0;

        while (e != null) {
            sum += e.hashCode();
            e = e.next();
        }

        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderStatisticTree)) {
            return false;
        }

        return true;
    }

    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException(
                "Value view not yet implemented");
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    private class KeySet implements Set<K> {

        @Override
        public int size() {
            return OrderStatisticTree.this.size;
        }

        @Override
        public boolean isEmpty() {
            return OrderStatisticTree.this.size == 0;
        }

        @Override
        public boolean contains(Object o) {
            return OrderStatisticTree.this.containsKey(o);
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[OrderStatisticTree.this.size];

            if (root == null) {
                return array;
            }

            Node<K, V> e = root.min();

            for (int i = 0; i < array.length; ++i, e = e.next()) {
                array[i] = e.key;
            }

            return array;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            final int len = Math.min(OrderStatisticTree.this.size, a.length);

            if (root == null) {
                if (a.length > 0) {
                    a[0] = null;
                }

                return a;
            }

            Node<K, V> e = root.min();

            for (int i = 0; i < len; ++i, e = e.next()) {
                a[i] = (T) e.key;
            }

            return a;
        }

        @Override
        public boolean add(K e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean remove(Object o) {
            boolean removed = OrderStatisticTree.this.containsKey(o);
            OrderStatisticTree.this.remove(o);
            return removed;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                if (OrderStatisticTree.this.containsKey(o) == false) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Iterator<K> iterator = new KeyIterator();
            boolean modified = false;

            while (iterator.hasNext()) {
                K key = iterator.next();

                if (c.contains(key) == false) {
                    iterator.remove();
                }
            }

            return modified;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean modified = false;

            for (Object o : c) {
                if (OrderStatisticTree.this.containsKey(o)) {
                    modified = true;
                }

                OrderStatisticTree.this.remove(o);
            }

            return modified;
        }

        @Override
        public void clear() {
            OrderStatisticTree.this.clear();
        }
    }

    /**
     * This class implements the set over this tree's entries.
     */
    private class EntrySet implements Set<Map.Entry<K, V>> {

        @Override
        public int size() {
            return OrderStatisticTree.this.size;
        }

        @Override
        public boolean isEmpty() {
            return OrderStatisticTree.this.size == 0;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Node)) {
                return false;
            }

            Node<K, V> e = (Node<K, V>) o;
            V value = OrderStatisticTree.this.get(e.key);
            return value.equals(e.value);
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[OrderStatisticTree.this.size];

            if (root == null) {
                return array;
            }

            Node<K, V> e = root.min();

            for (int i = 0; e != null; ++i, e = e.next()) {
                array[i] = e;
            }

            return array;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            final int max = Math.min(a.length, OrderStatisticTree.this.size);

            if (root == null) {
                return a;
            }

            Node<K, V> e = root.min();

            for (int i = 0; i < max; ++i, e = e.next()) {
                a[i] = (T) e;
            }

            if (a.length > max) {
                a[max] = null;
            }

            return a;
        }

        @Override
        public boolean add(Map.Entry<K, V> e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean remove(Object o) {
            boolean ret = OrderStatisticTree.this.containsKey(o);
            OrderStatisticTree.this.remove(o);
            return ret;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                Map.Entry<K, V> e = (Map.Entry<K, V>) o;

                if (OrderStatisticTree.this.containsKey(e.getKey()) == false) {
                    return false;
                }

                if (OrderStatisticTree.this
                        .get(e.getKey()).equals(e.getValue()) == false) {
                    return false;
                }

            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            if (root == null) {
                return false;
            }

            Iterator<K> iterator = OrderStatisticTree.this.iterator();
            boolean modified = false;

            while (iterator.hasNext()) {
                K key = iterator.next();

                if (c.contains(key) == false) {
                    iterator.remove();
                    modified = true;
                }
            }

            return modified;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean modified = false;

            for (Object o : c) {
                Map.Entry<K, V> e = (Map.Entry<K, V>) o;
                V value = OrderStatisticTree.this.get(e.getKey());

                if (value.equals(e.getValue())) {
                    OrderStatisticTree.this.remove(e.getKey());
                    modified = true;
                }
            }

            return modified;
        }

        @Override
        public void clear() {
            OrderStatisticTree.this.clear();
        }

        private class EntryIterator implements Iterator<Map.Entry<K, V>> {

            private Node<K, V> entry = (root == null ? null : root.min());
            private Node<K, V> lastReturned;
            private final long expectedModCount =
                    OrderStatisticTree.this.modCount;

            @Override
            public boolean hasNext() {
                checkModCount();
                return entry != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                checkModCount();
                lastReturned = entry;
                entry = entry.next();
                return lastReturned;
            }

            @Override
            public void remove() {
                checkModCount();

                if (lastReturned == null) {
                    throw new NoSuchElementException("No entry to remove.");
                }

                lastReturned = removeImpl(lastReturned);
                balanceAfterRemoval(lastReturned);
                lastReturned = null;
            }

            private void checkModCount() {
                if (expectedModCount != OrderStatisticTree.this.modCount) {
                    throw new ConcurrentModificationException(
                            "The tree is modified while iterating entries.");
                }
            }
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new KeyIterator();
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
        return hasCycles(root, new java.util.HashSet<Node<K, V>>());
    }

    private boolean heightFieldsOK() {
        if (root == null) {
            return true;
        }

        return checkHeight(root) == root.h;
    }

    private boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isWellIndexed() {
        if (size == 0) {
            return true;
        }

        boolean leftOk = root.count == countLeft(root.left);
        boolean rightOk = (root.right != null)
                         ? root.right.count == countLeft(root.right.left) :
                         true;

        return leftOk && rightOk;
    }

    private int checkHeight(Node<K, V> e) {
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

        if (h != e.h) {
            return Integer.MIN_VALUE;
        } else {
            return h;
        }
    }

    private boolean isBalanced(Node<K, V> e) {
        if (e == null) {
            return true;
        }

        if (Math.abs(h(e.left) - h(e.right)) > 1) {
            return false;
        }

        if (isBalanced(e.left) == false) {
            return false;
        }

        if (isBalanced(e.right) == false) {
            return false;
        }

        return true;
    }

    private int countLeft(Node<K, V> e) {
        if (e == null) {
            return 0;
        }

        int l;
        int r;

        if ((l = countLeft(e.left)) != e.count) {
            return Integer.MIN_VALUE;
        }

        if ((r = countLeft(e.right)) == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        return l + r + 1;
    }

    private boolean hasCycles(Node<K, V> e, java.util.HashSet<Node<K, V>> set) {
        if (e == null) {
            return false;
        }

        if (set.contains(e)) {
            return true;
        }

        set.add(e);

        if (hasCycles(e.left, set)) {
            return true;
        }

        if (hasCycles(e.right, set)) {
            return true;
        }

        set.remove(e);
        return false;
    }

    private void balanceAfterRemoval(Node<K, V> e) {
        Node<K, V> p = e.parent;

        while (p != null) {

            Node<K, V> subroot;
            Node<K, V> pp = p.parent;
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
        private Node<K, V> entry = OrderStatisticTree.this.root.min();
        private Node<K, V> lastReturned = null;

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
    private int h(Node<K, V> e) {
        return e != null ? e.h : -1;
    }

    /**
     * The left rotation of a tree node.
     *
     * @param e the disbalanced node.
     * @return the new root of a balanced subtree.
     */
    private Node<K, V> leftRotate(Node<K, V> e) {
        Node<K, V> ee = e.right;
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
    private Node<K, V> rightRotate(Node<K, V> e) {
        Node<K, V> ee = e.left;
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
    private Node<K, V> leftRightRotate(Node<K, V> e) {
        Node<K, V> ee = e.left;
        e.left = leftRotate(ee);
        return rightRotate(e);
    }

    /**
     * The right/left rotation of a tree node.
     *
     * @param e the disbalanced node.
     * @return the new root of a balanced subtree.
     */
    private Node<K, V> rightLeftRotate(Node<K, V> e) {
        Node<K, V> ee = e.right;
        e.right = rightRotate(ee);
        return leftRotate(e);
    }

    /**
     * Removes a node from the tree without balancing the tree.
     *
     * @param e the node to remove.
     * @return the actual node removed.
     */
    private Node<K, V> removeImpl(Node<K, V> e) {
        --size;

        if (e.left == null && e.right == null) {
            // The case where the removed node has no children.
            Node<K, V> p = e.parent;

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

            Node<K, V> pp = p.parent;

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
            Node<K, V> child = e.left != null ? e.left : e.right;
            Node<K, V> p = e.parent;
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
        Node<K, V> successor = e.right.min();
        e.key = successor.key;
        e.value = successor.value;
        Node<K, V> child = successor.right;
        Node<K, V> p = successor.parent;

        if (p.left == successor) {
            p.left = child;
        } else {
            p.right = child;
        }

        if (child != null) {
            child.parent = p;
        }

        Node<K, V> pLo = child;

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
