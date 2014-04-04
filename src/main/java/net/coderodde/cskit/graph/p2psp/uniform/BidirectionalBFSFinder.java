package net.coderodde.cskit.graph.p2psp.uniform;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static net.coderodde.cskit.Utilities.tracebackPathBidirectional;
import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * This class implements bidirectional breadth-first search algorithm.
 *
 * @author Rodion Efremov
 * @version 1.6
 */
public class BidirectionalBFSFinder
implements UniformCostPathFinder {

    // We choose to reuse the data structures. This resembles losing virginity:
    // it all expands and becomes loose enough. :)
    private Map<DirectedGraphNode, DirectedGraphNode> parentMapA =
            new HashMap<DirectedGraphNode, DirectedGraphNode>();

    private Map<DirectedGraphNode, DirectedGraphNode> parentMapB =
            new HashMap<DirectedGraphNode, DirectedGraphNode>();

    public List<DirectedGraphNode>
            find(DirectedGraphNode source, DirectedGraphNode target) {
        clear();

        parentMapA.put(source, null);
        parentMapB.put(target, null);

        Deque<DirectedGraphNode> queueA = new LinkedList<DirectedGraphNode>();
        Deque<DirectedGraphNode> queueB = new LinkedList<DirectedGraphNode>();

        queueA.addLast(source);
        queueB.addLast(target);
        
        while (queueA.isEmpty() == false && queueB.isEmpty() == false) {
            DirectedGraphNode A = queueA.removeFirst();

            for (DirectedGraphNode child : A) {
                if (parentMapA.containsKey(child) == false) {
                    parentMapA.put(child, A);
                    queueA.addLast(child);
                    
                    if (parentMapB.containsKey(child)) {
                        return tracebackPathBidirectional(child,
                                                          parentMapA,
                                                          parentMapB);
                    }
                }
            }

            // Expand the backwards search.
            DirectedGraphNode B = queueB.removeFirst();

            for (DirectedGraphNode parent : B.parentIterable()) {
                if (parentMapB.containsKey(parent) == false) {
                    parentMapB.put(parent, B);
                    queueB.addLast(parent);
                    
                    if (parentMapA.containsKey(parent)) {
                        return tracebackPathBidirectional(parent,
                                                          parentMapA,
                                                          parentMapB);
                    }
                }
            }
        }

        return Collections.<DirectedGraphNode>emptyList();
    }

    private void clear() {
        parentMapA.clear();
        parentMapB.clear();
    }
}
