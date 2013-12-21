package net.coderodde.cskit.ds.tree;

import java.util.Iterator;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests <code>OrderStatisticTree</code>.
 *
 * @author Rodion Efremov
 * @version 1.6180 (19.12.2013)
 */
public class OrderStatisticTreeTest {

    private OrderStatisticTree<Integer, Integer> tree =
        new OrderStatisticTree<Integer, Integer>();

    @Test
    public void test1() {
        for (int i = 0; i < 100; ++i) {
            tree.put(i, i);
        }

        assertEquals(100, tree.size());

        for (Integer i = 0; i < tree.size(); ++i) {
            assertEquals(i, tree.get(i));
            assertEquals(i, tree.entryAt(i).getKey());
            assertEquals(i, tree.entryAt(i).getValue());
            assertEquals(i, new Integer(tree.rankOf(new Integer(i))));
        }

        for (Integer i = 20; i < 100; ++i) {
            tree.remove(i);
        }

        assertEquals(20, tree.size());
    }

    @Test
    public void test2() {
        tree.clear();

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());

        for (int i = 0; i < 20; i++) {
            tree.put(i, i);
        }

        int i = 0;
        Integer j;
        Iterator<Integer> iterator = tree.iterator();

        while (iterator.hasNext()) {
            j = iterator.next();

            if (j == 9 || j == 19) {
                iterator.remove();
            }

            i++;
        }

        assertEquals(18, tree.size());
        assertFalse(tree.isEmpty());

        for (i = 0; i < 20; ++i) {
            if (i == 9 || i == 19) {
                assertFalse(tree.containsKey(i));
            } else {
                assertTrue(tree.containsKey(i));
            }
        }

        assertTrue(tree.isHealthy());

        for (i = 0; i < 20; ++i) {
            tree.remove(i);
        }

        assertTrue(tree.isEmpty());
        assertTrue(tree.isHealthy());
        assertEquals(0, tree.size());

        tree.put(1, 1);

        assertFalse(tree.isEmpty());
        assertTrue(tree.isHealthy());
        assertEquals(1, tree.size());

        Set<Integer> set = tree.keySet();

        assertEquals(1, set.size());

        set.remove(1);

        assertTrue(set.isEmpty());

        tree.put(1, 1);
        tree.put(2, 2);
        tree.put(3, 3);

        assertEquals(3, tree.size());

        set = tree.keySet();
        Iterator<Integer> iter2 = set.iterator();

        for (i = 1; i <= 3; ++i) {
            Integer tmp = iter2.next();
            assertEquals(new Integer(i), tmp);
        }
    }
}
