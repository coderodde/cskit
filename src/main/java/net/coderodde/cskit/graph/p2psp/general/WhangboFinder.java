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
public class WhangboFinder extends GeneralPathFinder {

    private HeuristicFunction h;
    private HeuristicFunction h2;
    private PriorityQueue<DirectedGraphNode, Double> OPEN2;
    private Set<DirectedGraphNode> CLOSED2;
    private Map<DirectedGraphNode, Double> GSCORE_MAP2;
    private Map<DirectedGraphNode, DirectedGraphNode> PARENT_MAP2;

    // Whangbo specific structures.
    private DirectedGraphNode touch;
    private DirectedGraphNode separator;
    private PriorityQueue<DirectedGraphNode, Double> OPENL1;
    private PriorityQueue<DirectedGraphNode, Double> OPENL2;

    public WhangboFinder(PriorityQueue<DirectedGraphNode, Double> OPEN,
                         HeuristicFunction h,
                         HeuristicFunction h2) {
        super(OPEN);
        this.h = h;
        this.h2 = h2;

        OPEN2 = OPEN.newInstance();
        CLOSED2 = new HashSet<DirectedGraphNode>();
        GSCORE_MAP2 = new HashMap<DirectedGraphNode, Double>();
        PARENT_MAP2 = new HashMap<DirectedGraphNode, DirectedGraphNode>();

        // Whangbo specific structures.
        OPENL1 = OPEN.newInstance();
        OPENL2 = OPEN.newInstance();
    }

    @Override
    public List<DirectedGraphNode> find(DirectedGraphNode source,
                                        DirectedGraphNode target,
                                        DirectedGraphWeightFunction w) {
        super.source = source;
        super.target = target;

        touch = null;
        separator = null;

        h.setTarget(target);
        OPEN.clear();
        CLOSED.clear();
        GSCORE_MAP.clear();
        PARENT_MAP.clear();
        OPENL1.clear();

        h2.setTarget(source);
        OPEN2.clear();
        CLOSED2.clear();
        GSCORE_MAP2.clear();
        PARENT_MAP2.clear();
        OPENL2.clear();

        OPEN.insert(source, h.get(source));
        PARENT_MAP.put(source, null);
        GSCORE_MAP.put(source, 0.0);

        OPEN2.insert(target, h2.get(target));
        PARENT_MAP2.put(target, null);
        GSCORE_MAP2.put(target, 0.0);

        double m = Double.POSITIVE_INFINITY;

        while ((OPEN.isEmpty() == false) && (OPEN2.isEmpty() == false)) {

            if (touch != null) {
                double L1 = GSCORE_MAP.get(touch);
                double L2 = GSCORE_MAP2.get(touch);

                if (L1 <= OPENL1.getPriority(OPENL1.min())
                        && L2 <= OPENL2.getPriority(OPENL2.min())) {
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

                            if (touch == null) {
                                separator = child;
                            }

                            touch = child;
                            OPENL1.insert(child, tmpg + l1(child));
                            OPENL2.insert(child, GSCORE_MAP2.get(child) + l2(child));
                        }
                    }
                } else if (tmpg < GSCORE_MAP.get(child)) {
                    OPEN.decreasePriority(child, tmpg);
                    GSCORE_MAP.put(child, tmpg);
                    PARENT_MAP.put(child, current);

                    if (CLOSED2.contains(child)) {
                        if (m > tmpg + GSCORE_MAP2.get(child)) {
                            m = tmpg + GSCORE_MAP2.get(child);

                            if (touch == null) {
                                separator = child;
                            }

                            touch = child;
                            OPENL1.decreasePriority(child, tmpg + l1(child));
                            OPENL2.decreasePriority(child, GSCORE_MAP2.get(child) + l2(child));
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

                            if (touch == null) {
                                separator = parent;
                            }

                            touch = parent;
                            OPENL1.insert(parent, GSCORE_MAP.get(parent) + l1(parent));
                            OPENL2.insert(parent, tmpg + l2(parent));
                        }
                    }
                } else if (tmpg < GSCORE_MAP2.get(parent)) {
                    OPEN2.decreasePriority(parent, tmpg);
                    GSCORE_MAP2.put(parent, tmpg);
                    PARENT_MAP2.put(parent, current);

                    if (CLOSED.contains(parent)) {
                        if (m > tmpg + GSCORE_MAP.get(parent)) {
                            m = tmpg + GSCORE_MAP.get(parent);

                            if (touch == null) {
                                separator = parent;
                            }

                            touch = parent;
                            OPENL1.decreasePriority(parent, GSCORE_MAP.get(parent) + l1(parent));
                            OPENL2.decreasePriority(parent, tmpg + l2(parent));
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

    private double l1(DirectedGraphNode node) {
        CoordinateMap cm = h.getCoordinateMap();
        double[] s = cm.get(source);
        double[] p = cm.get(separator);
        double[] x = cm.get(node);

        for (int i = 0; i < x.length; ++i) {
            x[i] -= p[i];
        }

        for (int i = 0; i < s.length; ++i) {
            s[i] -= p[i];
        }

        double dotProduct = 0.0;

        for (int i = 0; i < s.length; ++i) {
            dotProduct += x[i] * s[i];
        }

        double len = 0.0;

        for (int i = 0; i < s.length; ++i) {
            len += s[i] * s[i];
        }

        return dotProduct / Math.sqrt(len);
    }

    private double l2(DirectedGraphNode node) {
        CoordinateMap cm = h2.getCoordinateMap();
        double[] t = cm.get(target);
        double[] p = cm.get(separator);
        double[] x = cm.get(node);

        for (int i = 0; i < x.length; ++i) {
            x[i] -= p[i];
        }

        for (int i = 0; i < t.length; ++i) {
            t[i] -= p[i];
        }

        double dotProduct = 0.0;

        for (int i = 0; i < t.length; ++i) {
            dotProduct += x[i] * t[i];
        }

        double len = 0.0;

        for (int i = 0; i < t.length; ++i) {
            len += t[i] * t[i];
        }

        return dotProduct / Math.sqrt(len);
    }
}
