package net.coderodde.cskit.graph;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public class WeightFunction {

    private Map<DirectedGraphNode, Map<DirectedGraphNode, Double>> map =
    new HashMap<DirectedGraphNode, Map<DirectedGraphNode, Double>>();

    public void put(DirectedGraphNode from,
                    DirectedGraphNode to,
                    double weight) {
        if (map.get(from) == null) {
            map.put(from, new HashMap<DirectedGraphNode, Double>());
        }

        map.get(from).put(to, weight);
    }

    public double get(DirectedGraphNode from, DirectedGraphNode to) {
        if (map.get(from) == null || map.get(from).get(to) == null) {
            return 0.0; // For the sake of residual graphs of max-flow problem.
        }

        return map.get(from).get(to);
    }
}
