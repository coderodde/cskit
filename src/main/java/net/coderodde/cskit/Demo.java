package net.coderodde.cskit;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.coderodde.cskit.ds.pq.BinaryHeap;
import net.coderodde.cskit.ds.pq.PriorityQueue;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.DoubleWeightFunction;
import net.coderodde.cskit.graph.p2psp.general.AStarFinder;
import net.coderodde.cskit.graph.p2psp.general.BidirectionalDijkstraFinder;
import net.coderodde.cskit.graph.p2psp.general.CoordinateMap;
import net.coderodde.cskit.graph.p2psp.general.DijkstraFinder;
import net.coderodde.cskit.graph.p2psp.general.EuclidianMetric;
import net.coderodde.cskit.graph.p2psp.general.GeneralPathFinder;
import net.coderodde.cskit.graph.p2psp.uniform.BidirectionalBreadthFirstSearchFinder;
import net.coderodde.cskit.graph.p2psp.uniform.BreadthFirstSearchFinder;
import net.coderodde.cskit.graph.p2psp.uniform.ParallelBidirectionalBFSFinder;
import net.coderodde.cskit.graph.p2psp.uniform.UniformCostPathFinder;
import net.coderodde.cskit.sorting.BatchersSort;
import net.coderodde.cskit.sorting.CombSort;
import net.coderodde.cskit.sorting.CountingSort;
import net.coderodde.cskit.sorting.HeapSelectionSort;
import net.coderodde.cskit.sorting.IterativeMergeSort;
import net.coderodde.cskit.sorting.NaturalMergeSort;
import net.coderodde.cskit.sorting.ObjectSortingAlgorithm;
import net.coderodde.cskit.sorting.TreeSort;
import static net.coderodde.cskit.Utilities.Triple;
import static net.coderodde.cskit.Utilities.allWeakEquals;
import static net.coderodde.cskit.Utilities.debugPrintArray;
import static net.coderodde.cskit.Utilities.generateSimpleGraph;
import static net.coderodde.cskit.Utilities.getPathCost;
import static net.coderodde.cskit.Utilities.getPresortedArray;
import static net.coderodde.cskit.Utilities.getRandomGraph;
import static net.coderodde.cskit.Utilities.getRandomIntegerArray;
import static net.coderodde.cskit.Utilities.isConnectedPath;
import static net.coderodde.cskit.Utilities.isSorted;
import static net.coderodde.cskit.Utilities.line;
import static net.coderodde.cskit.Utilities.pathsAreSame;
import static net.coderodde.cskit.Utilities.title;
import static net.coderodde.cskit.Utilities.title2;
import net.coderodde.cskit.ds.tree.OrderStatisticTree;

/**
 * Hello from cskit.
 *
 */
public class Demo{

    public static void main(String... args) {
//        profileObjectSortingAlgorithms(new BatchersSort<Integer>(),
//                                       new CombSort<Integer>(),
//                                       new CountingSort<Integer>(),
//                                       new HeapSelectionSort<Integer>(),
//                                       new IterativeMergeSort<Integer>(),
//                                       new NaturalMergeSort<Integer>(),
//                                       new TreeSort<Integer>());
//        profileShortestPathAlgorithms();
//        profileBreadthFirstSearchAlgorithms();
        debugOST();
    }

