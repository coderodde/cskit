package net.coderodde.cskit;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.sorting.Range;

/**
 * This class contains general utilities.
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public class Utilities {

    public static <T> T checkNotNull(T field, String errmsg) {
        if (field == null) {
            throw new NullPointerException(errmsg);
        }

        return field;
    }

    public static void checkModCount(long expected,
            long actual,
            String errmsg) {
        if (expected != actual) {
            throw new ConcurrentModificationException(errmsg);
        }
    }

    public static List<DirectedGraphNode> tracebackPath(DirectedGraphNode target,
            Map<DirectedGraphNode, DirectedGraphNode> parentMap) {
        ArrayList<DirectedGraphNode> path = new ArrayList<DirectedGraphNode>();
        while (target != null) {
            path.add(target);
            target = parentMap.get(target);
        }
        java.util.Collections.reverse(path);
        return path;
    }

    public static DirectedGraphNode findTouchNode(
            Set<DirectedGraphNode> levelA,
            Set<DirectedGraphNode> levelB,
            Map<DirectedGraphNode, DirectedGraphNode> parentMapA,
            Map<DirectedGraphNode, DirectedGraphNode> parentMapB,
            Map<DirectedGraphNode, Integer> distanceMapA,
            Map<DirectedGraphNode, Integer> distanceMapB) {
        Integer tmp;
        DirectedGraphNode touch = null;
        int minDistance = Integer.MAX_VALUE;

        for (DirectedGraphNode u : levelA) {
            tmp = distanceMapB.get(u);

            if (tmp != null && minDistance > tmp + distanceMapA.get(u)) {
                minDistance = tmp + distanceMapA.get(u);
                touch = u;
            }
        }

        for (DirectedGraphNode u : levelB) {
            tmp = distanceMapA.get(u);

            if (tmp != null && minDistance > tmp + distanceMapB.get(u)) {
                minDistance = tmp + distanceMapB.get(u);
                touch = u;
            }
        }

        return touch;
    }

    public static List<DirectedGraphNode> tracebackPathBidirectional(
            DirectedGraphNode touchNode,
            Map<DirectedGraphNode, DirectedGraphNode> parentMapA,
            Map<DirectedGraphNode, DirectedGraphNode> parentMapB) {
        ArrayList<DirectedGraphNode> path = new ArrayList<DirectedGraphNode>();
        DirectedGraphNode tmp = touchNode;

        while (tmp != null) {
            path.add(tmp);
            tmp = parentMapA.get(tmp);
        }

        java.util.Collections.reverse(path);
        tmp = parentMapB.get(touchNode);

        while (tmp != null) {
            path.add(tmp);
            tmp = parentMapB.get(tmp);
        }

        return path;
    }

    public static final void line() {
        System.out.println(
                "________________________________________"
                + "________________________________________");
    }

    public static final void title(String text) {
        int w = text.length() + 2;
        int leftPadding = (80 - w) / 2;
        int rightPadding = 80 - w - leftPadding;
        StringBuilder sb = new StringBuilder(80);

        for (int i = 0; i < leftPadding; ++i) {
            sb.append('/');
        }

        sb.append(' ');
        sb.append(text);
        sb.append(' ');

        for (int i = 0; i < rightPadding; ++i) {
            sb.append('/');
        }

        System.out.println(sb.toString());
    }

    public static final void title2(String text) {
        int w = text.length() + 2;
        int leftPadding = (80 - w) / 2;
        int rightPadding = 80 - w - leftPadding;
        StringBuilder sb = new StringBuilder(80);

        for (int i = 0; i < leftPadding; ++i) {
            sb.append('-');
        }

        sb.append(' ');
        sb.append(text);
        sb.append(' ');

        for (int i = 0; i < rightPadding; ++i) {
            sb.append('-');
        }

        System.out.println(sb.toString());
    }

    public static final boolean isConnectedPath(List<DirectedGraphNode> candidate) {
        for (int i = 0; i < candidate.size() - 1; ++i) {
            if (candidate.get(i).hasChild(candidate.get(i + 1)) == false) {
                return false;
            }
        }

        return true;
    }

    public static final boolean pathsAreSame(List<DirectedGraphNode>... paths) {
        for (int i = 0; i < paths.length - 1; ++i) {
            if (paths[i].size() != paths[i + 1].size()) {
                return false;
            }
        }

        for (int i = 0; i < paths[0].size(); ++i) {
            for (int p = 0; p < paths.length - 1; ++p) {
                if (paths[p].get(i).equals(paths[p + 1].get(i)) == false) {
                    return false;
                }
            }
        }

        return true;
    }

    public static final List<DirectedGraphNode> generateSimpleGraph(int size, float edgeLoadFactor, Random r) {
        List<DirectedGraphNode> graph = new ArrayList<DirectedGraphNode>(size);

        for (int i = 0; i < size; ++i) {
            graph.add(new DirectedGraphNode("" + i));
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (r.nextFloat() < edgeLoadFactor) {
                    graph.get(i).addChild(graph.get(j));
                }
            }
        }

        for (int i = 0; i < size - 1; ++i) {
            graph.get(i).addChild(graph.get(i + 1));
        }

        graph.get(graph.size() - 1).addChild(graph.get(0));

        return graph;
    }

    public static final <E extends Comparable<? super E>>
            boolean isSorted(E[] array, Range r) {
        if (r.from <= r.to) {
            return ascendingIsSorted(array, r.from, r.to);
        } else {
            return descendingIsSorted(array, r.to, r.from);
        }
    }

    public static final <E extends Comparable<? super E>>
            boolean isSorted(E[] array) {
        return isSorted(array, new Range(0, array.length - 1));
    }

    public static final Integer[] getRandomIntegerArray(int size,
                                                        int min,
                                                        int max,
                                                        Random r) {
        Integer[] array = new Integer[size];

        for (int i = 0; i < size; ++i) {
            array[i] = min + r.nextInt(max - min + 1);
        }

        return array;
    }

    public static final Integer[] getRandomIntegerArray(int size, Random r) {
        return getRandomIntegerArray(size, 0, 1000000000, r);
    }

    public static final Integer[] getPresortedArray(int size, int runAmount) {
        Integer[] array = new Integer[size];
        final int runLength = size / runAmount + 1;

        for (int i = 0; i < size; ++i) {
            array[i] = i % runLength;
        }

        return array;
    }

    public static final <E> void reverse(E[] array, int from, int to) {
        while (from < to) {
            E tmp = array[from];
            array[from] = array[to];
            array[to] = tmp;
            from++;
            to--;
        }
    }

    private static final <E extends Comparable<? super E>>
            boolean ascendingIsSorted(E[] array, int from, int to) {
        for (int i = from; i < to; ++i) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }

        return true;
    }

    public static final <E extends Comparable<? super E>>
            boolean descendingIsSorted(E[] array, int from, int to) {
        for (int i = from; i < to; ++i) {
            if (array[i].compareTo(array[i + 1]) < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether input arrays are <b>same</b>. For any index
     * <code>i</code> and any two arrays <code>A, B</code> (transitivity
     * assumed), it holds <code>A[i].equals(B[i])</code>, yet it is allowed to
     * hold also <code>A[i] != B[i]</code>.
     *
     * @param <E> the element type.
     * @param arrays the var-arg array of arrays to check.
     * @return <code>true</code> if arrays are identical, <code>false</code>
     * otherwise.
     */
    public static final <E> boolean allWeakEquals(E[]... arrays) {
        for (int i = 0; i < arrays.length - 1; ++i) {
            if (arrays[i].length != arrays[i + 1].length) {
                return false;
            }
        }

        for (int i = 0; i < arrays[0].length; ++i) {
            for (int arrayId = 0; arrayId < arrays.length - 1; ++arrayId) {
                if (arrays[arrayId][i].equals(arrays[arrayId + 1][i]) == false) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks whether input arrays are <b>identical</b>.
     *
     * @param <E> the element type.
     * @param arrays the var-arg array of arrays to check.
     * @return <code>true</code> if arrays are identical, <code>false</code>
     * otherwise.
     */
    public static final <E> boolean allStrongEquals(E[]... arrays) {
        for (int i = 0; i < arrays.length - 1; ++i) {
            if (arrays[i].length != arrays[i + 1].length) {
                return false;
            }
        }

        for (int i = 0; i < arrays[0].length; ++i) {
            for (int arrayId = 0; arrayId < arrays.length - 1; ++arrayId) {
                if (arrays[arrayId][i] != arrays[arrayId + 1][i]) {
                    return false;
                }
            }
        }

        return true;
    }

    public static final <E> void debugPrintArray(E[] array) {
        debugPrintArray(array, new Range(0, array.length - 1));
    }

    public static final <E> void debugPrintArray(E[] array, Range r) {
        for (int i = r.from; i <= r.to; ++i) {
            System.out.print(array[i] + " ");
        }

        System.out.println();
    }

    public static final Integer[] getAscendingArray(int size) {
        Integer[] array = new Integer[size];

        for (int i = 0; i < size; ++i) {
            array[i] = i;
        }

        return array;
    }

    public static final <E> void shuffle(E[] array, Random r) {
        for (int i = 0; i < array.length / 2; ++i) {
            int j = r.nextInt(array.length);
            int k = r.nextInt(array.length);

            E tmp = array[j];
            array[j] = array[k];
            array[k] = tmp;
        }
    }
}
