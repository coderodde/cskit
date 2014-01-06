package net.coderodde.cskit.graph;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import static net.coderodde.cskit.Utilities.checkNotNull;

/**
 * This class defines nodes of undirected graphs.
 *
 * @author Rodion Efremov
 * @version 1.618033 (28.12.2013)
 */
public class UndirectedGraphNode
implements Iterable<UndirectedGraphNode> {

    private Set<UndirectedGraphNode> adj =
            new LinkedHashSet<UndirectedGraphNode>();

    private final String name;

    public UndirectedGraphNode(String name) {
        checkNotNull(name, "Undirected nodes must have a name.");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return ((UndirectedGraphNode) o).getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void connect(UndirectedGraphNode other) {
        if (this == other) {
            return;
        }

        adj.add(other);
        other.adj.add(this);
    }

    public boolean hasConnectionTo(UndirectedGraphNode other) {
        return this.adj.contains(other);
    }

    @Override
    public Iterator<UndirectedGraphNode> iterator() {
        return new AllIterator();
    }

    private class AllIterator implements Iterator<UndirectedGraphNode> {

        private Iterator<UndirectedGraphNode> iterator =
                UndirectedGraphNode.this.adj.iterator();

        private UndirectedGraphNode lastReturned;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public UndirectedGraphNode next() {
            return lastReturned = iterator.next();
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new NoSuchElementException(
                        "Iterator does not point anywhere."
                        );
            }

            iterator.remove();
            lastReturned.adj.remove(UndirectedGraphNode.this);
            lastReturned = null;
        }
    }
}
