package net.coderodde.cskit.graph.mst;

import java.util.List;
import net.coderodde.cskit.Utilities.Pair;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.WeightFunction;

/**
 * This abstract class defines the API for minimum-spanning-    tree algorithms.
 *
 * @author Rodion Efremov
 * @version 1.618033 (27.12.2013)
 */
public abstract class MinimumSpanningTreeFinder {

    public abstract Pair<List<DirectedGraphNode>, Double>
            find(List<DirectedGraphNode> graph,
                 WeightFunction w);
}
