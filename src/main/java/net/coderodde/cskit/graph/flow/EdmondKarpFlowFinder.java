package net.coderodde.cskit.graph.flow;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.coderodde.cskit.Utilities;
import net.coderodde.cskit.Utilities.Pair;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.WeightFunction;

/**
 * This class implements the Edmond-Karp maximum-flow algorithm.
 *
 * @author Rodion Efremov
 * @version 1.61803 (23.12.2013)
 */
public class EdmondKarpFlowFinder extends FlowFinder {

    @Override
    public Pair<WeightFunction, Double> find(DirectedGraphNode source,
                                             DirectedGraphNode sink,
                                             WeightFunction c) {
        double flow = 0.0;
        WeightFunction f = new WeightFunction(); // The flow map.
        List<DirectedGraphNode> path = null;

        while ((path = findAugmentingPath(source, sink, c, f)).size() > 1) {
            double df = findMinimumEdgeAndRemove(path, c, f);
            flow += df;
        }

        return new Pair<WeightFunction, Double>(f, flow);
    }

    /**
     * This method is essentially breadth-first search over the residual graph.
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
                                                       WeightFunction c,
                                                       WeightFunction f) {
        Deque<DirectedGraphNode> queue = new LinkedList<DirectedGraphNode>();
        Map<DirectedGraphNode, DirectedGraphNode> parentMap =
                   new HashMap<DirectedGraphNode, DirectedGraphNode>();

        queue.add(source);
        parentMap.put(source, null);

        while (queue.isEmpty() == false) {
            DirectedGraphNode current = queue.removeFirst();

            if (current.equals(sink)) {
                return Utilities.tracebackPath(current, parentMap);
            }

            for (DirectedGraphNode u : current.allIterable()) {
                if (parentMap.containsKey(u)) {
                    continue;
                }

                if (residualEdgeWeight(current, u, f, c) > 0.0) {
                    parentMap.put(u, current);
                    queue.addLast(u);
                }
            }
        }

        return java.util.Collections.<DirectedGraphNode>emptyList();
    }

    private double findMinimumEdgeAndRemove(List<DirectedGraphNode> path,
                                            WeightFunction c,
                                            WeightFunction f) {
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
