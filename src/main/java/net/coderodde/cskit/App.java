package net.coderodde.cskit;

import java.util.List;
import java.util.Random;
import static net.coderodde.cskit.Utilities.generateSimpleGraph;
import static net.coderodde.cskit.Utilities.isConnectedPath;
import static net.coderodde.cskit.Utilities.line;
import static net.coderodde.cskit.Utilities.pathsAreSame;
import static net.coderodde.cskit.Utilities.title;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.p2psp.BidirectionalBreadthFirstSearchFinder;
import net.coderodde.cskit.p2psp.BreadthFirstSearchFinder;
import net.coderodde.cskit.p2psp.UniformCostPathFinder;
/**
 * Hello from cskit.
 *
 */
public class App{

    public static void main(String... args) {
        profileBreadthFirstSearchAlgorithms();
    }

    public static void profileBreadthFirstSearchAlgorithms() {
        title("Uniform cost graph search");
        final long SEED = 1386506122786L; //System.currentTimeMillis();
        final Random r = new Random(SEED);
        final int SIZE = 10000;
        final float LOAD_FACTOR = 5.5f / SIZE;
        System.out.println("Seed: " + SEED);

        List<DirectedGraphNode> graph =
                generateSimpleGraph(SIZE, LOAD_FACTOR, r);

        DirectedGraphNode source = graph.get(r.nextInt(SIZE));
        DirectedGraphNode target = graph.get(r.nextInt(SIZE));

        UniformCostPathFinder finder1 =
                new BreadthFirstSearchFinder();

        UniformCostPathFinder finder2 =
                new BidirectionalBreadthFirstSearchFinder();

        long ta = System.currentTimeMillis();
        List<DirectedGraphNode> path1 = finder1.find(source, target);
        long tb = System.currentTimeMillis();

        System.out.println("BreadthFirstSearchFinder in " + (tb - ta) + " ms.");

        ta = System.currentTimeMillis();
        List<DirectedGraphNode> path2 = finder2.find(source, target);
        tb = System.currentTimeMillis();

        System.out.println("BidirectionalBreadthFirstSearchFinder in "
                + (tb - ta) + " ms.");

        line();

        boolean eq = path1.size() == path2.size();

        if (eq == true) {
            System.out.println("Paths are of same length: " + eq
                    + ", length: " + path1.size());
        } else {
            System.out.println("Erroneous paths! Lengths: "
                    + path1.size() + " and " + path2.size());
        }

        boolean ok1 = (isConnectedPath(path1)
                && path1.get(0).equals(source)
                && path1.get(path1.size() - 1).equals(target));

        boolean ok2 = (isConnectedPath(path1)
                && path2.get(0).equals(source)
                && path2.get(path2.size() - 1).equals(target));

        System.out.println("Breadth-first search path o.k.: " + ok1);
        System.out.println("Bidirectional breadth-first search path o.k.: " + ok2);

        line();
    }
}
