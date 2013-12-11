package net.coderodde.cskit.graph.p2psp.uniform;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static net.coderodde.cskit.Utilities.findTouchNode;
import static net.coderodde.cskit.Utilities.tracebackPathBidirectional;
import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * This class implements bidirectional breadth-first search algorithm.
 *
 * @author Rodion Efremov
 * @version 1.6
 */
public class BidirectionalBreadthFirstSearchFinder implements UniformCostPathFinder {

    // We choose to reuse the data structures. This resembles losing virginity:
    // it all expands and becomes loose enough. :)
    Map<DirectedGraphNode, DirectedGraphNode> parentMapA =
            new HashMap<DirectedGraphNode, DirectedGraphNode>();

    Map<DirectedGraphNode, DirectedGraphNode> parentMapB =
            new HashMap<DirectedGraphNode, DirectedGraphNode>();

    Map<DirectedGraphNode, Integer> distanceMapA =
            new HashMap<DirectedGraphNode, Integer>();

    Map<DirectedGraphNode, Integer> distanceMapB =
            new HashMap<DirectedGraphNode, Integer>();

    Set<DirectedGraphNode> levelA = new HashSet<DirectedGraphNode>();
    Set<DirectedGraphNode> levelB = new HashSet<DirectedGraphNode>();

    public List<DirectedGraphNode>
            find(DirectedGraphNode source, DirectedGraphNode target) {
        clear();

        parentMapA.put(source, null);
        parentMapB.put(target, null);

        distanceMapA.put(source, 0);
        distanceMapB.put(target, 0);

        levelA.add(source);
        levelB.add(target);

        DirectedGraphNode lastA = source;
        DirectedGraphNode lastB = target;

        Deque<DirectedGraphNode> queueA = new LinkedList<DirectedGraphNode>();
        Deque<DirectedGraphNode> queueB = new LinkedList<DirectedGraphNode>();

        queueA.addLast(source);
        queueB.addLast(target);

        while (queueA.isEmpty() == false && queueB.isEmpty() == false) {
            DirectedGraphNode A = queueA.getFirst();

            for (DirectedGraphNode child : A) {
                if (parentMapA.containsKey(child) == false) {
                    parentMapA.put(child, A);
                    queueA.addLast(child);
                    levelA.add(child);
                    distanceMapA.put(child, distanceMapA.get(A) + 1);
                }
            }

            // Level of forward search frontier complete?
            if (lastA.equals(A)) {
                if (Collections.disjoint(
                        levelA,
                        parentMapB.keySet()) == false) {
                    DirectedGraphNode touchNode =
                            findTouchNode(levelA,
                                          levelB,
                                          parentMapA,
                                          parentMapB,
                                          distanceMapA,
                                          distanceMapB);
                    return tracebackPathBidirectional(touchNode,
                                                      parentMapA,
                                                      parentMapB);
                }

                lastA = queueA.getLast();
            }

            levelA.remove(A);
            queueA.removeFirst();

            // Expand the backwards search.
            DirectedGraphNode B = queueB.getFirst();

            for (DirectedGraphNode parent : B.parentIterable()) {
                if (parentMapB.containsKey(parent) == false) {
                    parentMapB.put(parent, B);
                    queueB.addLast(parent);
                    levelB.add(parent);
                    distanceMapB.put(parent, distanceMapB.get(B) + 1);
                }
            }

            if (lastB.equals(B)) {
                if (Collections.disjoint(parentMapA.keySet(),
                                         levelB) == false) {
                    DirectedGraphNode touchNode =
                            findTouchNode(levelA,
                                          levelB,
                                          parentMapA,
                                          parentMapB,
                                          distanceMapA,
                                          distanceMapB);
                    return tracebackPathBidirectional(touchNode,
                                                      parentMapA,
                                                      parentMapB);
                }

                lastB = queueB.getLast();
            }

            queueB.removeFirst();
            levelB.remove(B);
        }

        return Collections.<DirectedGraphNode>emptyList();
    }

    private void clear() {
        parentMapA.clear();
        parentMapB.clear();
        distanceMapA.clear();
        distanceMapB.clear();
        levelA.clear();
        levelB.clear();
    }
}
