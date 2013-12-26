package net.coderodde.cskit.graph.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.coderodde.cskit.Utilities.Pair;
import static net.coderodde.cskit.Utilities.expandGraph;
import net.coderodde.cskit.graph.DirectedGraphNode;
import net.coderodde.cskit.graph.WeightFunction;
import net.coderodde.cskit.graph.flow.L.LNode;

/**
 * This class implements a generic push-relabel algorithm, which runs in
 * time <tt>O(EV^2)</tt>.
 *
 * @author Rodion Efremov
 * @version 1.61803 (25.12.2013)
 */
public class RelabelToFrontFlowFinder extends FlowFinder {

    private Map<DirectedGraphNode, Double> e =
    new HashMap<DirectedGraphNode, Double>();

    private Map<DirectedGraphNode, Integer> h =
    new HashMap<DirectedGraphNode, Integer>();

    @Override
    public Pair<WeightFunction, Double> find(DirectedGraphNode source,
                                             DirectedGraphNode sink,
                                             WeightFunction c) {
        Set<DirectedGraphNode> set = expandGraph(source);

        if (set.contains(sink) == false) {
            return new Pair<WeightFunction, Double>(new WeightFunction(), 0.0);
        }

        set.remove(source);
        set.remove(sink);

        WeightFunction f = new WeightFunction();
        Pair<L, Map<DirectedGraphNode, N>> pair = buildList(set, f, c);

        set.add(source);
        set.add(sink);

        List<DirectedGraphNode> graph =
                new ArrayList<DirectedGraphNode>(set.size());

        for (DirectedGraphNode u : set) {
            graph.add(u);
        }

        super.initializePreflow(graph, source, h, e, f, c);

        LNode uNode = pair.first.head;
        LNode previous = null;

        while (uNode != null) {
            DirectedGraphNode u = uNode.node;
            int oldHeight = h.get(u);
            discharge(u, pair.second, e, h, f, c);

            if (h.get(u) > oldHeight) {
                if (previous != null) {
                    // uNode is not at front of L.
                    previous.next = uNode.next;
                    LNode tmpHead = pair.first.head;
                    pair.first.head = uNode;
                    uNode.next = tmpHead;
                    previous = null;
                }
            }

            previous = uNode;
            uNode = uNode.next;
        }

        Pair<WeightFunction, Double> ret =
                new Pair<WeightFunction, Double>();

        ret.first = f;
        ret.second = 0.0;

        for (DirectedGraphNode child : source) {
            ret.second += f.get(source, child);
        }

        for (DirectedGraphNode parent : source.parentIterable()) {
            ret.second -= f.get(parent, source);
        }

        return ret;
    }

    /**
     * Builds the node list. Assumes that <tt>set</tt> contains no source, nor
     * sink nodes.
     *
     * @param set the set of nodes except the source and the sink.
     *
     * @return the pair containing the main list and the map from nodes to their
     * respective neighbor lists.
     */
    private Pair<L, Map<DirectedGraphNode, N>>
            buildList(Set<DirectedGraphNode> set,
                      WeightFunction f,
                      WeightFunction c) {
        L list = new L();
        Map<DirectedGraphNode, N> map =
                new HashMap<DirectedGraphNode, N>(set.size());

        for (DirectedGraphNode u : set) {
            L.LNode listNode = new L.LNode();
            listNode.node = u;

            if (list.head == null) {
                list.head = listNode;
            } else {
                L.LNode tmp = list.head;
                list.head = listNode;
                listNode.next = tmp;
            }

            N neighborList = new N();
            map.put(u, neighborList);

            for (DirectedGraphNode v : u.allIterable()) {
                N.NNode newOne = new N.NNode();
                newOne.node = v;

                if (neighborList.head == null) {
                    neighborList.head = newOne;
                } else {
                    N.NNode tmpHead = neighborList.head;
                    neighborList.head = newOne;
                    newOne.next = tmpHead;
                }
            }

            neighborList.current = neighborList.head;
        }

        return new Pair<L, Map<DirectedGraphNode, N>>(list, map);
    }
}