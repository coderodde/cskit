package net.coderodde.cskit.sorting;

import java.util.Comparator;
import static net.coderodde.cskit.Utilities.reverse;

/**
 * This class implements the run scanning logic.
 *
 * @author Rodion Efremov
 * @version 1.618 (9.12.2013)
 */
public class RunScanner<E extends Comparable<? super E>> {

    public RunQueue scan(E[] array, int from, int to) {
        if (from <= to) {
            return ascendingScan(array, from, to);
        } else {
            return descendingScan(array, to, from);
        }
    }

    private RunQueue ascendingScan(E[] array, int from, int to) {
        if (from == to) {
            return new RunQueue(new Run(from, from));
        }

        RunQueue queue;
        int head = from;
        int left = from;
        int right = left + 1;
        final int last = to;

        if (array[left++].compareTo(array[right++]) <= 0) {
            while (left < last && array[left].compareTo(array[right]) <= 0) {
                left++;
                right++;
            }

            queue = new RunQueue(new Run(head, left));
        } else {
            while (left < last && array[left].compareTo(array[right]) > 0) {
                left++;
                right++;
            }

            Run run = new Run(head, left);
            reverse(array, head, left);
            queue = new RunQueue(run);
        }

        left++;
        right++;

        if (left == last) {
            queue.append(new Run(left, left));
            return queue;
        }

        while (left < last) {
            head = left;

            if (array[left++].compareTo(array[right++]) <= 0) {
                while (left < last
                        && array[left].compareTo(array[right]) <= 0) {
                    left++;
                    right++;
                }

                queue.append(new Run(head, left));
            } else {
                while (left < last
                        && array[left].compareTo(array[right]) > 0) {
                    left++;
                    right++;
                }

                Run run = new Run(head, left);
                reverse(array, head, left);
                queue.append(run);
            }

            left++;
            right++;
        }

        if (left == last) {
            queue.append(new Run(left, left));
        }

        return queue;
    }

    private RunQueue descendingScan(E[] array, int from, int to) {
        RunQueue queue;
        int head = from;
        int left = from;
        int right = left + 1;
        final int last = to;

        if (array[left++].compareTo(array[right++]) >= 0) {
            while (left < last && array[left].compareTo(array[right]) >= 0) {
                left++;
                right++;
            }

            queue = new RunQueue(new Run(head, left));
        } else {
            while (left < last && array[left].compareTo(array[right]) < 0) {
                left++;
                right++;
            }

            Run run = new Run(head, left);
            reverse(array, head, left);
            queue = new RunQueue(run);
        }

        left++;
        right++;

        if (left == last) {
            queue.append(new Run(left, left));
            return queue;
        }

        while (left < last) {
            head = left;

            if (array[left++].compareTo(array[right++]) >= 0) {
                while (left < last
                        && array[left].compareTo(array[right]) >= 0) {
                    left++;
                    right++;
                }

                queue.append(new Run(head, left));
            } else {
                while (left < last
                        && array[left].compareTo(array[right]) < 0) {
                    left++;
                    right++;
                }

                Run run = new Run(head, left);
                reverse(array, head, left);
                queue.append(run);
            }

            left++;
            right++;
        }

        if (left == last) {
            queue.append(new Run(left, left));
        }

        return queue;
    }
}
