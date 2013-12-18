package net.coderodde.cskit.graph.p2psp.general;

import net.coderodde.cskit.graph.DirectedGraphNode;

/**
 * This class implements Euclidian metric for use with <tt>A*</tt>-search.
 *
 * @author Rodion Efremov
 * @version 1.618 (16.12.2013)
 */
public class EuclidianMetric extends HeuristicFunction {

    private double[] p;

    public EuclidianMetric(CoordinateMap map, DirectedGraphNode target) {
        super(map, target);
    }

    @Override
    public void setTarget(DirectedGraphNode u) {
        this.target = u;
        this.p = map.get(u);
    }

    @Override
    public double get(DirectedGraphNode u) {
        double[] q = map.get(u);
        double sum = 0.0;

        for (int i = 0; i < q.length; ++i) {
            sum += (p[i] - q[i]) * (p[i] - q[i]);
        }

        return Math.sqrt(sum);
    }

    @Override
    public double get(double[] p, double[] q) {
        double sum = 0.0;

        for (int i = 0; i < q.length; ++i) {
            sum += (p[i] - q[i]) * (p[i] - q[i]);
        }

        return Math.sqrt(sum);
    }
}
