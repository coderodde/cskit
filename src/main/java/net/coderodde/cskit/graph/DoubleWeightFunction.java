package net.coderodde.cskit.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a weight function with double weights.
 *
 * @author Rodion Efremov
 * @version 1.6 (7.12.2013)
 */
public class DoubleWeightFunction
implements WeightFunction<DirectedGraphNode, Double> {

    /**
     * The default capacity for the map structures.
     */
    public static final int DEFAULT_CAPACITY = 128;

    /**
     * The default load factor for the map structures.
     */
    public static final float DEFAULT_LOAD_FACTOR = 1.05f;

    /**
     * The initial capacity for each map structure.
     */
    private final int capacity;

    /**
     * The initial load factor for each map structure.
     */
    private final float loadFactor;

    /**
     * A weight matrix.
     */
    private Map<DirectedGraphNode, Map<DirectedGraphNode, Double>> map;

    /**
     * Constructs a weight map with double values.
     *
     * @param capacity the initial capacity of the map structures.
     * @param loadFactor the initial load factor of the map structures.
     */
    public DoubleWeightFunction(int capacity, float loadFactor) {
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        map = new HashMap<DirectedGraphNode,
                          Map<DirectedGraphNode, Double>>(capacity, loadFactor);
    }

    /**
     * Constructs a weight map with double values. Load factor is 1,05.
     *
     * @param capacity the initial capacity.
     */
    public DoubleWeightFunction(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a weight map with double values. Load factor is 1.05 and
     * capacity is 128.
     */
    public DoubleWeightFunction() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Maps an edge <code>(from, to)</code> to its weight.
     *
     * @param from the tail node of the edge.
     * @param to the head node of the edge.
     * @param weight the weight of the edge.
     */
    public void put(DirectedGraphNode from, DirectedGraphNode to, Double weight) {
        if (map.containsKey(from) == false) {
            map.put(from, new HashMap<DirectedGraphNode,
                                      Double>(capacity, loadFactor));
        }

        map.get(from).put(to, weight);
    }

    /**
     * Retrieves the weight of the edge <code>(from, to)</code>.
     *
     * @param from the tail of the edge.
     * @param to the head of the edge.
     * @return the weight of the edge.
     */
    public Double get(DirectedGraphNode from, DirectedGraphNode to) {
        return map.get(from).get(to);
    }

    /**
     * Returns the identity element.
     *
     * @return 0.0
     */
    public Double zero() {
        return 0.0;
    }

    /**
     * Appends the two weights.
     *
     * @param left a weight value.
     * @param right another weight value.
     * @return the sum of <code>left</code> and <code>right</code>.
     */
    public Double plus(Double left, Double right) {
        return left + right;
    }
}
