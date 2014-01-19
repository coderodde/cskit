package net.coderodde.cskit.graph.mst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.coderodde.cskit.Utilities.Pair;
import static net.coderodde.cskit.Utilities.checkNotNull;
import static net.coderodde.cskit.Utilities.expandGraph;
import net.coderodde.cskit.ds.disjointset.DisjointSet;
import net.coderodde.cskit.graph.UndirectedGraphEdge;
import net.coderodde.cskit.graph.UndirectedGraphNode;
import net.coderodde.cskit.graph.DirectedGraphWeightFunction;
import net.coderodde.cskit.graph.UndirectedGraphWeightFunction;

/**
 * This class implements Kruskal's minimum spanning tree algorithm.
 *
 * @author Rodion Efremov
 * @version 1.618033 (28.12.2013)
 */
public class KruskalMSTFinder extends MinimumSpanningTreeFinder {

    private Map<UndirectedGraphNode, DisjointSet<UndirectedGraphNode>> map =
    new HashMap<UndirectedGraphNode, DisjointSet<UndirectedGraphNode>>();

    private Set<UndirectedGraphNode> set =
    new HashSet<UndirectedGraphNode>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<List<UndirectedGraphEdge>, Double>
           find(List<UndirectedGraphNode> graph,
                UndirectedGraphWeightFunction w) {
        Double weight = 0.0;
        List<UndirectedGraphEdge> edgeList = checkPrerequisites(graph, w);

        if (edgeList == null) {
            return null;
        }

        DisjointSet<UndirectedGraphNode> ds =
                new DisjointSet<UndirectedGraphNode>();

        List<UndirectedGraphEdge> mst =
                new ArrayList<UndirectedGraphEdge>(set.size() - 1);

        Set<UndirectedGraphNode> CLOSED =
                new HashSet<UndirectedGraphNode>(graph.size());

        for (UndirectedGraphEdge e : edgeList) {
            if (CLOSED.size() >= set.size()) {
                break;
            }

            if (ds.find(e.getA()).equals(ds.find(e.getB())) == false) {
                ds.union(e.getA(), e.getB());
                mst.add(e);
                weight += e.getWeight();
                CLOSED.add(e.getA());
                CLOSED.add(e.getB());
            }
        }

        return new Pair<List<UndirectedGraphEdge>, Double>(mst, weight);
    }

    private List<UndirectedGraphEdge>
            checkPrerequisites(List<UndirectedGraphNode> graph,
                                    UndirectedGraphWeightFunction w) {
        checkNotNull(graph, "'graph' is null.");
        checkNotNull(w, "'w' is null.");

        set.clear();
        map.clear();

        if (graph.isEmpty()) {
            return null;
        }

        set.addAll(expandGraph(graph));

        for (UndirectedGraphNode u : set) {
            map.put(u, new DisjointSet<UndirectedGraphNode>());
        }

        List<UndirectedGraphEdge> edgeList =
                new ArrayList<UndirectedGraphEdge>();

        Set<UndirectedGraphEdge> edgeSet =
                new HashSet<UndirectedGraphEdge>();

        for (UndirectedGraphNode u : set) {
            for (UndirectedGraphNode v : u) {
                UndirectedGraphEdge edge = new UndirectedGraphEdge(u, v);

                if (edgeSet.contains(edge) == false) {
                    edge.setWeight(w.get(u, v));
                    edgeSet.add(edge);
                    edgeList.add(edge);
                }
            }
        }

        Collections.sort(
                edgeList,
                new UndirectedGraphEdge.AscendingComparator());

        return edgeList;
    }
}