    public static void profileBreadthFirstSearchAlgorithms() {
        title("Uniform cost graph search");
        final long SEED = System.currentTimeMillis();
        final Random r = new Random(SEED);
        final int SIZE = 50010;
        final float LOAD_FACTOR = 5.5f / SIZE;

        System.out.println("Nodes in the graph: " + SIZE + ", load factor: "
                + LOAD_FACTOR);

        System.out.println("Seed: " + SEED);

        List<DirectedGraphNode> graph =
                generateSimpleGraph(SIZE, LOAD_FACTOR, r);

        DirectedGraphNode source = graph.get(r.nextInt(SIZE));
        DirectedGraphNode target = graph.get(r.nextInt(SIZE));

        UniformCostPathFinder finder1 =
                new BreadthFirstSearchFinder();

        UniformCostPathFinder finder2 =
                new BidirectionalBreadthFirstSearchFinder();

        UniformCostPathFinder finder3 =
                new ParallelBidirectionalBFSFinder();

        long ta = System.currentTimeMillis();
        List<DirectedGraphNode> path1 = finder1.find(source, target);
        long tb = System.currentTimeMillis();

        System.out.println("BreadthFirstSearchFinder in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();
        List<DirectedGraphNode> path2 = finder2.find(source, target);
        tb = System.currentTimeMillis();

        System.out.println("BidirectionalBreadthFirstSearchFinder in "
                + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();
        List<DirectedGraphNode> path3 = finder3.find(source, target);
        tb = System.currentTimeMillis();

        System.out.println("ParallelBidirectionalBFSFinder in "
                + (tb - ta) + " ms.");

        line();

        boolean eq = path1.size() == path2.size()
                  && path2.size() == path3.size();

        if (eq == true) {
            System.out.println("Paths are of same length: " + eq
                    + ", length: " + path1.size());
        } else {
            System.out.println("Erroneous paths! Lengths: "
                    + path1.size() + ", " + path2.size()
                    + " and " + path3.size() + ".");
        }

        boolean ok1 = (isConnectedPath(path1)
                && path1.get(0).equals(source)
                && path1.get(path1.size() - 1).equals(target));

        boolean ok2 = (isConnectedPath(path2)
                && path2.get(0).equals(source)
                && path2.get(path2.size() - 1).equals(target));

        boolean ok3 = (isConnectedPath(path3)
                && path3.get(0).equals(source)
                && path3.get(path3.size() - 1).equals(target));



        System.out.println("Breadth-first search path OK: " + ok1);
        System.out.println("Bidirectional breadth-first search path OK: "
                + ok2);
        System.out.println("Bidirectional parallel BFS path OK: "
                + ok3);

        line();

        System.gc();
    }

    private static void profileObjectSortingAlgorithms(
            ObjectSortingAlgorithm<Integer>... algos) {
        title("Object sorting algorithms");

        ////

        int SIZE = 200000;
        Random r = new Random();

        Integer[] array = getRandomIntegerArray(SIZE, 0, 100, r);

        profileSortingAlgorithmsOn(array, "Small amount of different elements"
                + ", size: " + SIZE + ", random order", algos);

        ////

        SIZE = 20000;

        array = getRandomIntegerArray(SIZE, 0, 100, r);

        profileSortingAlgorithmsOn(array, "Small amount of different elements"
                + ", size: " + SIZE + ", random order", algos);

        ////

        SIZE = 200000;

        array = getRandomIntegerArray(SIZE, r);

        profileSortingAlgorithmsOn(array, "Random elements, size: " + SIZE,
                                   algos);

        ////

        SIZE = 20000;

        array = getRandomIntegerArray(SIZE, r);

        profileSortingAlgorithmsOn(array, "Random elements, size: " + SIZE,
                                   algos);

        ////

        SIZE = 200000;
        int RUNS = 16;

        array = getPresortedArray(SIZE, RUNS);

        profileSortingAlgorithmsOn(array, "Presorted array of " + SIZE +
                " elements with " + RUNS + " runs", algos);

        ////

        SIZE = 20000;
        RUNS = 16;

        array = getPresortedArray(SIZE, RUNS);

        profileSortingAlgorithmsOn(array, "Presorted array of " + SIZE +
                " elements with " + RUNS + " runs", algos);
    }

    private static void profileSortingAlgorithmsOn(
            Integer[] array, String title,
            ObjectSortingAlgorithm<Integer>... algos) {
        title2(title);

        // + 1 for Arrays.sort().
        Integer[][] arrays = new Integer[algos.length + 1][];
        arrays[0] = array;

        for (int i = 1; i < arrays.length; ++i) {
            arrays[i] = arrays[0].clone();
        }

        // - 1, for it is the arrray going to Arrays.sort().
        for (int i = 0; i < arrays.length - 1; ++i) {
            System.out.print(algos[i].getClass().getName() + " in ");

            long ta = System.currentTimeMillis();
            algos[i].sort(arrays[i]);
            long tb = System.currentTimeMillis();

            System.out.print((tb - ta) + " ms, sorted: ");
            System.out.println(isSorted(arrays[i]));
        }

        long ta = System.currentTimeMillis();
        Arrays.sort(arrays[arrays.length - 1]);
        long tb = System.currentTimeMillis();

        System.out.println("Arrays.sort() in " + (tb - ta) + " ms, sorted: "
                + isSorted(arrays[arrays.length - 1]));

        line();

        System.out.println("All arrays same: " + allWeakEquals(arrays));
    }

    private static void profileBinaryHeap() {
        BinaryHeap<Integer> heap = new BinaryHeap<Integer>();

        for (int i = 10; i > 0; --i) {
            heap.insert(i, i);
        }

        while(heap.isEmpty() == false) {
            System.out.print(heap.extractMinimum() + " ");
        }

        System.out.println();

        heap.clear();
        line();

        for (int i = 10; i > 0; --i) {
            heap.insert(i, i);
        }

        heap.decreasePriority(10, 0);

        while(heap.isEmpty() == false) {
            System.out.print(heap.extractMinimum() + " ");
        }

        System.out.println();
    }

    private static void profileShortestPathAlgorithms() {
        final int N = 2000;
        final float LOAD_FACTOR = 10.0f / N;

        title("General shortest path algorithms with " + N + " nodes");

        Random r = new Random();
        Triple<List<DirectedGraphNode>,
               DoubleWeightFunction,
               CoordinateMap> triple = getRandomGraph(N, LOAD_FACTOR, r, new EuclidianMetric(null, null));

        DirectedGraphNode source = triple.first.get(r.nextInt(N));
        DirectedGraphNode target = triple.first.get(r.nextInt(N));

        System.out.println("Source: " + source.toString());
        System.out.println("Target: " + target.toString());

        PriorityQueue<DirectedGraphNode> OPEN =
                new BinaryHeap<DirectedGraphNode>(N);

        GeneralPathFinder finder1 = new DijkstraFinder(OPEN);

        long ta = System.currentTimeMillis();

        List<DirectedGraphNode> path1 =
                finder1.find(source, target, triple.second);

        long tb = System.currentTimeMillis();

        System.out.println("DijkstraFinder in " + (tb - ta) + " ms, "
                + "path connected: " + isConnectedPath(path1)
                + ", cost: " + getPathCost(path1, triple.second));

        OPEN = new BinaryHeap<DirectedGraphNode>(N);

        GeneralPathFinder finder2 =
                new AStarFinder(OPEN,
                                new EuclidianMetric(
                                    triple.third,
                                    target));

        ta = System.currentTimeMillis();

        List<DirectedGraphNode> path2 =
                finder2.find(source, target, triple.second);

        tb = System.currentTimeMillis();

        System.out.println("AStarFinder in " + (tb - ta) + " ms, "
                + "path connected: " + isConnectedPath(path2)
                + ", cost: " + getPathCost(path2, triple.second));

        OPEN = new BinaryHeap<DirectedGraphNode>(N);

        GeneralPathFinder finder3 =
                new BidirectionalDijkstraFinder(OPEN);

        ta = System.currentTimeMillis();

        List<DirectedGraphNode> path3 =
                finder3.find(source, target, triple.second);

        tb = System.currentTimeMillis();

        System.out.println("BidirectionalDijkstraFinder in " + (tb - ta)
                + " ms, " + "path connected: " + isConnectedPath(path3)
                + ", cost: " + getPathCost(path3, triple.second));

        System.out.println("Path are same: " + pathsAreSame(path1, path2, path3));
    }

    private static void debugHeapSelectionSort() {
        Random r = new Random();
        Integer[] array = getRandomIntegerArray(10, 0, 10, r);

        debugPrintArray(array);
        new HeapSelectionSort<Integer>().sort(array);
        debugPrintArray(array);
    }

    private static void debugOST() {
        OrderStatisticTree<Integer, Integer> tree =
                new OrderStatisticTree<Integer, Integer>();

        for (int i = 0; i < 100; ++i) {
            tree.put(i, i);
        }

        System.out.println("Size: " + tree.size());

        for (Integer i = 0; i < tree.size(); ++i) {
            System.out.println("i.equals(tree.get(i)): " + i.equals(tree.get(i)));
            System.out.println("i.equals(tree.entryAt(i).getKey()): " + i.equals(tree.entryAt(i).getKey()));
            System.out.println("i.equals(tree.entryAt(i).getValue()): " + i.equals(tree.entryAt(i).getValue()));
            System.out.println(i + ".equals(" + tree.getRankOf(i) + "): " + i.equals(tree.getRankOf(i)));
        }

        for (Integer i = 20; i < 100; ++i) {
            tree.remove(i);
        }

        System.out.println("size: " + tree.size());
        System.out.println("Healthy: " + tree.isHealthy());
    }
}
