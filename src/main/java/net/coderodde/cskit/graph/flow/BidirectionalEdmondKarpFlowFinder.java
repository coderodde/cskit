package net.coderodde.cskit.graph.flow;

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
import net.coderodde.cskit.Utilities.Pair;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.DoubleWeightFunction;

/**
 * This class implements the Edmond-Karp maximum-flow algorithm.
 *
 * @author Rodion Efremov
 * @version 1.61803 (24.12.2013)
 */
public class BidirectionalEdmondKarpFlowFinder extends FlowFinder {

    private final Map<DirectedGraphNode, DirectedGraphNode> parentMapA =
          new HashMap<DirectedGraphNode, DirectedGraphNode>();

    private final Map<DirectedGraphNode, DirectedGraphNode> parentMapB =
          new HashMap<DirectedGraphNode, DirectedGraphNode>();

    private final Map<DirectedGraphNode, Integer> distanceMapA =
          new HashMap<DirectedGraphNode, Integer>();

    private final Map<DirectedGraphNode, Integer> distanceMapB =
          new HashMap<DirectedGraphNode, Integer>();

    private final Set<DirectedGraphNode> levelA =
          new HashSet<DirectedGraphNode>();

    private final Set<DirectedGraphNode> levelB =
          new HashSet<DirectedGraphNode>();

    @Override
    public Pair<DoubleWeightFunction, Double> find(DirectedGraphNode source,
                                                   DirectedGraphNode sink,
                                                   DoubleWeightFunction c) {
        double flow = 0.0;
        DoubleWeightFunction f = new DoubleWeightFunction(); // The flow map.
        List<DirectedGraphNode> path = null;

        while ((path = findAugmentingPath(source, sink, c, f)).size() > 1) {
            double df = findMinimumEdgeAndRemove(path, c, f);
            flow += df;
        }

        return new Pair<DoubleWeightFunction, Double>(f, flow);
    }

    /**
     * This method is essentially bidirectional breadth-first search over the
     * residual graph.
     *
     * @param source the source node.
     * @param sink the sink node.
     * @param c the capacity map.
     * @param f the flow map.
     *
     * @return an augmenting path.
     */
    private List<DirectedGraphNode> findAugmentingPath(DirectedGraphNode source,
                                                       DirectedGraphNode sink,
                                                       DoubleWeightFunction c,
                                                       DoubleWeightFunction f) {
        Deque<DirectedGraphNode> queueA = new LinkedList<DirectedGraphNode>();
        Deque<DirectedGraphNode> queueB = new LinkedList<DirectedGraphNode>();

        DirectedGraphNode lastA = source;
        DirectedGraphNode lastB = sink;

        levelA.clear();
        levelB.clear();
        parentMapA.clear();
        parentMapB.clear();
        distanceMapA.clear();
        distanceMapB.clear();

        queueA.add(source);
        levelA.add(source);
        parentMapA.put(source, null);
        distanceMapA.put(source, 0);

        queueB.add(sink);
        levelB.add(sink);
        parentMapB.put(sink, null);
        distanceMapB.put(sink, 0);

        while (queueA.isEmpty() == false && queueB.isEmpty() == false) {
            DirectedGraphNode current = queueA.getFirst();

            for (DirectedGraphNode u : current.allIterable()) {
                if (parentMapA.containsKey(u)) {
                    continue;
                }

                if (residualEdgeWeight(current, u, f, c) > 0.0) {
                    queueA.addLast(u);
                    levelA.add(u);
                    parentMapA.put(u, current);
                    distanceMapA.put(u, distanceMapA.get(current) + 1);
                }
            }

            if (lastA.equals(current)) {
                if (Collections.disjoint(levelA,
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

            levelA.remove(current);
            queueA.removeFirst();

            // Expand the backwards search.
            current = queueB.getFirst();

            for (DirectedGraphNode u : current.allIterable()) {
                if (parentMapB.containsKey(u)) {
                    continue;
                }

                if (residualEdgeWeight(u, current, f, c) > 0.0) {
                    queueB.addLast(u);
                    levelB.add(u);
                    parentMapB.put(u, current);
                    distanceMapB.put(u, distanceMapB.get(current) + 1);
                }
            }

            if (lastB.equals(current)) {
                if (Collections.disjoint(levelB,
                                         parentMapA.keySet()) == false) {
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

            levelB.remove(current);
            queueB.removeFirst();
        }

        return java.util.Collections.<DirectedGraphNode>emptyList();
    }

    private double findMinimumEdgeAndRemove(List<DirectedGraphNode> path,
                                            DoubleWeightFunction c,
                                            DoubleWeightFunction f) {
        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < path.size() - 1; ++i) {
            if (min > residualEdgeWeight(path.get(i), path.get(i + 1), f, c)) {
                min = residualEdgeWeight(path.get(i), path.get(i + 1), f, c);
            }
        }

        for (int i = 0; i < path.size() - 1; ++i) {
            DirectedGraphNode from = path.get(i);
            DirectedGraphNode to = path.get(i + 1);
            f.put(from, to, f.get(from, to) + min);
        }

        return min;
    }
}
