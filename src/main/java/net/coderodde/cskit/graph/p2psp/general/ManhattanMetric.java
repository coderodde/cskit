package net.coderodde.cskit.graph.p2psp.general;

import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * This class implements the Manhattan metric used mainly in <code>A*</code>
 * -search.
 *
 * @author Rodion Efremov
 * @version 1.618 (16.12.2013)
 */
public class ManhattanMetric extends HeuristicFunction {

    private double[] p;

    public ManhattanMetric(CoordinateMap map, DirectedGraphNode target) {
        super(map, target);
        this.p = map.get(target);
    }

    @Override
    public double get(DirectedGraphNode u) {
        double[] q = map.get(u);
        double sum = 0.0;

        for (int i = 0; i < q.length; ++i) {
            sum += Math.abs(p[i] - q[i]);
        }

        return sum;
    }

    @Override
    public void setTarget(DirectedGraphNode u) {
        this.p = map.get(u);
        this.target = u;
    }

    @Override
    public double get(double[] p, double[] q) {
        double sum = 0.0;

        for (int i = 0; i < q.length; ++i) {
            sum += Math.abs(p[i] - q[i]);
        }

        return sum;
    }
}
