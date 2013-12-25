package net.coderodde.cskit.graph.p2psp.general;

import java.util.List;
import static net.coderodde.cskit.Utilities.tracebackPath;
import net.coderodde.cskit.ds.pq.PriorityQueue;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.WeightFunction;

/**
 * This class implements <tt>A*</tt>-search algorithm.
 *
 * @author Rodion Efremov
 * @version 1.618 (16.12.2013)
 */
public class AStarFinder extends GeneralPathFinder {

    private HeuristicFunction h;

    public AStarFinder(PriorityQueue<DirectedGraphNode, Double> OPEN,
                       HeuristicFunction h) {
        super(OPEN);
        this.h = h;
    }

    @Override
    public List<DirectedGraphNode> find(DirectedGraphNode source,
                                        DirectedGraphNode target,
                                        WeightFunction w) {
        h.setTarget(target);
        OPEN.clear();
        CLOSED.clear();
        GSCORE_MAP.clear();
        PARENT_MAP.clear();

        OPEN.insert(source, h.get(source));
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

                double tmpg = GSCORE_MAP.get(current) + w.get(current, child);

                if (GSCORE_MAP.containsKey(child) == false) {
                    OPEN.insert(child, tmpg);
                    OPEN.insert(child, tmpg + h.get(child));
                    GSCORE_MAP.put(child, tmpg);
                    PARENT_MAP.put(child, current);
                } else if (tmpg < GSCORE_MAP.get(child)) {
                    OPEN.decreasePriority(child, tmpg);
                    GSCORE_MAP.put(child, tmpg);
                    PARENT_MAP.put(child, current);
                }
            }
        }

        return java.util.Collections.<DirectedGraphNode>emptyList();
    }
}
