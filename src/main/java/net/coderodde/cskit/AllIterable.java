package net.coderodde.cskit;

/**
 * This interface defines the API for iterating over all nodes connected to
 * the object implementing this interface, in both directions.
 *
 * @author Rodion Efremov
 * @version 1.61803 (24.12.2013)
 */
public interface AllIterable<T> {

    /**
     * Returns the <tt>Iterable</tt> over all connected nodes.
     *
     * @return the <tt>Iterable</tt> over all connected nodes.
     */
    public Iterable<T> allIterable();
}
