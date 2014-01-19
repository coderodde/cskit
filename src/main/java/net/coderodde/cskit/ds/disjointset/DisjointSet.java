package net.coderodde.cskit.ds.disjointset;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the disjoint-set structure.
 *
 * @author Rodion Efremov
 * @version 1.618033 (28.12.2013)
 */
public class DisjointSet<E> {

    public static class Node<E> {
        E datum;
        Node<E> parent;
        int rank;

        Node(E rootElement) {
            this.datum = rootElement;
            this.parent = this;
        }
    }

    private Map<E, Node<E>> map = new HashMap<E, Node<E>>();

    public E find(E e) {
        Node<E> node = find(getNode(e));

        if (node == node.parent) {
            return node.datum;
        }

        node.parent = find(node.parent);
        return node.parent.datum;
    }

    public void union(E e1, E e2) {
        Node<E> n1 = find(getNode(e1));
        Node<E> n2 = find(getNode(e2));

        if (n1 == n2) {
            return;
        }

        if (n1.rank < n2.rank) {
            n1.parent = n2;
        } else if (n1.rank > n2.rank) {
            n2.parent = n1;
        } else {
            n2.parent = n1;
            n1.rank++;
        }
    }

    private Node<E> find(Node<E> node) {
        if (node == node.parent) {
            return node;
        }

        return find(node.parent);
    }

    private Node<E> getNode(E element) {
        Node<E> node = map.get(element);

        if (node == null) {
            node = new Node<E>(element);
            map.put(element, node);
        }

        return node;
    }
}
