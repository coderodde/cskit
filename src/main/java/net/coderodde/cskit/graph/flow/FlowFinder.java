package net.coderodde.cskit.graph.flow;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.coderodde.cskit.Utilities.Pair;
import net.coderodde.cskit.Utilities.Triple;
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
}
