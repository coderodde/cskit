package net.coderodde.cskit.graph;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import static net.coderodde.cskit.Utilities.checkModCount;
import static net.coderodde.cskit.Utilities.checkNotNull;
import net.coderodde.cskit.AllIterable;
import net.coderodde.cskit.ParentIterable;

/**
 * This class models nodes of a directed graph.
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public class DirectedGraphNode
implements
Iterable<DirectedGraphNode>,
ParentIterable<DirectedGraphNode>,
AllIterable<DirectedGraphNode> {

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

    public String getName() {
        return name;
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
        if (this.out.contains(child) == false) {
            modCount++;
            this.out.add(child);
            child.in.add(this);
        }
    }

    public boolean hasChild(DirectedGraphNode candidate) {
        return this.out.contains(candidate);
    }

    public void removeChild(DirectedGraphNode child) {
        modCount++;
        this.out.remove(child);
        child.in.remove(this);
    }

    @Override
    public Iterator<DirectedGraphNode> iterator() {
        return new ChildIterator();
    }

    @Override
    public Iterable<DirectedGraphNode> parentIterable() {
        return new ParentIterable();
    }

    @Override
    public Iterable<DirectedGraphNode> allIterable() {
        return new AllIterable();
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
            iterator.remove();
            lastReturned = null;
        }
    }

    /**
     * This class implements an iterator over this node's parents.
     */
    private class ParentIterator implements Iterator<DirectedGraphNode> {

        private long expectedModCount = DirectedGraphNode.this.modCount;
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
            iterator.remove();
            lastReturned = null;
        }
    }

    private class AllIterator implements Iterator<DirectedGraphNode> {

        private ChildIterator childIterator = new ChildIterator();
        private ParentIterator parentIterator = new ParentIterator();

        @Override
        public boolean hasNext() {
            return childIterator.hasNext() || parentIterator.hasNext();
        }

        @Override
        public DirectedGraphNode next() {
            return childIterator.hasNext() ?
                   childIterator.next() :
                   parentIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "Compound iterator does not support remove()."
                    );
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

    /**
     * This class solely wraps an iterator over this node's parents and
     * children.
     */
    private class AllIterable implements Iterable<DirectedGraphNode> {

        @Override
        public Iterator<DirectedGraphNode> iterator() {
            return new AllIterator();
        }

    }
}
