package net.coderodde.cskit.graph.p2psp.general;

import java.util.List;
import net.coderodde.cskit.ds.pq.PriorityQueue;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.DoubleWeightFunction;

/**
 * This class implements the classical Dijkstra's algorithm for finding point-
 * 2-point shortest paths.
 *
 * @author Rodion Efremov
 * @version 1.618
 */
public class DijkstraFinder extends GeneralPathFinder {

    public DijkstraFinder(PriorityQueue<DirectedGraphNode> OPEN) {
        super(OPEN);
    }

    @Override
    public List<DirectedGraphNode> find(DirectedGraphNode source,
                                        DirectedGraphNode target,
                                        DoubleWeightFunction w) {
        OPEN.clear();
        CLOSED.clear();
        GSCORE_MAP.clear();
        PARENT_MAP.clear();

        OPEN.insert(source, 0.0);
        PARENT_MAP.put(source, null);
        GSCORE_MAP.put(source, 0.0);

        while (OPEN.isEmpty() == false) {
            DirectedGraphNode current = OPEN.extractMinimum();

            if (current.equals(target)) {
                return tracebackPath(current, PARENT_MAP);
            }

            CLOSED.add(current);

            for (DirectedGraphNode child : current) {
                if (CLOSED.contains(child)) {
                    continue;
                }

                double tmp = GSCORE_MAP.get(current) + w.get(current, child);

                if (PARENT_MAP.containsKey(child) == false) {
                    OPEN.insert(child, tmp);
                    PARENT_MAP.put(child, current);
                    GSCORE_MAP.put(child, tmp);
                } else if (tmp < GSCORE_MAP.get(child)) {
                    OPEN.decreasePriority(child, tmp);
                    PARENT_MAP.put(child, current);
                    GSCORE_MAP.put(child, tmp);
                }
            }
        }

        return java.util.Collections.<DirectedGraphNode>emptyList();
    }

}
