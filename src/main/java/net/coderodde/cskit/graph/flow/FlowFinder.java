package net.coderodde.cskit.graph.flow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.coderodde.cskit.Utilities.Pair;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.WeightFunction;

/**
 * This abstract class defines the API for maximum-flow algorithms.
 *
 * @author Rodion Efremov
 * @version 1.61803
 */
public abstract class FlowFinder {

    public abstract Pair<WeightFunction, Double> find(DirectedGraphNode source,
                                                      DirectedGraphNode sink,
                                                      WeightFunction w);

    public static final void resolveParallelEdges(
            List<DirectedGraphNode> graph, WeightFunction w) {
        List<DirectedGraphNode> toAdd = new ArrayList<DirectedGraphNode>();
        int index = 0;

        for (DirectedGraphNode from : graph) {
            for (DirectedGraphNode to : from) {
                if (to.hasChild(from)) {
                    double weight = w.get(from, to);
                    to.removeChild(from);
                    DirectedGraphNode newOne =
                            new DirectedGraphNode(
                            "Antiparallel node from " + to.getName() +
                            " to " + from.getName());

                    to.addChild(newOne);
                    newOne.addChild(from);

                    w.put(to, newOne, weight);
                    w.put(newOne, from, weight);

                    toAdd.add(newOne);
                }
            }
        }

        graph.addAll(toAdd);
    }

    public static final void removeSelfLoops(List<DirectedGraphNode> graph) {
        for (DirectedGraphNode u : graph) {
            u.removeChild(u);
        }
    }

    public static final DirectedGraphNode
            createSuperSource(WeightFunction w,
                              DirectedGraphNode... sources) {
        DirectedGraphNode superSource = new DirectedGraphNode("Super source");

        for (DirectedGraphNode source : sources) {
            superSource.addChild(source);
            w.put(superSource, source, Double.POSITIVE_INFINITY);
        }

        return superSource;
    }

    public static final DirectedGraphNode
            createSuperSink(WeightFunction w,
                            DirectedGraphNode... sinks) {
        DirectedGraphNode superSink = new DirectedGraphNode("Super sink");

        for (DirectedGraphNode sink : sinks) {
            sink.addChild(superSink);
            w.put(sink, superSink, Double.POSITIVE_INFINITY);
        }

        return superSink;
    }

    public static final void pruneSource(DirectedGraphNode source) {
        Iterator<DirectedGraphNode> iterator =
                source.parentIterable().iterator();

        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    public static final void pruneSink(DirectedGraphNode sink) {
        Iterator<DirectedGraphNode> iterator =
                sink.iterator();

        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    protected double findMinimumEdgeAndRemove(List<DirectedGraphNode> path,
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

    protected double residualEdgeWeight(DirectedGraphNode from,
                                        DirectedGraphNode to,
                                        WeightFunction f,
                                        WeightFunction c) {
        if (from.hasChild(to)) {
            return c.get(from, to) - f.get(from, to);
        } else if (to.hasChild(from)) {
            return f.get(to, from);
        } else {
            return 0.0;
        }
    }

    protected void initializePreflow(List<DirectedGraphNode> network,
                                     DirectedGraphNode source,
                                     Map<DirectedGraphNode, Integer> h,
                                     Map<DirectedGraphNode, Double> e,
                                     WeightFunction f,
                                     WeightFunction c) {
        for (DirectedGraphNode u : network) {
            h.put(u, 0);
            e.put(u, 0.0);
        }
/*
        for (DirectedGraphNode from : network) {
            for (DirectedGraphNode to : from) {
                f.put(from, to, 0.0);
            }
        }*/

        h.put(source, network.size());

        for (DirectedGraphNode u : source) {
            f.put(source, u, c.get(source, u));
            e.put(u, c.get(source, u));
            e.put(source, e.get(source) - c.get(source, u));
        }
    }

    protected void relabel(DirectedGraphNode u,
                           Map<DirectedGraphNode, Integer> h,
                           WeightFunction f,
                           WeightFunction c) {
        int minh = Integer.MAX_VALUE;

        for (DirectedGraphNode v : u.allIterable()) {
            if (residualEdgeWeight(u, v, f, c) > 0.0) {
                if (minh > h.get(v)) {
                    minh = h.get(v);
                }
            }
        }

        h.put(u, minh + 1);
    }

    protected void push(DirectedGraphNode from,
                        DirectedGraphNode to,
                        WeightFunction f,
                        WeightFunction c,
                        Map<DirectedGraphNode, Double> e) {
        double delta = Math.min(e.get(from),
                                residualEdgeWeight(from, to, f, c));

        f.put(from, to, f.get(from, to) + delta);
        f.put(to, from, f.get(to, from) - delta);
/*
        if (from.hasChild(to)) {
            f.put(from, to, f.get(from, to) + delta);
        } else {
            f.put(to, from, f.get(to, from) - delta);
        }*/

        e.put(from, e.get(from) - delta);
        e.put(to, e.get(to) + delta);
    }
}
