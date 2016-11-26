package ru.edwgiz.test.moex.interview;

import com.gs.collections.api.tuple.primitive.ShortShortPair;
import com.gs.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.Comparator;
import java.util.Map;

import static java.lang.Short.compare;
import static java.lang.StrictMath.floor;
import static java.lang.StrictMath.min;
import static java.util.Arrays.sort;

/**
 * Deals over the given buy and sell bids to maximize total deal amount for optimal price.
 * <p>
 * Class optimized for footprint as well as for CPU consumption.
 * <p>
 * Must not be used after {@link #deal()} method called
 */
public final class Auction {

    private static final Comparator<ShortShortPair> PRICE_COMPARATOR = (o1, o2) -> compare(o1.getOne(), o2.getOne());


    private BidsSet buyBids;
    private BidsSet sellBids;


    public Auction() {
        this.buyBids = new BidsSet();
        this.sellBids = new BidsSet();
    }

    /**
     * Receives buy bid
     *
     * @param amount amount
     * @param price  price
     */
    public void buy(int amount, int price) {
        buyBids.append((short) price, (short) amount);
    }

    /**
     * Receives sell bid
     *
     * @param amount amount
     * @param price  price
     */
    public void sell(int amount, int price) {
        sellBids.append((short) price, (short) amount);
    }

    /**
     * @return {@code null} when no possible deals or successful deal result
     */
    public DealResult deal() {
        removeNonIntersectingBids(); // optimization: exclude unmatched bids from sorting

        if (this.buyBids.getMinPrice() <= buyBids.getMaxPrice()) {

            // get buy bids and their total amount
            ShortShortPair[] buyBids;
            int totalBuyAmount;// int is enough bids count (1e6) multiplied on amount per bid (1e3)
            {
                Map.Entry<ShortShortPair[], Integer> bidsWithToTotalAmount = this.buyBids.toArrayWithToTotalAmount();
                this.buyBids = null;// let out to gc
                buyBids = bidsWithToTotalAmount.getKey();
                totalBuyAmount = bidsWithToTotalAmount.getValue();
            }

            // get sell bids
            ShortShortPair[] sellBids = this.sellBids.toArrayWithToTotalAmount().getKey();
            this.sellBids = null;// let out to gc

            sort(buyBids, PRICE_COMPARATOR);
            sort(sellBids, PRICE_COMPARATOR);

            return deal(sellBids, buyBids, totalBuyAmount);
        }
        return null;
    }

    /**
     * Removes bids that are not intersected by price
     */
    private void removeNonIntersectingBids() {
        // remove the buy bids having lesser prices than the lowest sell price
        buyBids.removeLesser(sellBids.getMinPrice());
        // remove the sell bids having greater prices than the highest buy price
        sellBids.removeGreater(buyBids.getMaxPrice());
    }

    /**
     * Slides over sell and buy bids, finds extrema price during decreasing {@code totalBuyAmount} and increasing
     * {@code totalSellAmount}
     *
     * @param sellBids       sell bids sorted by price
     * @param buyBids        buy bids sorted by price
     * @param totalBuyAmount total amount of buy bids
     * @return successful deal result
     */
    private DealResult deal(ShortShortPair[] sellBids, ShortShortPair[] buyBids, int totalBuyAmount) {
        IntHashSet extremaPrices = new IntHashSet();
        int totalSellAmount = 0;
        int maxDealAmount = 0;
        int buyIndex = 0;
        int sellIndex = 0;
        int buyPrice = buyBids[buyIndex].getOne();
        int sellPrice = 0;
        BOTH_BIDS_SLIDING:
        for (; ; ) {// slide over sell and buy bids
            for (; ; ) {
                ShortShortPair sell = sellBids[sellIndex];
                if (sell.getOne() <= buyPrice) {
                    // summarize sell bids until buy price will be reached
                    totalSellAmount += sell.getTwo();
                    sellPrice = sell.getOne();
                    if (++sellIndex < sellBids.length) {
                        continue;
                    }
                }
                // sell bids are summarized
                int dealAmount = min(totalSellAmount, totalBuyAmount);
                if (maxDealAmount <= dealAmount) {
                    if (maxDealAmount < dealAmount) {
                        extremaPrices.clear();
                    }
                    extremaPrices.add((sellPrice + buyPrice + 1) / 2);
                    maxDealAmount = dealAmount;
                }
                sellPrice = sell.getOne(); // set limit the next summarizing of buy bids
                if (sellIndex == sellBids.length) {
                    break BOTH_BIDS_SLIDING;
                }
                break;
            }

            for (; ; ) {// summarize buy bids until sell price will be reached
                ShortShortPair buy = buyBids[buyIndex];
                buyPrice = buy.getOne();// set limit for next summarizing of sell bids
                if (buyPrice >= sellPrice) {
                    break;
                }
                totalBuyAmount -= buy.getTwo();
                buyIndex++;
            }
        }
        return new DealResult((int) floor(extremaPrices.average()), maxDealAmount);
    }

    public static final class DealResult {
        private final int price;
        private final int amount;

        public DealResult(int price, int amount) {
            this.price = price;
            this.amount = amount;
        }

        public int getPrice() {
            return price;
        }

        public int getAmount() {
            return amount;
        }
    }
}
