package net.coderodde.cskit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import net.coderodde.cskit.Utilities.Pair;
import static net.coderodde.cskit.Utilities.Triple;
import static net.coderodde.cskit.Utilities.allWeakEquals;
import static net.coderodde.cskit.Utilities.debugPrintArray;
import static net.coderodde.cskit.Utilities.epsilonEquals;
import static net.coderodde.cskit.Utilities.generateSimpleGraph;
import static net.coderodde.cskit.Utilities.getPathCost;
import static net.coderodde.cskit.Utilities.getPresortedArray;
import static net.coderodde.cskit.Utilities.getRandomGraph;
import static net.coderodde.cskit.Utilities.getRandomIntegerArray;
import static net.coderodde.cskit.Utilities.isConnectedPath;
import static net.coderodde.cskit.Utilities.isSorted;
import static net.coderodde.cskit.Utilities.isSpanningTree;
import static net.coderodde.cskit.Utilities.line;
import static net.coderodde.cskit.Utilities.pathsAreSame;
import static net.coderodde.cskit.Utilities.spanningTreesEqual;
import static net.coderodde.cskit.Utilities.sumEdgeWeights;
import static net.coderodde.cskit.Utilities.title;
import static net.coderodde.cskit.Utilities.title2;
import net.coderodde.cskit.ds.pq.BinaryHeap;
import net.coderodde.cskit.ds.pq.FibonacciHeap;
import net.coderodde.cskit.ds.pq.PriorityQueue;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.DirectedGraphWeightFunction;
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
import net.coderodde.cskit.ds.tree.OrderStatisticTree;
import net.coderodde.cskit.graph.UndirectedGraphEdge;
import net.coderodde.cskit.graph.UndirectedGraphNode;
import net.coderodde.cskit.graph.UndirectedGraphWeightFunction;
import net.coderodde.cskit.graph.flow.BidirectionalEdmondKarpFlowFinder;
import net.coderodde.cskit.graph.flow.EdmondKarpFlowFinder;
import net.coderodde.cskit.graph.flow.FlowFinder;
import net.coderodde.cskit.graph.mst.KruskalMSTFinder;
import net.coderodde.cskit.graph.mst.MinimumSpanningTreeFinder;
import net.coderodde.cskit.graph.mst.PrimMSTFinder;
import net.coderodde.cskit.sorting.HeapSort;
import net.coderodde.ds.list.TreeList;

/**
 * Hello from cskit. This is a performance demo.
 */
public class Demo{

    public static void main(String... args) {
        profileTreeList();
//        profileObjectSortingAlgorithms(new BatchersSort<Integer>(),
//                                       new CombSort<Integer>(),
//                                       new CountingSort<Integer>(),
//                                       new HeapSelectionSort<Integer>(),
//                                       new IterativeMergeSort<Integer>(),
//                                       new NaturalMergeSort<Integer>(),
//                                       new TreeSort<Integer>(),
//                                       new HeapSort<Integer>(
//                                            new BinaryHeap<Integer, Integer>()),
//                                       new HeapSort<Integer>(
//                                            new FibonacciHeap<Integer, Integer>())
//                );
//        profileShortestPathAlgorithms();
//        profileBreadthFirstSearchAlgorithms();
//        profileOrderStatisticTree();
//        profileMaxFlowAlgorithms();
//        profileMSTAlgorithms();
//        debugMaxFlowAlgorithms();
//        profileFibonacciHeap();
    }

