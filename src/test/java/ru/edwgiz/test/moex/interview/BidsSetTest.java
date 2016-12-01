package ru.edwgiz.test.moex.interview;

import com.gs.collections.impl.map.mutable.primitive.ShortIntHashMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class BidsSetTest {

    @Test
    void testAppend() {
        BidsSet bids = new BidsSet();

        // test illegal price
        testAppendIllegalArgumentException(bids, -1, 0);
        testAppendIllegalArgumentException(bids, 99, 0);
        bids.append(100, 0);
        bids.append(10000, 0);
        testAppendIllegalArgumentException(bids, 10001, 0);

        // test illegal amount
        testAppendIllegalArgumentException(bids, 100, -1);
        bids.append(100, 0);
        bids.append(100, 1000);
        testAppendIllegalArgumentException(bids, 100, 1001);
    }

    private void testAppendIllegalArgumentException(BidsSet bids, int price, int amount) {
        try {
            bids.append(price, amount);
            fail("No expected price exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    void testMinMaxPrice() {
        BidsSet bids = new BidsSet();
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append(1000, 8);
        assertEquals(1000, bids.getMinPrice());
        assertEquals(1000, bids.getMaxPrice());

        bids.append(10000, 8);
        assertEquals(1000, bids.getMinPrice());
        assertEquals(10000, bids.getMaxPrice());

        bids.append(100, 8);
        assertEquals(100, bids.getMinPrice());
        assertEquals(10000, bids.getMaxPrice());
    }

    @Test
    void testRemoveLesser() {
        BidsSet bids = new BidsSet();

        bids.removeLesser( 0);
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append(1000, 8);
        ShortIntHashMap expected = new ShortIntHashMap(bids.m);

        bids.removeLesser(100);
        assertEquals(expected, bids.m);
        assertEquals(1000, bids.getMaxPrice());
        assertEquals(1000, bids.getMinPrice());

        bids.removeLesser(1000);
        assertEquals(expected, bids.m);
        assertEquals(1000, bids.getMaxPrice());
        assertEquals(1000, bids.getMinPrice());

        bids.removeLesser(10000);
        assertTrue(bids.m.isEmpty());
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.removeLesser(10000);
        assertTrue(bids.m.isEmpty());
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append(100, 8);
        bids.append(1000, 8);
        bids.removeLesser(1000);
        assertEquals(expected, bids.m);
        assertEquals(1000, bids.getMaxPrice());
        assertEquals(1000, bids.getMinPrice());
    }

    @Test
    void testRemoveGreater() {
        BidsSet bids = new BidsSet();

        bids.removeGreater( 0);
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append(1000, 8);
        ShortIntHashMap expected = new ShortIntHashMap(bids.m);

        bids.removeGreater(10000);
        assertEquals(expected, bids.m);
        assertEquals(1000, bids.getMaxPrice());
        assertEquals(1000, bids.getMinPrice());

        bids.removeGreater(1000);
        assertEquals(expected, bids.m);
        assertEquals(1000, bids.getMaxPrice());
        assertEquals(1000, bids.getMinPrice());

        bids.removeGreater(100);
        assertTrue(bids.m.isEmpty());
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.removeGreater(100);
        assertTrue(bids.m.isEmpty());
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append(1000, 8);
        bids.append(10000, 8);
        bids.removeGreater(1000);
        assertEquals(expected, bids.m);
        assertEquals(1000, bids.getMaxPrice());
        assertEquals(1000, bids.getMinPrice());
    }

}