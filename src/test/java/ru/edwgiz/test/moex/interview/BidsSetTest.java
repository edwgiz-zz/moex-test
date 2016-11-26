package ru.edwgiz.test.moex.interview;

import com.gs.collections.api.map.primitive.MutableShortShortMap;
import com.gs.collections.impl.map.mutable.primitive.ShortShortHashMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BidsSetTest {

    @Test
    void testMinMaxPrice() {
        BidsSet bids = new BidsSet();
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append((short)10, (short)8);
        assertEquals(10, bids.getMinPrice());
        assertEquals(10, bids.getMaxPrice());

        bids.append((short)100, (short)8);
        assertEquals(10, bids.getMinPrice());
        assertEquals(100, bids.getMaxPrice());

        bids.append((short)1, (short)8);
        assertEquals(1, bids.getMinPrice());
        assertEquals(100, bids.getMaxPrice());
    }

    @Test
    void testRemoveLesser() {
        BidsSet bids = new BidsSet();

        bids.removeLesser((short) 0);
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append((short)10, (short)8);
        MutableShortShortMap expected = new ShortShortHashMap(bids.m);

        bids.removeLesser((short)1);
        assertEquals(expected, bids.m);
        assertEquals((short)10, bids.getMaxPrice());
        assertEquals((short)10, bids.getMinPrice());

        bids.removeLesser((short)10);
        assertEquals(expected, bids.m);
        assertEquals((short)10, bids.getMaxPrice());
        assertEquals((short)10, bids.getMinPrice());

        bids.removeLesser((short)100);
        assertTrue(bids.m.isEmpty());
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.removeLesser((short)100);
        assertTrue(bids.m.isEmpty());
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append((short)1, (short)8);
        bids.append((short)10, (short)8);
        bids.removeLesser((short)10);
        assertEquals(expected, bids.m);
        assertEquals((short)10, bids.getMaxPrice());
        assertEquals((short)10, bids.getMinPrice());
    }

    @Test
    void testRemoveGreater() {
        BidsSet bids = new BidsSet();

        bids.removeGreater((short) 0);
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append((short)10, (short)8);
        MutableShortShortMap expected = new ShortShortHashMap(bids.m);

        bids.removeGreater((short)100);
        assertEquals(expected, bids.m);
        assertEquals((short)10, bids.getMaxPrice());
        assertEquals((short)10, bids.getMinPrice());

        bids.removeGreater((short)10);
        assertEquals(expected, bids.m);
        assertEquals((short)10, bids.getMaxPrice());
        assertEquals((short)10, bids.getMinPrice());

        bids.removeGreater((short)1);
        assertTrue(bids.m.isEmpty());
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.removeGreater((short)1);
        assertTrue(bids.m.isEmpty());
        assertEquals(Short.MIN_VALUE, bids.getMaxPrice());
        assertEquals(Short.MAX_VALUE, bids.getMinPrice());

        bids.append((short)10, (short)8);
        bids.append((short)100, (short)8);
        bids.removeGreater((short)10);
        assertEquals(expected, bids.m);
        assertEquals((short)10, bids.getMaxPrice());
        assertEquals((short)10, bids.getMinPrice());
    }

}