    public static void profileOrderStatisticTree() {
        title("OrderStatisticTree demo");
        OrderStatisticTree<Integer, Integer> m1 =
                new OrderStatisticTree<Integer, Integer>();
        Map<Integer, Integer> m2 = new TreeMap<Integer, Integer>();

        long ta = System.currentTimeMillis();

        for (int i = 0; i < 100000; ++i) {
            m1.put(i, i);
        }

        long tb = System.currentTimeMillis();

        System.out.println("OST.put() in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 100000; ++i) {
            m2.put(i, i);
        }

        tb = System.currentTimeMillis();

        System.out.println("TreeMap.put() in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 102000; ++i) {
            m1.get(i);
        }

        tb = System.currentTimeMillis();

        System.out.println("OST.get() in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 102000; ++i) {
            m2.get(i);
        }

        tb = System.currentTimeMillis();

        System.out.println("TreeMap.get() in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 102000; ++i) {
            m1.remove(i);
        }

        tb = System.currentTimeMillis();

        System.out.println("OST.remove() in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 102000; ++i) {
            m2.remove(i);
        }

        tb = System.currentTimeMillis();

        System.out.println("TreeMap.remove() in " + (tb - ta) + " ms.");

        for (int i = 0; i < 100000; ++i) {
            m1.put(i, i);
            m2.put(i, i);
        }

        ta = System.currentTimeMillis();

        for (int i = 0; i < 20000; ++i) {
            m1.entryAt(i);
        }

        tb = System.currentTimeMillis();

        System.out.println("OST.entryAt() in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 20000; ++i) {
            getFromTreeMapHack(i, m2);
        }

        tb = System.currentTimeMillis();

        System.out.println("TreeMap dirty select() hack in " + (tb - ta)
                + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 20000; ++i) {
            m1.rankOf(i);
        }

        tb = System.currentTimeMillis();

        System.out.println("OST.rankOf() in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 20000; ++i) {
            getRankOfTreeMapHack(i, m2);
        }

        tb = System.currentTimeMillis();

        System.out.println("TreeMap dirty rankOf() hack in " + (tb - ta)
                + " ms.");

        line();
    }

    private static <K, V>   int getRankOfTreeMapHack(K key, Map<K, V> map) {
        int index = 0;

        for (K k : map.keySet()) {
            if (k.equals(key)) {
                return index;
            }

            ++index;
        }

        return -1;
    }

