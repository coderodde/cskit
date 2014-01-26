package net.coderodde.cskit.ds.list;

import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * This class tests TreeList.
 *
 * @author Rodion Efremov
 */
public class TreeListTest {

    private TreeList<Integer> list = new TreeList<Integer>(3);

    @Before
    public void init() {
        list.clear();
    }

    @Test
    public void testAdd_GenericType() {
        list.clear();

        assertTrue(list.isHealthy());

        for (int i = 0; i < 100; ++i) {
            list.add(new Integer(i));
        }

        assertTrue(list.isHealthy());

        Iterator<Integer> iter = list.iterator();
        Integer i = 0;

        while (iter.hasNext()) {
            assertEquals(i, iter.next());
            ++i;
        }

        assertEquals(new Integer(100), i);

        Iterator<Integer> descIter = list.descendingIterator();

        i = 99;

        while (i >= 0) {
            assertEquals(i, descIter.next());
            --i;
        }

        assertEquals(new Integer(-1), i);
        assertEquals(100, list.size());
        assertFalse(list.isEmpty());
        assertTrue(list.isHealthy());
    }

    @Test
    public void testAddFirst() {
    }

    @Test
    public void testAddLast() {
    }

    @Test
    public void testAdd_int_GenericType() {
    }

    @Test
    public void testGet() {
    }

    @Test
    public void testSet() {
    }

    @Test
    public void testSize() {
    }

    @Test
    public void testClear() {
    }

    @Test
    public void testOfferFirst() {
    }

    @Test
    public void testOfferLast() {
    }

    @Test
    public void testRemoveFirst() {
    }

    @Test
    public void testRemoveLast() {
    }

    @Test
    public void testRemove_int() {
    }

    @Test
    public void testPollFirst() {
    }

    @Test
    public void testPollLast() {
    }

    @Test
    public void testGetFirst() {
    }

    @Test
    public void testGetLast() {
    }

    @Test
    public void testPeekFirst() {
    }

    @Test
    public void testPeekLast() {
    }

    @Test
    public void testRemoveFirstOccurrence() {
    }

    @Test
    public void testRemoveLastOccurrence() {
    }

    @Test
    public void testOffer() {
    }

    @Test
    public void testRemove_0args() {
    }

    @Test
    public void testPoll() {
    }

    @Test
    public void testElement() {
    }

    @Test
    public void testPeek() {
    }

    @Test
    public void testPush() {
    }

    @Test
    public void testPop() {
    }

    @Test
    public void testIterator() {
    }

    @Test
    public void testDescendingIterator() {
    }

    @Test
    public void testIsHealthy() {
    }
}
