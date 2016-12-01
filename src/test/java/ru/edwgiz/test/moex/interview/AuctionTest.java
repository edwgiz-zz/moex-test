package ru.edwgiz.test.moex.interview;

import com.gs.collections.api.block.procedure.primitive.IntIntProcedure;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static org.junit.jupiter.api.Assertions.*;

class AuctionTest {

    @Test
    void testNoDeal() {
        {
            Auction a = new Auction();
            a.buy(100, 1000);
            a.sell(150, 1010);
            assertNull(a.deal());
        }
        {
            Auction a = new Auction();
            a.buy(10, 800);
            assertNull(a.deal());
        }
        {
            Auction a = new Auction();
            a.buy(100, 700);
            a.buy(10, 800);
            assertNull(a.deal());
        }
        {
            Auction a = new Auction();
            a.sell(30, 5300);
            assertNull(a.deal());
        }
        {
            Auction a = new Auction();
            a.sell(30, 5300);
            a.sell(30, 5300);
            a.sell(10, 5200);
            assertNull(a.deal());
        }
    }

    @Test
    void testOnePriceDeal() {
        {
            Auction a = new Auction();
            a.buy(10, 5300);
            a.sell(30, 5300);
            assertDeal(10, 5300, a.deal());
        }
        {
            Auction a = new Auction();
            a.buy(10, 5300);
            a.buy(20, 5300);
            a.sell(30, 5300);
            assertDeal(30, 5300, a.deal());
        }
        {
            Auction a = new Auction();
            a.buy(10, 5300);
            a.buy(20, 5300);
            a.buy(30, 5300);
            a.sell(30, 5300);
            a.sell(10, 5300);
            assertDeal(40, 5300, a.deal());
        }
    }

    @Test
    void testComplexDeal() {
        {
            Auction a = new Auction();
            a.sell(3, 1001);
            a.buy(1, 1000);
            a.buy(2, 1001);
            assertDeal(2, 1001, a.deal());
        }
        {
            Auction a = new Auction();
            a.sell(3, 1001);
            a.sell(3, 1000);
            a.buy(1, 1000);
            a.buy(2, 1001);
            assertDeal(3, 1000, a.deal());
        }
        {
            Auction a = new Auction();
            // 200 -> 3 buy, 4 sell
            a.sell(2, 2000);
            a.sell(2, 2000);
            a.buy(1, 2000);
            a.buy(1, 2000);
            a.buy(1, 2000);

            // 100 -> 1 buy, 99 -> 5 sell
            a.sell(3, 999);
            a.sell(1, 999);
            a.sell(1, 999);
            a.buy(1, 1000);
            assertDeal(4, 1000, a.deal());
        }
        {
            Auction a = new Auction();
            // 200 -> 3 buy, 4 sell
            a.sell(2, 2000);
            a.sell(2, 2000);
            a.buy(1, 2000);
            a.buy(1, 2000);
            a.buy(1, 2000);

            // 150 -> 1 buy
            a.buy(10, 1500);

            // 99 -> 5 sell
            a.sell(5, 999);
            // 100 -> 1 buy
            a.buy(1, 1000);

            assertDeal(5, 1000, a.deal());
        }
        {
            Auction a = new Auction();
            // 200 -> 3 buy, 4 sell
            a.sell(2, 2000);
            a.sell(2, 2000);
            a.buy(1, 2000);
            a.buy(1, 2000);
            a.buy(1, 2000);

            // 99 -> 5 sell
            a.sell(5, 999);
            // 100 -> 1 buy
            a.buy(1, 1000);

            // 300 -> 10 buy
            a.buy(10, 3000);

            assertDeal(9, 2000, a.deal());
        }

        {
            Auction a = new Auction();
            // 50 -> 3 buy, 4 sell
            a.sell(2, 500);
            a.sell(2, 500);
            a.buy(1, 500);
            a.buy(1, 500);
            a.buy(1, 500);

            // 99 -> 5 sell
            a.sell(5, 999);
            // 100 -> 1 buy
            a.buy(1, 1000);

            // 300 -> 10 buy
            a.buy(10, 3000);

            assertDeal(9, 1000, a.deal());
        }
    }

    @Test
    void testMiltipleExtremumDeal() {
        {
            Auction a = new Auction();
            // 50 -> 30 buy, 30 sell
            a.sell(30, 500);
            a.buy(30, 500);

            // 150 -> 30 buy, 30 sell
            a.sell(30, 1500);
            a.buy(30, 1500);

            a.sell(100, 2000); // too high price

            assertDeal(30, 1000, a.deal());
        }
        {
            Auction a = new Auction();
            // 50 -> 30 buy, 30 sell
            a.sell(30, 500);
            a.buy(30, 500);

            // 100 -> 7 sell, 30 buy
            a.sell(7, 1000);
            a.buy(30, 1000);

            // 150 -> 30 buy, 30 sell
            a.sell(30, 1500);
            a.buy(30, 1538);

            // 200 -> 30 sell, 7 buy
            a.sell(30, 2000); // too high price
            a.buy(7, 2000); // too high price

            assertDeal(37, 1259, a.deal());
        }
    }


    private void assertDeal(int amount, int price, Auction.DealResult deal) {
        assertEquals(amount, deal.getAmount(), "amount");
        assertEquals(price, deal.getPrice(), "price");
    }

    @Test
    void stressTest() throws InterruptedException {
        for (int n = 0; n < 30; n++) {
            long nanos = nanoTime();
            Auction a = new Auction();
            for (int i = 0; i < 1000000; i++) {
                int amount = i % 1000;// between 0 and 1000
                int price = 100 + i % 9900;// between 1.00 and 100.00
                if (amount != 0 && price != 0) {
                    if ((i % 2) == 0) {
                        a.sell(amount, price);
                    } else {
                        a.buy(amount, price);
                    }
                }
            }
            System.out.print(format("fill/calc at  %1$g", (nanoTime() - nanos) * 1e-6));
            nanos = nanoTime();
            Auction.DealResult result = a.deal();
            System.out.println(format(" / %1$g  ms", (nanoTime() - nanos) * 1e-6));

            assertDeal(124885616, 5057, result);
        }
    }

    @Test
    void overflowTest() throws InterruptedException {
        int maxPrice = 10000; // aka 100.00
        int maxAmount = 1000; // aka 1000
        int maxBids = 1000000;
        {
            Auction a = new Auction();
            for (int i = 0; i < 1000000; i++) {
                if ((i % 2) == 0) {
                    a.sell(maxAmount, maxPrice);
                } else {
                    a.buy(maxAmount, maxPrice);
                }
            }
            long expectedAmount = (long) maxBids / 2L * maxAmount;
            assertTrue(expectedAmount < Integer.MAX_VALUE);
            assertDeal((int) expectedAmount, maxPrice, a.deal());
        }
        {
            Auction a = new Auction();
            overflowTest(maxPrice, maxAmount, a, a::buy, a::sell);
        }
        {
            Auction a = new Auction();
            overflowTest(maxPrice, maxAmount, a, a::sell, a::buy);
        }
    }

    private void overflowTest(int maxPrice, int maxAmount, Auction a,
                              IntIntProcedure toOverflowTestOperation,
                              IntIntProcedure toDealSupportOperation) {
        toDealSupportOperation.value(maxAmount, maxPrice);
        for (int i = 1; i < 1000000; i++) {
            toOverflowTestOperation.value(maxAmount, maxPrice);
        }
        assertDeal(maxAmount, maxPrice, a.deal());
    }


}