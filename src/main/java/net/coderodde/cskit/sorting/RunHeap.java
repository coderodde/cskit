package net.coderodde.cskit.sorting;

/**
 * This class implements a run heap for the sake of heap-selection sort.
 *
 * @author Rodion Efremov
 * @version 1.618 (14.12.2013)
 */
class RunHeap<E extends Comparable<? super E>> {
    private E[] array;
    private Run[] runs;
    private int size;

    RunHeap(int size, E[] array) {
        runs = new Run[size];
        this.array = array;
    }

    E min() {
        return array[runs[0].from];
    }

    void inc() {
        if (runs[0].from == runs[0].to) {
            Run last = runs[--size];
            runs[0] = last;
            // Establish the invariant.
            --last.from;
            inc();
        } else {
            ++runs[0].from;
            int nodeIndex = 0;
            int leftChildIndex = 1;
            int rightChildIndex = 2;
            int minIndex = 0;

            for (;;) {
                if (leftChildIndex < size
                        && array[runs[leftChildIndex].from].compareTo(
                           array[runs[nodeIndex].from]) < 0) {
                    minIndex = leftChildIndex;
                }

                if (rightChildIndex < size
                        && array[runs[rightChildIndex].from].compareTo(
                           array[runs[minIndex].from]) < 0) {
                    minIndex = rightChildIndex;
                }

                if (minIndex == nodeIndex) {
                    return;
                }

                // Sift down the run node.
                int oldNodeIndex = nodeIndex;
                nodeIndex = minIndex;

                // Swap.
                Run run = runs[minIndex];
                runs[minIndex] = runs[oldNodeIndex];
                runs[oldNodeIndex] = run;

                leftChildIndex = (nodeIndex << 1) + 1;
                rightChildIndex = leftChildIndex + 1;
            }
        }
    }

    void insert(Run r) {
        int nodeIndex = size;
        runs[nodeIndex] = r;
        size++;

        for (;;) {
            int parentIndex = (nodeIndex - 1) >> 1;

            if (parentIndex < 0) {
                return;
            }

            if (array[runs[parentIndex].from].compareTo(
                    array[runs[nodeIndex].from]) > 0) {
                // Sift up the node.
                Run tmp = runs[parentIndex];
                runs[parentIndex] = runs[nodeIndex];
                runs[nodeIndex] = tmp;
                nodeIndex = parentIndex;
            } else {
                return;
            }
        }
    }
}
