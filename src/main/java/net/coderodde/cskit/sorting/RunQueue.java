package net.coderodde.cskit.sorting;

/**
 * This class implements a queue specialized for natural merge sort.
 *
 * @author Rodion Efremov
 * @version 1.618 (9.12.2013)
 */
public class RunQueue {

    private static class Node {
        Run run;
        Node next;

        Node(Run run) {
            this.run = run;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    /**
     * Appends the first run to this queue, thus establishing the invariant for
     * further appendage of runs.
     *
     * @param first the very first element.
     */
    public RunQueue(Run first) {
        Node node = new Node(first);
        head = tail = node;
        size = 1;
    }

    /**
     * Appends a run to the tail of this queue.
     *
     * @param run the run to append.
     */
    public void append(Run run) {
        Node newNode = new Node(run);
        tail.next = newNode;
        tail = newNode;
        ++size;
    }

    /**
     * Returns the first run of this queue.
     *
     * @return the run in the head.
     */
    public Run first() {
        return head.run;
    }

    /**
     * Returns the second run.
     *
     * @return the run right after the head of this queue.
     */
    public Run second() {
        return head.next.run;
    }

    /**
     * Returns the tail run of this queue.
     *
     * @return the tail run of this queue.
     */
    public Run last() {
        return tail.run;
    }

    /**
     * Duh.
     *
     * @return the run stored in this queue.
     */
    public int size() {
        return size;
    }

    /**
     * Merges the first 2 runs into one.
     */
    public void merge() {
        Node remove = head;
        head = head.next;
        head.run.from = remove.run.from;
        remove.next = null;
        --size;
    }

    /**
     * Sends the first element to the end of this run queue.
     */
    public void bounce() {
        Node node = head;
        head = head.next;
        tail.next = node;
        node.next = null;
        tail = node;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024).append("<");

        for (Node n = head; n != null; n = n.next) {
            sb.append(n.run);

            if (n != tail) {
                sb.append(", ");
            }
        }

        return sb.append(">").toString();
    }
}
