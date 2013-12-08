package net.coderodde.cskit.graph;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static net.coderodde.cskit.Utilities.checkNotNull;
import static net.coderodde.cskit.Utilities.checkModCount;
import net.coderodde.cskit.ParentIterable;

/**
 * This class models nodes of a directed graph.
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public class DirectedGraphNode
implements Iterable<DirectedGraphNode>, ParentIterable<DirectedGraphNode> {

    public static final float DEFAULT_LOAD_FACTOR = 1.05f;

    /**
     * HashSets use a power of 2 as capacities.
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 1024;

    /**
     * The name of this node. It is advised to have each node have a unique name.
     */
    private final String name;

    /**
     * The set of incoming nodes.
     */
    private final Set<DirectedGraphNode> in;

    /**
     * The set of out-going nodes.
     */
    private final Set<DirectedGraphNode> out;

    private long modCount;

    /**
     * Constructs a new <code>DirectedGraphNode</code>.
     *
     * @param name the name of this node.
     * @param capacity the initial capacity of each of the adjacency lists.
     * @param loadFactor the load factor of each of the adjacency lists.
     */
    public DirectedGraphNode(String name, int capacity, float loadFactor) {
        this.name = checkNotNull(name, "A node must have a non-null name.");
        this.in = new LinkedHashSet<DirectedGraphNode>(capacity, loadFactor);
        this.out = new LinkedHashSet<DirectedGraphNode>(capacity, loadFactor);
    }

    public DirectedGraphNode(String name, int capacity) {
        this(name, capacity, DEFAULT_LOAD_FACTOR);
    }

    public DirectedGraphNode(String name) {
        this(name, DEFAULT_INITIAL_CAPACITY);
    }

    @Override
    public String toString() {
        return "[Node: " + name + "]";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.name.equals(((DirectedGraphNode) o).name);
    }

    public void addChild(DirectedGraphNode child) {
        modCount++;
        this.out.add(child);
        child.in.add(this);
    }

    public boolean hasChild(DirectedGraphNode candidate) {
        return this.out.contains(candidate);
    }

    public void removeChild(DirectedGraphNode child) {
        modCount++;
        this.out.remove(child);
        child.in.remove(this);
    }

    public Iterator<DirectedGraphNode> iterator() {
        return new ChildIterator();
    }

    public Iterable<DirectedGraphNode> parentIterable() {
        return new ParentIterable();
    }

    /**
     * This class implements an iterator over this node's parents.
     */
    private class ChildIterator implements Iterator<DirectedGraphNode> {

        private final long expectedModCount = DirectedGraphNode.this.modCount;
        private DirectedGraphNode lastReturned;
        private Iterator<DirectedGraphNode> iterator =
                DirectedGraphNode.this.out.iterator();

        @Override
        public boolean hasNext() {
            checkModCount(expectedModCount,
                          DirectedGraphNode.this.modCount,
                          "Concurrent modification encountered.");
            return iterator.hasNext();
        }

        @Override
        public DirectedGraphNode next() {
            checkModCount(expectedModCount,
                          DirectedGraphNode.this.modCount,
                          "Concurrent modification encountered.");
            return lastReturned = iterator.next();
        }

        @Override
        public void remove() {
            checkModCount(expectedModCount,
                          DirectedGraphNode.this.modCount,
                          "Concurrent modification encountered.");
            if (lastReturned == null) {
                throw new NoSuchElementException("No recently returned node.");
            }

            lastReturned.in.remove(DirectedGraphNode.this);
            DirectedGraphNode.this.out.remove(lastReturned);
            lastReturned = null;
        }
    }

    /**
     * This class implements an iterator over this node's parents.
     */
    private class ParentIterator implements Iterator<DirectedGraphNode> {

        private final long expectedModCount = DirectedGraphNode.this.modCount;
        private DirectedGraphNode lastReturned;
        private Iterator<DirectedGraphNode> iterator =
                DirectedGraphNode.this.in.iterator();

        @Override
        public boolean hasNext() {
            checkModCount(expectedModCount,
                          DirectedGraphNode.this.modCount,
                          "Concurrent modification encountered.");
            return iterator.hasNext();
        }

        @Override
        public DirectedGraphNode next() {
            checkModCount(expectedModCount,
                          DirectedGraphNode.this.modCount,
                          "Concurrent modification encountered.");
            return lastReturned = iterator.next();
        }

        @Override
        public void remove() {
            checkModCount(expectedModCount,
                          DirectedGraphNode.this.modCount,
                          "Concurrent modification encountered.");
            if (lastReturned == null) {
                throw new NoSuchElementException("No recently returned node.");
            }

            lastReturned.out.remove(DirectedGraphNode.this);
            DirectedGraphNode.this.in.remove(lastReturned);
            lastReturned = null;
        }
    }

    /**
     * This class solely wraps an iterator over this node's parents.
     */
    private class ParentIterable implements Iterable<DirectedGraphNode> {

        @Override
        public Iterator<DirectedGraphNode> iterator() {
            return new ParentIterator();
        }

    }
}
