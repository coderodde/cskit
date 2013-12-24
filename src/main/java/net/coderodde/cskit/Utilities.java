package net.coderodde.cskit;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.DoubleWeightFunction;
import net.coderodde.cskit.graph.p2psp.general.CoordinateMap;
import net.coderodde.cskit.graph.p2psp.general.HeuristicFunction;
import net.coderodde.cskit.sorting.Range;

/**
 * This class contains general utilities.
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public class Utilities {

    public static class Pair<F, S> {
        public F first;
        public S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public Pair() {

        }
    }

    public static class Triple<F, S, T> {
        public F first;
        public S second;
        public T third;

        public Triple(F first, S second, T third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public Triple() {

        }
    }

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

    public static double getPathCost(List<DirectedGraphNode> path,
                                     DoubleWeightFunction w) {
        double cost = 0;

        for (int i = 0; i < path.size() - 1; ++i) {
            cost += w.get(path.get(i), path.get(i + 1));
        }

        return cost;
    }

    public static List<DirectedGraphNode> tracebackPath(
            DirectedGraphNode target,
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

    public static final Pair<List<DirectedGraphNode>, DoubleWeightFunction>
            getWeightedGraph(int size, float elf, Random r) {
        List<DirectedGraphNode> graph = new ArrayList<DirectedGraphNode>(size);
        DoubleWeightFunction w = new DoubleWeightFunction();

        for (int i = 0; i < size; ++i) {
            graph.add(new DirectedGraphNode("" + i));
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (r.nextFloat() < elf) {
                    graph.get(i).addChild(graph.get(j));
                    w.put(graph.get(i), graph.get(j), 10.0 * r.nextDouble());
                }
            }
        }

        for (int i = 0; i < size - 1; ++i) {
            graph.get(i).addChild(graph.get(i + 1));
            w.put(graph.get(i), graph.get(i + 1), 10.0 * r.nextDouble());
        }

        graph.get(graph.size() - 1).addChild(graph.get(0));
        w.put(graph.get(graph.size() - 1), graph.get(0), 1.0 * r.nextDouble());

        return new Pair<List<DirectedGraphNode>, DoubleWeightFunction>
                   (graph, w);
    }

    public static final List<DirectedGraphNode>
            copy(List<DirectedGraphNode> graph) {
        List<DirectedGraphNode> copyGraph =
                new ArrayList<DirectedGraphNode>(graph.size());

        Map<DirectedGraphNode, DirectedGraphNode> map =
                   new HashMap<DirectedGraphNode,
                               DirectedGraphNode>(graph.size());

        for (DirectedGraphNode u : graph) {
            DirectedGraphNode copy = new DirectedGraphNode(u.getName());
            copyGraph.add(copy);
            map.put(u, copy);
        }

        for (DirectedGraphNode u : graph) {
            DirectedGraphNode copyFrom = map.get(u);

            for (DirectedGraphNode child : u) {
                DirectedGraphNode copyTo = map.get(child);
                copyFrom.addChild(copyTo);
            }
        }

        return copyGraph;
    }

    public static final List<DirectedGraphNode>
            getResidualGraphOf(List<DirectedGraphNode> graph) {
        List<DirectedGraphNode> residualGraph =
                new ArrayList<DirectedGraphNode>(graph.size());

        Map<DirectedGraphNode, DirectedGraphNode> map =
                   new HashMap<DirectedGraphNode,
                               DirectedGraphNode>(graph.size());

        for (DirectedGraphNode u : graph) {
            DirectedGraphNode copy = new DirectedGraphNode(u.getName());
            residualGraph.add(copy);
            map.put(u, copy);
        }

        for (DirectedGraphNode u : graph) {
            DirectedGraphNode from = u;
            DirectedGraphNode residualTo = map.get(u);

            for (DirectedGraphNode child : u) {
                DirectedGraphNode residualFrom = map.get(child);
                residualFrom.addChild(residualTo);
            }
        }

        return residualGraph;
    }

    public static final Pair<List<DirectedGraphNode>, DoubleWeightFunction>
            getRandomFlowNetwork(int size,
                                 float elf,
                                 Random r,
                                 double maxCapacity) {
        List<DirectedGraphNode> graph = new ArrayList<DirectedGraphNode>(size);
        DoubleWeightFunction c = new DoubleWeightFunction();

        for (int i = 0; i < size; ++i) {
            graph.add(new DirectedGraphNode("" + i));
        }

        for (int i = 1; i < size; ++i) {
            for (int j = 0; j < i; ++j) {
                if (r.nextFloat() < elf) {
                    DirectedGraphNode from = graph.get(i);
                    DirectedGraphNode to = graph.get(j);

                    from.addChild(to);
                    c.put(from, to, maxCapacity * r.nextDouble());
                }
            }
        }

        for (int i = 0; i < size - 1; ++i) {
            DirectedGraphNode from = graph.get(i);
            DirectedGraphNode to = graph.get(i + 1);

            from.addChild(to);
            c.put(from, to, maxCapacity * r.nextDouble());
        }

        graph.get(graph.size() - 1).addChild(graph.get(0));
        c.put(graph.get(graph.size() - 1),
              graph.get(0),
              maxCapacity * r.nextDouble());

        return new Pair<List<DirectedGraphNode>, DoubleWeightFunction>(
                graph,
                c);
    }

    public static boolean epsilon(double a, double b, double e) {
        return Math.abs(a - b) < e;
    }

    /**
     * Creates the structures defining a laid-out graph.
     *
     * @param size the number of nodes.
     * @param elf edge load factor.
     * @param r the random number generator.
     * @return the graph structures.
     */
    public static final Triple<List<DirectedGraphNode>,
                               DoubleWeightFunction,
                               CoordinateMap> getRandomGraph(
                                    int size,
                                    float elf,
                                    Random r,
                                    HeuristicFunction f) {
        ArrayList<DirectedGraphNode> graph =
                new ArrayList<DirectedGraphNode>(size);
        DoubleWeightFunction w = new DoubleWeightFunction();
        CoordinateMap m = new CoordinateMap(4, size);

        for (int i = 0; i < size; ++i) {
            DirectedGraphNode u = new DirectedGraphNode("" + i);
            graph.add(u);
            m.put(u, getRandomCoordinates(4, r, 1000));
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (elf < r.nextFloat()) {
                    graph.get(i).addChild(graph.get(j));
                    w.put(graph.get(i),
                          graph.get(j),
                          1.5 * f.get(m.get(graph.get(i)),
                                      m.get(graph.get(j))));
                }
            }
        }

        for (int i = 0; i < size - 1; ++i) {
            graph.get(i).addChild(graph.get(i + 1));
            w.put(graph.get(i),
                  graph.get(i + 1),
                  1.5 * f.get(m.get(graph.get(i)),
                              m.get(graph.get(i + 1))));
        }

        graph.get(size - 1).addChild(graph.get(0));
        w.put(graph.get(size - 1),
              graph.get(0),
              1.5 * f.get(m.get(graph.get(size - 1)),
                          m.get(graph.get(0))));

        return new Triple<List<DirectedGraphNode>,
                          DoubleWeightFunction,
                          CoordinateMap>(graph, w, m);

    }

    public static double[] getRandomCoordinates(int n, Random r, double max) {
        double[] vec = new double[n];

        for (int i = 0; i < n; ++i) {
            vec[i] = max * r.nextDouble();
        }

        return vec;
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
