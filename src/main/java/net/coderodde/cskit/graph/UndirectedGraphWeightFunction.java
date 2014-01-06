package net.coderodde.cskit.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * This class models the weight function suitable for the weight function
 * of undirected graphs, namely if user called only <code>c.put(a, b, w);</code>,
 * both <code>c.get(a, b)</code> and <code>c.get(b, a)</code> will returned
 * <code>w</code>.
 *
 * @author Rodion Efremov
 */
public class UndirectedGraphWeightFunction {
    private Map<UndirectedGraphNode, Map<UndirectedGraphNode, Double>> map =
    new HashMap<UndirectedGraphNode, Map<UndirectedGraphNode, Double>>();

    public void put(UndirectedGraphNode a,
                    UndirectedGraphNode b,
                    double weight) {
        if (map.get(a) == null) {
            map.put(a, new HashMap<UndirectedGraphNode, Double>());
        }

        map.get(a).put(b, weight);

        if (map.get(b) == null) {
            map.put(b, new HashMap<UndirectedGraphNode, Double>());
        }

        map.get(b).put(a, weight);
    }

    public double get(UndirectedGraphNode a, UndirectedGraphNode b) {
        if (map.get(a) == null || map.get(a).get(b) == null) {
            return 0.0;
        }

        return map.get(a).get(b);
    }

    public void clear() {
        map.clear();
    }
}
