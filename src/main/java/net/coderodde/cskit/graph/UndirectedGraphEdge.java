package net.coderodde.cskit.graph;

import java.util.Comparator;

/**
 * This class holds
 * @author Rodion Efremov
 * @version 1.618033 (28.12.2013)
 */
public class UndirectedGraphEdge {

    private UndirectedGraphNode a;
    private UndirectedGraphNode b;
    double w;

    public UndirectedGraphEdge(UndirectedGraphNode a,
                               UndirectedGraphNode b) {
        this.a = a;
        this.b = b;
    }

    public double getWeight() {
        return w;
    }

    public void setWeight(double w) {
        this.w = w;
    }

    public UndirectedGraphNode getA() {
        return a;
    }

    public UndirectedGraphNode getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        UndirectedGraphEdge edge = (UndirectedGraphEdge) o;
        return (edge.a.getName().equals(this.a.getName())
                && edge.b.getName().equals(this.b.getName()))
                || (edge.a.getName().equals(this.b.getName())
                && (edge.b.getName().equals(this.a.getName())));
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    public static final class AscendingComparator
    implements Comparator<UndirectedGraphEdge> {

        @Override
        public int compare(UndirectedGraphEdge o1, UndirectedGraphEdge o2) {
            if (o1.w < o2.w) {
                return -1;
            } else if (o1.w > o2.w) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static final class DescendingComparator
    implements Comparator<UndirectedGraphEdge> {

        @Override
        public int compare(UndirectedGraphEdge o1, UndirectedGraphEdge o2) {
            if (o1.w > o2.w) {
                return -1;
            } else if (o1.w < o2.w) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
