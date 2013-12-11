package net.coderodde.cskit.sorting;

import java.util.Comparator;
import java.util.Iterator;

/**
 * This class implements a tree traversal sort using unbalanced binary trees.
 * Stability guaranteed.
 *
 * @author Rodion Efremov
 * @version 1.618
 */
public class TreeSort<E extends Comparable<? super E>>
implements ObjectSortingAlgorithm<E> {

    @Override
    public void sort(E[] array) {
        sort(array, new Range(0, array.length - 1));
    }

    @Override
    public void sort(E[] array, Range r) {
        if (r.from <= r.to) {
            ascendingSort(array, r.from, r.to);
        } else {
            descendingSort(array, r.to, r.from);
        }
    }

    static class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;
        Node<E> parent;

        Node(E value) {
            this.value = value;
        }
    }

    static class AscendingTree<E extends Comparable<? super E>>
    implements Iterable<E> {

        private Node<E> root;
        private int size;

        AscendingTree(E first) {
            this.root = new Node<E>(first);
            this.size = 1;
        }

        @Override
        public Iterator<E> iterator() {
            return new TreeIterator();
        }

        void insert(E value) {
            Node<E> newNode = new Node<E>(value);
            Node<E> current = root;
            int c;

            for (;;) {
                if (value.compareTo(current.value) < 0) {
                    if (current.left != null) {
                        current = current.left;
                    } else {
                        current.left = newNode;
                        break;
                    }
                } else {
                    if (current.right != null) {
                        current = current.right;
                    } else {
                        current.right = newNode;
                        break;
                    }
                }
            }

            newNode.parent = current;
            size++;
        }

        Node<E> successor(Node<E> node) {
            if (node.right == null) {
                while (node.parent != null && node.parent.right == node) {
                    node = node.parent;
                }

                return node.parent == null ? null : node.parent;
            }

            node = node.right;

            while (node.left != null) {
                node = node.left;
            }

            return node;
        }

        private class TreeIterator implements Iterator<E> {
            private int retrieved;
            private Node<E> current;

            TreeIterator() {
                current = root;
                if (current != null) {
                    while (current.left != null) {
                        current = current.left;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return retrieved < size;
            }

            @Override
            public E next() {
                Node<E> next = successor(current);
                E value = current.value;
                current = next;
                ++retrieved;
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                        "remove() makes no sense here."
                        );
            }
        }
    }

    static class DescendingTree<E extends Comparable<? super E>>
    implements Iterable<E> {

        static class Node<E> {
            E value;
            Node<E> left;
            Node<E> right;
            Node<E> parent;

            Node(E value) {
                this.value = value;
            }
        }

        private Node<E> root;
        private int size;

        DescendingTree(E first) {
            this.root = new Node<E>(first);
            this.size = 1;
        }

        @Override
        public Iterator<E> iterator() {
            return new TreeIterator();
        }

        void insert(E value) {
            Node<E> newNode = new Node<E>(value);
            Node<E> current = root;
            int c;

            for (;;) {
                if (value.compareTo(current.value) > 0) {
                    if (current.left != null) {
                        current = current.left;
                    } else {
                        current.left = newNode;
                        break;
                    }
                } else {
                    if (current.right != null) {
                        current = current.right;
                    } else {
                        current.right = newNode;
                        break;
                    }
                }
            }

            newNode.parent = current;
            size++;
        }

        Node<E> successor(Node<E> node) {
            if (node.right == null) {
                while (node.parent != null && node.parent.right == node) {
                    node = node.parent;
                }

                return node.parent == null ? null : node.parent;
            }

            node = node.right;

            while (node.left != null) {
                node = node.left;
            }

            return node;
        }

        private class TreeIterator implements Iterator<E> {
            private int retrieved;
            private Node<E> current;

            TreeIterator() {
                current = root;
                if (current != null) {
                    while (current.left != null) {
                        current = current.left;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return retrieved < size;
            }

            @Override
            public E next() {
                Node<E> next = successor(current);
                E value = current.value;
                current = next;
                ++retrieved;
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                        "remove() makes no sense here."
                        );
            }
        }
    }

    private void ascendingSort(E[] array, int from, int to) {
        if (from == to) {
            return;
        }

        AscendingTree<E> tree = new AscendingTree<E>(array[from]);
        int i = from + 1;

        for (; i <= to; ++i) {
            tree.insert(array[i]);
        }

        i = from;

        for (E element : tree) {
            array[i++] = element;
        }
    }

    private void descendingSort(E[] array, int from, int to) {
        DescendingTree<E> tree = new DescendingTree<E>(array[from]);
        int i = from + 1;

        for (; i <= to; ++i) {
            tree.insert(array[i]);
        }

        i = from;

        for (E element : tree) {
            array[i++] = element;
        }
    }
}
