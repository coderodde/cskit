package net.coderodde.cskit.graph.p2psp.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static net.coderodde.cskit.Utilities.tracebackPathBidirectional;
import net.coderodde.cskit.ds.pq.PriorityQueue;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.DirectedGraphWeightFunction;

/**
 * This class implements bidirectional Dijkstra's algorithm.
 *
 * @author Rodion Efremov
 * @version 1.618 (18.12.2013)
 */
public class BidirectionalDijkstraFinder extends GeneralPathFinder {

    private PriorityQueue<DirectedGraphNode, Double> OPENB;
    private Set<DirectedGraphNode> CLOSEDB;
    private Map<DirectedGraphNode, Double> GSCOREB;
    private Map<DirectedGraphNode, DirectedGraphNode> PARENTB;

    public BidirectionalDijkstraFinder(
            PriorityQueue<DirectedGraphNode, Double> OPEN) {
        super(OPEN);
        OPEN.clear();
        // Use the same heap structure.
        OPENB = OPEN.newInstance();
        CLOSEDB = new HashSet<DirectedGraphNode>();
        GSCOREB = new HashMap<DirectedGraphNode, Double>();
        PARENTB = new HashMap<DirectedGraphNode, DirectedGraphNode>();
    }

    @Override
    public List<DirectedGraphNode> find(DirectedGraphNode source,
                                        DirectedGraphNode target,
                                        DirectedGraphWeightFunction w) {

        PriorityQueue<DirectedGraphNode, Double> OPENA    = OPEN;
        Set<DirectedGraphNode> CLOSEDA                    = CLOSED;
        Map<DirectedGraphNode, Double> GSCOREA            = GSCORE_MAP;
        Map<DirectedGraphNode, DirectedGraphNode> PARENTA = PARENT_MAP;

        OPENA.clear();
        OPENB.clear();
        CLOSEDA.clear();
        CLOSEDB.clear();
        GSCOREA.clear();
        GSCOREB.clear();
        PARENTA.clear();
        PARENTB.clear();

        OPENA.insert(source, 0.0);
        OPENB.insert(target, 0.0);

        GSCOREA.put(source, 0.0);
        GSCOREB.put(target, 0.0);

        PARENTA.put(source, null);
        PARENTB.put(target, null);

        DirectedGraphNode touch = null;
        double m = Double.POSITIVE_INFINITY;

        while ((OPENA.isEmpty() == false) && (OPENB.isEmpty() == false)) {

            if (m < GSCOREA.get(OPENA.min()) + GSCOREB.get(OPENB.min())) {
                return tracebackPathBidirectional(touch, PARENTA, PARENTB);
            }

            DirectedGraphNode current = OPENA.extractMinimum();
            CLOSEDA.add(current);

            for (DirectedGraphNode child : current) {
                if (CLOSEDA.contains(child)) {
                    continue;
                }

                double tmpg = GSCOREA.get(current) + w.get(current, child);

                if (GSCOREA.containsKey(child) == false) {
                    OPENA.insert(child, tmpg);
                    GSCOREA.put(child, tmpg);
                    PARENTA.put(child, current);

                    if (CLOSEDB.contains(child)) {
                        if (m > tmpg + GSCOREB.get(child)) {
                            m = tmpg + GSCOREB.get(child);
                            touch = child;
                        }
                    }
                } else if (tmpg < GSCOREA.get(child)) {
                    OPENA.decreasePriority(child, tmpg);
                    GSCOREA.put(child, tmpg);
                    PARENTA.put(child, current);

                    if (CLOSEDB.contains(child)) {
                        if (m > tmpg + GSCOREB.get(child)) {
                            m = tmpg + GSCOREB.get(child);
                            touch = child;
                        }
                    }
                }
            }

            current = OPENB.extractMinimum();
            CLOSEDB.add(current);

            for (DirectedGraphNode parent : current.parentIterable()) {
                if (CLOSEDB.contains(parent)) {
                    continue;
                }

                double tmpg = GSCOREB.get(current) + w.get(parent, current);

                if (GSCOREB.containsKey(parent) == false) {
                    OPENB.insert(parent, tmpg);
                    GSCOREB.put(parent, tmpg);
                    PARENTB.put(parent, current);

                    if (CLOSEDA.contains(parent)) {
                        if (m > tmpg + GSCOREA.get(parent)) {
                            m = tmpg + GSCOREA.get(parent);
                            touch = parent;
                        }
                    }
                } else if (tmpg < GSCOREB.get(parent)) {
                    OPENB.decreasePriority(parent, tmpg);
                    GSCOREB.put(parent, tmpg);
                    PARENTB.put(parent, current);

                    if (CLOSEDA.contains(parent)) {
                        if (m > tmpg + GSCOREA.get(parent)) {
                            m = tmpg + GSCOREA.get(parent);
                            touch = parent;
                        }
                    }
                }
            }
        }

        if (touch == null) {
            return java.util.Collections.<DirectedGraphNode>emptyList();
        }

        return tracebackPathBidirectional(touch, PARENTA, PARENTB);
    }
}
