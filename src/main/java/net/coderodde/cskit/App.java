package net.coderodde.cskit;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import static net.coderodde.cskit.Utilities.allWeakEquals;
import static net.coderodde.cskit.Utilities.debugPrintArray;
import static net.coderodde.cskit.Utilities.generateSimpleGraph;
import static net.coderodde.cskit.Utilities.getRandomIntegerArray;
import static net.coderodde.cskit.Utilities.isConnectedPath;
import static net.coderodde.cskit.Utilities.isSorted;
import static net.coderodde.cskit.Utilities.line;
import static net.coderodde.cskit.Utilities.title;
import static net.coderodde.cskit.Utilities.title2;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.p2psp.uniform.BidirectionalBreadthFirstSearchFinder;
import net.coderodde.cskit.graph.p2psp.uniform.BreadthFirstSearchFinder;
import net.coderodde.cskit.graph.p2psp.uniform.ParallelBidirectionalBFSFinder;
import net.coderodde.cskit.graph.p2psp.uniform.UniformCostPathFinder;
import net.coderodde.cskit.sorting.BatchersSort;
import net.coderodde.cskit.sorting.CombSort;
import net.coderodde.cskit.sorting.CountingSort;
import net.coderodde.cskit.sorting.IterativeMergesort;
import net.coderodde.cskit.sorting.NaturalMergeSort;
import net.coderodde.cskit.sorting.Range;
import net.coderodde.cskit.sorting.TreeSort;
/**
 * Hello from cskit.
 *
 */
public class App{

    public static void main(String... args) {
//        profileBreadthFirstSearchAlgorithms();
        profileObjectSortingAlgorithms();
        debugNaturalMergeSort();
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

    private static void profileObjectSortingAlgorithms() {
        System.out.println();
        title("Object sorting algorithms");
        title2("Small amount of different elements, random order");

        final int SIZE = 200000;
        final long SEED = System.currentTimeMillis();
        final Random r = new Random(SEED);

        Integer[] array1 = getRandomIntegerArray(SIZE, 0, 100, r);
        Integer[] array2 = array1.clone();
        Integer[] array3 = array1.clone();
        Integer[] array4 = array1.clone();
        Integer[] array5 = array1.clone();
        Integer[] array6 = array1.clone();
        Integer[] array10 = array1.clone();

        //// Comb sort ////

        long ta = System.currentTimeMillis();
        new CombSort<Integer>().sort(array1);
        long tb = System.currentTimeMillis();

        System.out.println("Comb sort in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array1));

        //// Counting sort ////

        ta = System.currentTimeMillis();
        new CountingSort<Integer>().sort(array2);
        tb = System.currentTimeMillis();

        System.out.println("Counting sort in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array2));

        //// Tree sort ////

        ta = System.currentTimeMillis();
        new TreeSort<Integer>().sort(array3);
        tb = System.currentTimeMillis();

        System.out.println("Tree sort in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array3));

        //// Natural merge sort ////

        ta = System.currentTimeMillis();
        new NaturalMergeSort<Integer>().sort(array4);
        tb = System.currentTimeMillis();

        System.out.println("Natural merge sort in " + (tb - ta)
                + " ms. Sorted: "
                + isSorted(array4));

        //// Batcher's method ////

        ta = System.currentTimeMillis();
        new BatchersSort<Integer>().sort(array5);
        tb = System.currentTimeMillis();

        System.out.println("Batcher's method in " + (tb - ta)
                + " ms. Sorted: "
                + isSorted(array5));

        //// Iterative mergesort ////

        ta = System.currentTimeMillis();
        new IterativeMergesort<Integer>().sort(array6);
        tb = System.currentTimeMillis();

        System.out.println("Iterative mergesort in " + (tb - ta)
                + " ms. Sorted: "
                + isSorted(array6));

        //// Arrays.sort ////

        ta = System.currentTimeMillis();
        Arrays.sort(array10);
        tb = System.currentTimeMillis();

        System.out.println("Arrays.sort() in " + (tb - ta)
                + " ms. Sorted: "
                + isSorted(array10));

        line();

        System.out.println("All arrays are equal: "
                + allWeakEquals(array1, array2, array3, array5, array6, array10));

        array1 = getRandomIntegerArray(SIZE, r);
        array2 = array1.clone();
        array3 = array1.clone();
        array4 = array1.clone();
        array5 = array1.clone();
        array6 = array1.clone();
        array10 = array1.clone();

        System.gc();

        title2("As random as possible.");

        //// Comb sort ////

        ta = System.currentTimeMillis();
        new CombSort<Integer>().sort(array1);
        tb = System.currentTimeMillis();

        System.out.println("Comb sort in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array1));

        //// Counting sort ////

        ta = System.currentTimeMillis();
        new CountingSort<Integer>().sort(array2);
        tb = System.currentTimeMillis();

        System.out.println("Counting sort in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array2));

        //// Tree sort ////

        ta = System.currentTimeMillis();
        new TreeSort<Integer>().sort(array3);
        tb = System.currentTimeMillis();

        System.out.println("Tree sort in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array3));

        //// Natural mergesort ////

        ta = System.currentTimeMillis();
        new NaturalMergeSort<Integer>().sort(array4);
        tb = System.currentTimeMillis();

        System.out.println("Natural merge sort in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array4));

        //// Batcher's method ////

        ta = System.currentTimeMillis();
        new BatchersSort<Integer>().sort(array5);
        tb = System.currentTimeMillis();

        System.out.println("Batcher's method in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array5));

        //// Iterative merge sort ////

        ta = System.currentTimeMillis();
        new IterativeMergesort<Integer>().sort(array6);
        tb = System.currentTimeMillis();

        System.out.println("Iterative merge sort in " + (tb - ta)
                + " ms. Sorted: "
                + isSorted(array6));

        //// Arrays.sort ////

        ta = System.currentTimeMillis();
        Arrays.sort(array10);
        tb = System.currentTimeMillis();

        System.out.println("Arrays.sort() in " + (tb - ta) + " ms. Sorted: "
                + isSorted(array10));

        line();

        System.out.println("All arrays are equal: "
                + allWeakEquals(array1, array2, array3, array5, array6, array10));
    }

    private static void debugNaturalMergeSort() {
        final long SEED = System.currentTimeMillis();
        Integer[] array = getRandomIntegerArray(20, 0, 100, new Random(SEED));
        debugPrintArray(array);
        new NaturalMergeSort<Integer>().sort(array);
        debugPrintArray(array);
    }
}