    private static <K, V> K getFromTreeMapHack(int key, Map<K, V> map) {
        int pos = 0;

        for (K k : map.keySet()) {
            if (key == pos) {
                return k;
            }

            pos++;
        }

        return null;
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

        long SEED = System.currentTimeMillis();

        System.out.println("Seed: " + SEED);

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
        BinaryHeap<Integer, Integer> heap = new BinaryHeap<Integer, Integer>();

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

    private static void profileFibonacciHeap() {
        FibonacciHeap<Integer, Integer> heap =
                new FibonacciHeap<Integer, Integer>();

        for (int i = 10; i > 0; --i) {
            heap.insert(i, i);
        }

        while(heap.isEmpty() == false) {
            System.out.println("Removing: " + heap.min());
            heap.extractMinimum();
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

    private static void profileShortestPathAlgorithmsOn(
            PriorityQueue<DirectedGraphNode, Double> pq,
            int size,
            long seed,
            float lf) {
        title2("General shortest path algorithms with " + pq.getClass().getName());

        Random r = new Random(seed);
        Triple<List<DirectedGraphNode>,
               DirectedGraphWeightFunction,
               CoordinateMap> triple =
                getRandomGraph(size, lf, r, new EuclidianMetric(null, null));

        DirectedGraphNode source = triple.first.get(r.nextInt(size));
        DirectedGraphNode target = triple.first.get(r.nextInt(size));

        System.out.println("Source: " + source.toString());
        System.out.println("Target: " + target.toString());

        PriorityQueue<DirectedGraphNode, Double> OPEN = pq.newInstance();

        GeneralPathFinder finder1 = new DijkstraFinder(OPEN);

        long ta = System.currentTimeMillis();

        List<DirectedGraphNode> path1 =
                finder1.find(source, target, triple.second);

        long tb = System.currentTimeMillis();

        System.out.println("DijkstraFinder in " + (tb - ta) + " ms, "
                + "path connected: " + isConnectedPath(path1)
                + ", cost: " + getPathCost(path1, triple.second));

        OPEN = pq.newInstance();

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

        OPEN = pq.newInstance();

        GeneralPathFinder finder3 =
                new BidirectionalDijkstraFinder(OPEN);

        ta = System.currentTimeMillis();

        List<DirectedGraphNode> path3 =
                finder3.find(source, target, triple.second);

        tb = System.currentTimeMillis();

        System.out.println("BidirectionalDijkstraFinder in " + (tb - ta)
                + " ms, " + "path connected: " + isConnectedPath(path3)
                + ", cost: " + getPathCost(path3, triple.second));

        line();

        System.out.println("Path are same: " + pathsAreSame(path1, path2, path3));

        line();
    }

    private static void profileShortestPathAlgorithms() {
        final int N = 2000;
        final float LOAD_FACTOR = 10.0f / N;
        title("General shortest path algoirhtms with " + N + " nodes");

        PriorityQueue<DirectedGraphNode, Double> pq =
                new BinaryHeap<DirectedGraphNode, Double>();

        final long SEED = System.currentTimeMillis();

        System.out.println("Seed: " + SEED);

        profileShortestPathAlgorithmsOn(
                new BinaryHeap<DirectedGraphNode, Double>(),
                N,
                SEED,
                LOAD_FACTOR);

        profileShortestPathAlgorithmsOn(
                new FibonacciHeap<DirectedGraphNode, Double>(),
                N,
                SEED,
                LOAD_FACTOR);
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
            System.out.println(i + ".equals(" + tree.rankOf(i) + "): " + i.equals(tree.rankOf(i)));
        }

        for (Integer i = 20; i < 100; ++i) {
            tree.remove(i);
        }

        System.out.println("size: " + tree.size());
        System.out.println("Healthy: " + tree.isHealthy());
    }

    private static void debugMaxFlowAlgorithms() {
        DirectedGraphNode Vancouver = new DirectedGraphNode("Vancover");
        DirectedGraphNode Edmonton = new DirectedGraphNode("Edmonton");
        DirectedGraphNode Calgary = new DirectedGraphNode("Calgary");
        DirectedGraphNode Saskatoon = new DirectedGraphNode("Saskatoon");
        DirectedGraphNode Regina = new DirectedGraphNode("Regina");
        DirectedGraphNode Winnipeg = new DirectedGraphNode("Winnipeg");

        DirectedGraphWeightFunction c = new DirectedGraphWeightFunction();

        /// 1 - 3
        Vancouver.addChild(Edmonton);
        c.put(Vancouver, Edmonton, 16.0);

        Vancouver.addChild(Calgary);
        c.put(Vancouver, Calgary, 13.0);

        Calgary.addChild(Edmonton);
        c.put(Calgary, Edmonton, 4.0);

        /// 4 - 6
        Edmonton.addChild(Saskatoon);
        c.put(Edmonton, Saskatoon, 12.0);

        Saskatoon.addChild(Calgary);
        c.put(Saskatoon, Calgary, 9.0);

        Calgary.addChild(Regina);
        c.put(Calgary, Regina, 14.0);

        /// 7 - 9
        Saskatoon.addChild(Winnipeg);
        c.put(Saskatoon, Winnipeg, 20.0);

        Regina.addChild(Saskatoon);
        c.put(Regina, Saskatoon, 7.0);

        Regina.addChild(Winnipeg);
        c.put(Regina, Winnipeg, 4.0);

        Pair<DirectedGraphWeightFunction, Double> pair =
                new EdmondKarpFlowFinder().find(Vancouver, Winnipeg, c);

        System.out.println("EdmondKarpFlowFinder: " + pair.second);

        Pair<DirectedGraphWeightFunction, Double> pair2 =
                new BidirectionalEdmondKarpFlowFinder().find(Vancouver,
                                                             Winnipeg,
                                                             c);

        System.out.println("BidirectionalEdmonFlowFinder: " + pair2.second);
    }

    private static void profileMaxFlowAlgorithms() {
        final int N = 5000;
        final float ELF = 5.0f / N;
        final long SEED = System.currentTimeMillis();

        title("Max-flow algorithm demo");
        System.out.println("Seed: " + SEED);

        Random r = new Random(SEED);

        Pair<List<DirectedGraphNode>, DirectedGraphWeightFunction> pair =
                Utilities.getRandomFlowNetwork(N, ELF, r, 10.0);

        FlowFinder.resolveParallelEdges(pair.first, pair.second);
        FlowFinder.removeSelfLoops(pair.first);

        DirectedGraphNode source = pair.first.get(r.nextInt(N));
        DirectedGraphNode sink = pair.first.get(r.nextInt(N));

        System.out.println("Source: " + source.toString());
        System.out.println("Sink:   " + sink.toString());
        long ta = System.currentTimeMillis();

        Pair<DirectedGraphWeightFunction, Double> result1 =
                new EdmondKarpFlowFinder()
                .find(source, sink, pair.second);

        long tb = System.currentTimeMillis();

        System.out.println("EdmondKarpFlowFinder in " + (tb - ta)
                + " ms, |f| = " + result1.second);

        ta = System.currentTimeMillis();

        Pair<DirectedGraphWeightFunction, Double> result2 =
                new BidirectionalEdmondKarpFlowFinder()
                .find(source, sink, pair.second);

        tb = System.currentTimeMillis();

        System.out.println("BidirectionalEdmondKarpFlowFinder in " + (tb - ta)
                + " ms, |f| = " + result2.second);

        ta = System.currentTimeMillis();

        line();

        System.out.println(
                "Flows equal: " + epsilonEquals(0.001,
                                                result1.second,
                                                result2.second));
    }

    private static void profileMSTAlgorithms() {
        final int N = 50;
        final float ELF = 5.0f / N;
        final long SEED = System.currentTimeMillis();

        title("Minimum-spanning-tree algorithm demo");
        System.out.println("Seed: " + SEED);

        Random r = new Random(SEED);

        Pair<List<UndirectedGraphNode>, UndirectedGraphWeightFunction> pair =
                Utilities.getRandomUndirectedGraph(N, ELF, r, 10.0);

        MinimumSpanningTreeFinder finder1 =
                new KruskalMSTFinder();

        long ta = System.currentTimeMillis();

        Pair<List<UndirectedGraphEdge>, Double> result1 =
                finder1.find(pair.first, pair.second);

        long tb = System.currentTimeMillis();

        System.out.println("Kruskal in " + (tb - ta) + " ms, " +
                "cost: " + result1.second +
                "/" + sumEdgeWeights(result1.first) +
                ", is spanning forest: " + isSpanningTree(result1.first)
                );

        MinimumSpanningTreeFinder finder2 =
                new PrimMSTFinder();

        ta = System.currentTimeMillis();

        Pair<List<UndirectedGraphEdge>, Double> result2 =
                finder2.find(pair.first, pair.second);

        tb = System.currentTimeMillis();

        System.out.println("Prim in " + (tb - ta) + " ms, " +
                "cost: " + result2.second + "/" +
                sumEdgeWeights(result2.first) +
                ", is spanning forest: " + isSpanningTree(result2.first));

        line();

        System.out.println("MST equal: " + spanningTreesEqual(result1.first,
                                                              result2.first));
    }

    private static void profileTreeList() {
        TreeList<Integer> list = new TreeList<Integer>();
        org.apache.commons.collections4.list.TreeList<Integer> enemyList =
                new org.apache.commons.collections4.list.TreeList<Integer>();

        title("coderodde's TreeList vs. Commons Collections TreeList");

        title2("Adding");

        System.out.println("My TreeList is healthy: " + list.isHealthy());

        long ta = System.currentTimeMillis();

        for (int i = 0; i < 10000; ++i) {
            list.add(i);
        }

        long tb = System.currentTimeMillis();

        System.out.println("My TreeList is healthy: " + list.isHealthy());

        System.out.println("My TreeList.add in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 10000; ++i) {
            enemyList.add(i);
        }

        tb = System.currentTimeMillis();

        System.out.println("CC TreeList.add in " + (tb - ta) + " ms.");

        title2("Getting by index");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 10000; ++i) {
            if (list.get(i) != i) {
                System.out.println("Fail!");
                break;
            }
        }

        tb = System.currentTimeMillis();

        System.out.println("My TreeList.get in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 0; i < 10000; ++i) {
            if (enemyList.get(i) != i) {
                System.out.println("Fail!");
                break;
            }
        }

        tb = System.currentTimeMillis();

        System.out.println("CC TreeList.get in " + (tb - ta) + " ms.");

        title2("Removing");

        ta = System.currentTimeMillis();

        for (int i = 9999; i >= 0; --i) {
            if (i % 2 == 1) {
                list.remove(i);
            }
        }

        tb = System.currentTimeMillis();

        System.out.println("My TreeList.remove in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();

        for (int i = 9999; i >= 0; --i) {
            if (i % 2 == 1) {
                enemyList.remove(i);
            }
        }

        tb = System.currentTimeMillis();

        System.out.println("CC TreeList.remove in " + (tb - ta) + " ms.");

        System.out.println("My TreeList is healthy: " + list.isHealthy());
    }
}
