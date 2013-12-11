package net.coderodde.cskit.graph.p2psp.uniform;

import java.util.List;
import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * This interface defines the entry points for uniform cost shortest path
 * finders.
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public interface UniformCostPathFinder {
    public List<DirectedGraphNode> find(DirectedGraphNode source,
                                        DirectedGraphNode target);
}
