package net.coderodde.cskit.p2psp;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.coderodde.cskit.Utilities.tracebackPath;

import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * Implements the classic, point-to-point breadth-first search.
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public class BreadthFirstSearchFinder implements UniformCostPathFinder {

    @Override
    public List<DirectedGraphNode> find(DirectedGraphNode source,
                                        DirectedGraphNode target) {
        Map<DirectedGraphNode, DirectedGraphNode> parentMap =
                new HashMap<DirectedGraphNode, DirectedGraphNode>();

        Set<DirectedGraphNode> CLOSED = new HashSet<DirectedGraphNode>();
        Deque<DirectedGraphNode> Q = new LinkedList<DirectedGraphNode>();

        Q.addLast(source);
        parentMap.put(source, null);

        while (Q.isEmpty() == false) {
            DirectedGraphNode current = Q.removeFirst();

            if (current.equals(target)) {
                return tracebackPath(target, parentMap);
            }

            CLOSED.add(current);

            for (DirectedGraphNode child : current) {
                if (CLOSED.contains(child) == false) {
                    CLOSED.add(child);
                    Q.addLast(child);
                    parentMap.put(child, current);
                }
            }
        }

        // No path found.
        return java.util.Collections.<DirectedGraphNode>emptyList();
    }

}
