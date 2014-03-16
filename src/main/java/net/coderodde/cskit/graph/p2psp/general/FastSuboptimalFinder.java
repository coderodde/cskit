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
 * This class implements a bidirectional <tt>A*</tt>-search algorithm due to
 * Taeg-Keun Whangbo (Efficient Modified Bidirectional A∗ Algorithm for Optimal
 * Route-Finding.)
 *
 * @author Rodion Efremov
 * @version 1.618 (16.12.2013)
 */
public class FastSuboptimalFinder extends GeneralPathFinder {

    private HeuristicFunction h;
    private HeuristicFunction h2;
    private PriorityQueue<DirectedGraphNode, Double> OPEN2;
    private Set<DirectedGraphNode> CLOSED2;
    private Map<DirectedGraphNode, Double> GSCORE_MAP2;
    private Map<DirectedGraphNode, DirectedGraphNode> PARENT_MAP2;

    public FastSuboptimalFinder(PriorityQueue<DirectedGraphNode, Double> OPEN,
                         HeuristicFunction h,
                         HeuristicFunction h2) {
        super(OPEN);
        this.h = h;
        this.h2 = h2;

        OPEN2 = OPEN.newInstance();
        CLOSED2 = new HashSet<DirectedGraphNode>();
        GSCORE_MAP2 = new HashMap<DirectedGraphNode, Double>();
        PARENT_MAP2 = new HashMap<DirectedGraphNode, DirectedGraphNode>();
    }

    @Override
    public List<DirectedGraphNode> find(DirectedGraphNode source,
                                        DirectedGraphNode target,
                                        DirectedGraphWeightFunction w) {
        h.setTarget(target);
        OPEN.clear();
        CLOSED.clear();
        GSCORE_MAP.clear();
        PARENT_MAP.clear();

        h2.setTarget(source);
        OPEN2.clear();
        CLOSED2.clear();
        GSCORE_MAP2.clear();
        PARENT_MAP2.clear();

        OPEN.insert(source, h.get(source));
        PARENT_MAP.put(source, null);
        GSCORE_MAP.put(source, 0.0);

        OPEN2.insert(target, h2.get(target));
        PARENT_MAP2.put(target, null);
        GSCORE_MAP2.put(target, 0.0);

        double m = Double.POSITIVE_INFINITY;
        DirectedGraphNode touch = null;

        while ((OPEN.isEmpty() == false) && (OPEN2.isEmpty() == false)) {

            if (touch != null) {
//                if (CLOSED2.contains(OPEN.min())
//                        && GSCORE_MAP.get(OPEN.min()) +
//                           GSCORE_MAP2.get(OPEN.min()) > m) {
//                    return tracebackPathBidirectional(touch,
//                                                      PARENT_MAP,
//                                                      PARENT_MAP2);
//                }
//
//                if (CLOSED.contains(OPEN2.min())
//                        && GSCORE_MAP.get(OPEN2.min()) +
//                           GSCORE_MAP.get(OPEN2.min()) > m) {
//                    return tracebackPathBidirectional(touch,
//                                                      PARENT_MAP,
//                                                      PARENT_MAP2);
//                }
                double L1 = GSCORE_MAP.get(touch);
                double L2 = GSCORE_MAP2.get(touch);

                if (m >= L1 + L2) {
                    return tracebackPathBidirectional(touch,
                                                      PARENT_MAP,
                                                      PARENT_MAP2);
                }
            }

            DirectedGraphNode current = OPEN.extractMinimum();
            CLOSED.add(current);

            for (DirectedGraphNode child : current) {
                if (CLOSED.contains(child)) {
                    continue;
                }

                double tmpg = GSCORE_MAP.get(current) + w.get(current, child);

                if (GSCORE_MAP.containsKey(child) == false) {
                    OPEN.insert(child, tmpg);
                    GSCORE_MAP.put(child, tmpg);
                    PARENT_MAP.put(child, current);

                    if (CLOSED2.contains(child)) {
                        if (m > tmpg + GSCORE_MAP2.get(child)) {
                            m = tmpg + GSCORE_MAP2.get(child);
                            touch = child;
                        }
                    }
                } else if (tmpg < GSCORE_MAP.get(child)) {
                    OPEN.decreasePriority(child, tmpg);
                    GSCORE_MAP.put(child, tmpg);
                    PARENT_MAP.put(child, current);

                    if (CLOSED2.contains(child)) {
                        if (m > tmpg + GSCORE_MAP2.get(child)) {
                            m = tmpg + GSCORE_MAP2.get(child);
                            touch = child;
                        }
                    }
                }
            }

            current = OPEN2.extractMinimum();
            CLOSED2.add(current);

            for (DirectedGraphNode parent : current.parentIterable()) {
                if (CLOSED2.contains(parent)) {
                    continue;
                }

                double tmpg = GSCORE_MAP2.get(current) + w.get(parent, current);

                if (GSCORE_MAP2.containsKey(parent) == false) {
                    OPEN2.insert(parent, tmpg);
                    GSCORE_MAP2.put(parent, tmpg);
                    PARENT_MAP2.put(parent, current);

                    if (CLOSED.contains(parent)) {
                        if (m > tmpg + GSCORE_MAP.get(parent)) {
                            m = tmpg + GSCORE_MAP.get(parent);
                            touch = parent;
                        }
                    }
                } else if (tmpg < GSCORE_MAP2.get(parent)) {
                    OPEN2.decreasePriority(parent, tmpg);
                    GSCORE_MAP2.put(parent, tmpg);
                    PARENT_MAP2.put(parent, current);

                    if (CLOSED.contains(parent)) {
                        if (m > tmpg + GSCORE_MAP.get(parent)) {
                            m = tmpg + GSCORE_MAP.get(parent);
                            touch = parent;
                        }
                    }
                }
            }
        }

        if (touch != null) {
            return tracebackPathBidirectional(touch,
                                              PARENT_MAP,
                                              PARENT_MAP2);
        }

        return java.util.Collections.<DirectedGraphNode>emptyList();
    }
}
