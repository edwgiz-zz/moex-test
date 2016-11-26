package ru.edwgiz.test.moex.interview;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.iterator.MutableShortIterator;
import com.gs.collections.api.tuple.primitive.ShortShortPair;
import com.gs.collections.impl.map.mutable.primitive.ShortShortHashMap;

import java.util.Map;

import static com.gs.collections.impl.tuple.ImmutableEntry.of;
import static java.lang.Short.MAX_VALUE;
import static java.lang.Short.MIN_VALUE;

/**
 * Bids consolidating by price and amount, there's no separate bids keeping.
 */
final class BidsSet {

    /**
     * The backing map.
     * Its package-private only for unit testing purposes
     */
    final ShortShortHashMap m;
    private short minPrice;
    private short maxPrice;

    BidsSet() {
        m = new ShortShortHashMap();
        minPrice = MAX_VALUE;
        maxPrice = MIN_VALUE;
    }

    short append(short price, short amount) {
        if(price > maxPrice) {
            maxPrice = price;
        }
        if(price < minPrice) {
            minPrice = price;
        }
        return m.addToValue(price, amount);
    }

    short getMinPrice() {
        return minPrice;
    }

    short getMaxPrice() {
        return maxPrice;
    }

    void removeLesser(short priceLimit) {
        if(this.minPrice < priceLimit) {
            short newMinPrice = Short.MAX_VALUE;
            MutableShortIterator it = m.keySet().shortIterator();
            while (it.hasNext()) {
                short price = it.next();
                if (price < priceLimit) {
                    it.remove();
                } else if (newMinPrice > price) {
                    newMinPrice = price;
                }
            }
            this.minPrice = newMinPrice;
            if(m.size() == 0) {
                this.maxPrice = Short.MIN_VALUE;
            }
        }
    }

    void removeGreater(short priceLimit) {
        if(this.maxPrice > priceLimit) {
            short newMaxPrice = Short.MIN_VALUE;
            MutableShortIterator it = m.keySet().shortIterator();
            while (it.hasNext()) {
                short price = it.next();
                if(price > priceLimit) {
                    it.remove();
                } else if (newMaxPrice < price) {
                    newMaxPrice = price;
                }
            }
            this.maxPrice = newMaxPrice;
            if(m.size() == 0) {
                this.minPrice = Short.MAX_VALUE;
            }
        }
    }

    Map.Entry<ShortShortPair[], Integer> toArrayWithToTotalAmount() {
        int size = 0;
        int totalAmount = 0;// int is enough bids count (1e6) multiplied on amount per bid (1e3)
        RichIterable<ShortShortPair> view = m.keyValuesView();
        for (ShortShortPair e : view) {
            size++;
            totalAmount += e.getTwo();
        }
        ShortShortPair[] result = new ShortShortPair[size];
        for (ShortShortPair e : view) {
            result[--size] = e;
        }
        return of(result, totalAmount);
    }

    @Override
    public String toString() {
        return m.toString();
    }
}
