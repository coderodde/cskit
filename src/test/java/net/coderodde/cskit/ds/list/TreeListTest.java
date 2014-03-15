package net.coderodde.cskit.ds.list;

import java.util.Iterator;
import java.util.ListIterator;
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
    public void iteratorsOnEmpty() {
        Iterator<Integer> i1 = list.iterator();
        assertFalse(i1.hasNext());

        Iterator<Integer> i2 = list.iterator();
        assertFalse(i2.hasNext());

        ListIterator<Integer> i3 = list.listIterator();
        assertFalse(i3.hasNext());
        assertFalse(i3.hasPrevious());
    }

    @Test
    public void testDescendingIterator() {
        for (int i = 0; i < 40; ++i) {
            list.add(i, i);
        }

        Iterator<Integer> it = list.descendingIterator();

        assertTrue(it.hasNext());

        it.next();
        Integer tmp = it.next();

        assertEquals(new Integer(38), tmp);

        it.remove();

        assertEquals(39, list.size());
        assertEquals(new Integer(39), list.get(list.size() - 1));
        assertEquals(new Integer(37), list.get(list.size() - 2));
        assertTrue(list.isHealthy());

        for (int i = 0; i < 19; ++i) {
            assertTrue(it.hasNext());
            it.next();
        }

        for (int i = 0; i < 6; ++i) {
            assertTrue(it.hasNext());
            it.next();
            it.remove();
            assertTrue(list.isHealthy());
        }
    }
}
