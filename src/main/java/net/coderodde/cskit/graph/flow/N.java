package net.coderodde.cskit.graph.flow;

import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * This class implements a singly linked list for the sake of
 * relabel-to-front algorithm.
 *
 * @author Rodion Efremov
 * @version 1.61803 (26.12.2013)
 */
class N {

    static class NNode {
        NNode next;
        DirectedGraphNode node;
    }

    NNode head;
    NNode current;
}
