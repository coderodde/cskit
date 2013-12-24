package net.coderodde.cskit.graph;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests <code>DirectedGraphNode</code>.
 *
 * @author Rodion Efremov
 */
public class DirectedGraphNodeTest {

    @Test
    public void testAddChild() {
        DirectedGraphNode A = new DirectedGraphNode("A");
        DirectedGraphNode B1 = new DirectedGraphNode("B1");
        DirectedGraphNode B2 = new DirectedGraphNode("B2");
        DirectedGraphNode C = new DirectedGraphNode("C");

        A.addChild(B1);
        A.addChild(B2);
        C.addChild(A); // C is a parent of A.

        Set<DirectedGraphNode> childrenSet = new HashSet<DirectedGraphNode>();

        for (DirectedGraphNode u : A) {
            childrenSet.add(u);
        }

        assertTrue(childrenSet.contains(B1));
        assertTrue(childrenSet.contains(B2));
        assertEquals(2, childrenSet.size());

        ////

        Iterator<DirectedGraphNode> parentIterOfA =
                A.parentIterable().iterator();

        assertTrue(parentIterOfA.hasNext());
        assertEquals(C, parentIterOfA.next());
        assertFalse(parentIterOfA.hasNext());
    }

    @Test
    public void testRemoveChild() {
        DirectedGraphNode A = new DirectedGraphNode("A");
        DirectedGraphNode B1 = new DirectedGraphNode("B1");
        DirectedGraphNode B2 = new DirectedGraphNode("B2");
        A.addChild(B1);
        A.addChild(B2);
        assertTrue(A.hasChild(B1));
        assertTrue(A.hasChild(B2));

        A.removeChild(B2);
        assertFalse(A.hasChild(B2));

        Iterator<DirectedGraphNode> iterOfA = A.iterator();

        assertTrue(iterOfA.hasNext());
        assertEquals(B1, iterOfA.next());
        assertFalse(iterOfA.hasNext());
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testConcurrentModificationMustThrow() {
        DirectedGraphNode A = new DirectedGraphNode("A");
        DirectedGraphNode B1 = new DirectedGraphNode("B1");
        DirectedGraphNode B2 = new DirectedGraphNode("B2");
        A.addChild(B1);
        A.addChild(B2);

        Iterator<DirectedGraphNode> iterOfA = A.iterator();

        assertEquals(B1, iterOfA.next());
        A.removeChild(B1);
        iterOfA.next(); // this must throw.
    }

    @Test(expected = NoSuchElementException.class)
    public void iterationThrowsNoSuchElementExceptionWhenDone() {
        DirectedGraphNode A = new DirectedGraphNode("A");
        DirectedGraphNode B1 = new DirectedGraphNode("B1");
        DirectedGraphNode B2 = new DirectedGraphNode("B2");

        A.addChild(B1);
        A.addChild(B2);
        Iterator<DirectedGraphNode> iter = A.iterator();

        assertTrue(iter.hasNext());
        assertEquals(B1, iter.next());

        assertTrue(iter.hasNext());
        assertEquals(B2, iter.next());

        assertFalse(iter.hasNext());
        iter.next(); // this must throw.
    }
}
