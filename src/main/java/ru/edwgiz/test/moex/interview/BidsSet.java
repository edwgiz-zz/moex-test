package ru.edwgiz.test.moex.interview;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.iterator.MutableShortIterator;
import com.gs.collections.api.tuple.primitive.ShortIntPair;
import com.gs.collections.impl.map.mutable.primitive.ShortIntHashMap;

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
    final ShortIntHashMap m;
    private int minPrice;
    private int maxPrice;

    BidsSet() {
        m = new ShortIntHashMap();
        minPrice = MAX_VALUE;
        maxPrice = MIN_VALUE;
    }

    int append(int price, int amount) {
        if (100 > price || price > 10000) {
            throw new IllegalArgumentException("Price must be between 1.00 and 100.00");
        }
        if (0 > amount || amount > 1000) {
            throw new IllegalArgumentException("Amount must be between 0 and 1000");
        }

        if(price > maxPrice) {
            maxPrice = price;
        }
        if(price < minPrice) {
            minPrice = price;
        }

        if(amount > 0) {
            return m.addToValue((short) price, amount);
        } else {
            return 0;
        }
    }

    int getMinPrice() {
        return minPrice;
    }

    int getMaxPrice() {
        return maxPrice;
    }

    boolean isEmpty() {
        return minPrice > maxPrice;
    }

    void removeLesser(int priceLimit) {
        if(this.minPrice < priceLimit) {
            int newMinPrice = Short.MAX_VALUE;
            MutableShortIterator it = m.keySet().shortIterator();
            while (it.hasNext()) {
                int price = it.next();
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

    void removeGreater(int priceLimit) {
        if(this.maxPrice > priceLimit) {
            int newMaxPrice = Short.MIN_VALUE;
            MutableShortIterator it = m.keySet().shortIterator();
            while (it.hasNext()) {
                int price = it.next();
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

    Map.Entry<ShortIntPair[], Integer> toArrayWithToTotalAmount() {
        int size = 0;
        int totalAmount = 0;// int is enough bids count (1e6) multiplied on amount per bid (1e3)
        RichIterable<ShortIntPair> view = m.keyValuesView();
        for (ShortIntPair e : view) {
            size++;
            totalAmount += e.getTwo();
        }
        ShortIntPair[] result = new ShortIntPair[size];
        for (ShortIntPair e : view) {
            result[--size] = e;
        }
        return of(result, totalAmount);
    }

    @Override
    public String toString() {
        return m.toString();
    }
}
