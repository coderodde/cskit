package net.coderodde.cskit;

public interface ParentIterable<T> {

    /**
     * Gets the iterator over incoming nodes.
     *
     * @return the iterator over incoming nodes.
     */
    public Iterable<T> parentIterable();

}
