package net.coderodde.cskit.graph;

import java.util.Map;

/**
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public interface WeightFunction<N, W extends Comparable<? super W>> {

    public void put(N from, N to, W weight);

    public W get(N from, N to);

    public W zero();

    public W plus(W left, W right);
}
