package net.coderodde.cskit.graph.mst;

import java.util.List;
import net.coderodde.cskit.Utilities.Pair;
import net.coderodde.cskit.graph.UndirectedGraphEdge;
import net.coderodde.cskit.graph.UndirectedGraphNode;
import net.coderodde.cskit.graph.DirectedGraphWeightFunction;
import net.coderodde.cskit.graph.UndirectedGraphWeightFunction;

/**
 * This abstract class defines the API for minimum-spanning-    tree algorithms.
 *
 * @author Rodion Efremov
 * @version 1.618033 (27.12.2013)
 */
public abstract class MinimumSpanningTreeFinder {

    public abstract Pair<List<UndirectedGraphEdge>, Double>
            find(List<UndirectedGraphNode> graph,
                 UndirectedGraphWeightFunction w);
}
