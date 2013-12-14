package net.coderodde.cskit.sorting;

import java.util.Comparator;
import static net.coderodde.cskit.Utilities.debugPrintArray;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests <code>RunScanner</code>.
 *
 * @author Rodion Efremov
 */
public class RunScannerTest {

    private static final class AscendingComparator
    implements Comparator<Integer> {

        @Override
        public int compare(Integer i1, Integer i2) {
            return i1 - i2;
        }
    }

    private static final class DescendingComparator
    implements Comparator<Integer> {

        @Override
        public int compare(Integer i1, Integer i2) {
            return i2 - i1;
        }
    }

    private AscendingComparator ascCmp = new AscendingComparator();
    private DescendingComparator descCmp = new DescendingComparator();

    @Test
    public void testScan() {
        Integer[] array = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        debugPrintArray(array);
        RunScanner<Integer> scanner = new RunScanner<Integer>();
        RunQueue q = null;

        assertEquals(1, (q = scanner.scanAndReturnRunQueue(array, 0, 6)).size());
        System.out.println(q);

        assertEquals(1, (q = scanner.scanAndReturnRunQueue(array, 6, 0)).size());
        System.out.println(q);

        array = new Integer[]{1, 2, 3, 4, 3, 2, 1};

        assertEquals(2, (q = scanner.scanAndReturnRunQueue(array, 0, 6)).size());
        System.out.println(q);

        assertEquals(2, (q = scanner.scanAndReturnRunQueue(array, 6, 0)).size());
        System.out.println(q);

        array = new Integer[]{1, 3, 2};

        assertEquals(2, (q = scanner.scanAndReturnRunQueue(array, 0, 2)).size());
        System.out.println(q);

        array = new Integer[]{1, 3};

        assertEquals(1, (q = scanner.scanAndReturnRunQueue(array, 0, 1)).size());
        System.out.println(q);

        assertEquals(1, (q = scanner.scanAndReturnRunQueue(array, 0, 0)).size());
        System.out.println(q);

        array = new Integer[]{1};

        assertEquals(1, (q = scanner.scanAndReturnRunQueue(array, 0, 0)).size());
        System.out.println(q);
    }
}
