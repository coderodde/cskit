package net.coderodde.cskit.graph;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
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

        Iterator<DirectedGraphNode> childIterOfA = A.iterator();

        assertTrue(childIterOfA.hasNext());
        assertEquals(B1, childIterOfA.next());
        assertTrue(childIterOfA.hasNext());
        assertEquals(B2, childIterOfA.next());
        assertFalse(childIterOfA.hasNext());

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

        assertTrue(iterOfA.hasNext());
        assertEquals(B1, iterOfA.next());
        A.removeChild(B2);
        iterOfA.hasNext(); // this must throw.
    }
}
