package net.coderodde.cskit.graph.flow;

import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * The node list for relabel-to-front algorithm.
 *
 * @author Rodion Efremov
 * @version 1.61803 (25.12.2013)
 */
class L {

    static class LNode {
        DirectedGraphNode node;
        LNode next;
    }

    LNode head;

    @Override
    public int hashCode() {
        return head.node.hashCode();
    }
}
