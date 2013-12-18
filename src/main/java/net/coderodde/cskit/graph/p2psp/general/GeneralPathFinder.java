package net.coderodde.cskit.graph.p2psp.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.coderodde.cskit.ds.pq.PriorityQueue;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.DoubleWeightFunction;

/**
 * This interface defines the common API for general shortest path algorithms.
 *
 * @author Rodion Efremov
 * @version 1.618
 */
public abstract class GeneralPathFinder {

    protected PriorityQueue<DirectedGraphNode> OPEN;
    protected Set<DirectedGraphNode> CLOSED;
    protected Map<DirectedGraphNode, Double> GSCORE_MAP;
    protected Map<DirectedGraphNode, DirectedGraphNode> PARENT_MAP;

    public GeneralPathFinder(PriorityQueue OPEN) {
        this.OPEN = OPEN;
        this.CLOSED = new HashSet<DirectedGraphNode>();
        this.GSCORE_MAP = new HashMap<DirectedGraphNode, Double>();
        this.PARENT_MAP = new HashMap<DirectedGraphNode, DirectedGraphNode>();
    }

    public abstract List<DirectedGraphNode> find(DirectedGraphNode source,
                                                 DirectedGraphNode target,
                                                 DoubleWeightFunction w);

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

}
