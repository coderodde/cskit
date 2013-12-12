package net.coderodde.cskit.ds.pq.support;

import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.Test;
import static net.coderodde.cskit.Utilities.getAscendingArray;
import static net.coderodde.cskit.Utilities.isSorted;
import static net.coderodde.cskit.Utilities.shuffle;
import net.coderodde.cskit.sorting.Range;
import static org.junit.Assert.*;

/**
 * This class test <code>BinaryHeap</code>.
 *
 * @author Rodion Efremov
 */
public class BinaryHeapTest {
    private BinaryHeap<Integer> pq = new BinaryHeap<Integer>(9);

    @Test(expected = NoSuchElementException.class)
    public void testSize() {
        assertTrue(pq.isEmpty());
        assertEquals(pq.size(), 0);
        pq.min();
    }

    @Test
    public void testInsert() {
        pq.clear();
        Integer[] array = getAscendingArray(10);
        Random r = new Random();
        shuffle(array, r);

        for (Integer i : array) {
            pq.insert(i, i);
        }

        int i = 0;
        while (pq.size() > 0) {
            array[i++] = pq.extractMinimum();
        }

        assertTrue(isSorted(array, new Range(0, array.length - 1)));
    }

    @Test
    public void testDecreasePriority() {
    }

    @Test
    public void testMin() {
    }

    @Test
    public void testExtractMinimum() {
    }

    @Test
    public void testClear() {
    }
}
