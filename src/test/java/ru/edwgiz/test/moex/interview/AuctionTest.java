package ru.edwgiz.test.moex.interview;

import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
            a.sell(3, 101);
            a.buy(1, 100);
            a.buy(2, 101);
            assertDeal(2, 101, a.deal());
        }
        {
            Auction a = new Auction();
            a.sell(3, 101);
            a.sell(3, 100);
            a.buy(1, 100);
            a.buy(2, 101);
            assertDeal(3, 100, a.deal());
        }
        {
            Auction a = new Auction();
            // 200 -> 3 buy, 4 sell
            a.sell(2, 200);
            a.sell(2, 200);
            a.buy(1, 200);
            a.buy(1, 200);
            a.buy(1, 200);

            // 100 -> 1 buy, 99 -> 5 sell
            a.sell(3, 99);
            a.sell(1, 99);
            a.sell(1, 99);
            a.buy(1, 100);
            assertDeal(4, 100, a.deal());
        }
        {
            Auction a = new Auction();
            // 200 -> 3 buy, 4 sell
            a.sell(2, 200);
            a.sell(2, 200);
            a.buy(1, 200);
            a.buy(1, 200);
            a.buy(1, 200);

            // 150 -> 1 buy
            a.buy(10, 150);

            // 99 -> 5 sell
            a.sell(5, 99);
            // 100 -> 1 buy
            a.buy(1, 100);

            assertDeal(5, 100, a.deal());
        }
        {
            Auction a = new Auction();
            // 200 -> 3 buy, 4 sell
            a.sell(2, 200);
            a.sell(2, 200);
            a.buy(1, 200);
            a.buy(1, 200);
            a.buy(1, 200);

            // 99 -> 5 sell
            a.sell(5, 99);
            // 100 -> 1 buy
            a.buy(1, 100);

            // 300 -> 10 buy
            a.buy(10, 300);

            assertDeal(9, 200, a.deal());
        }

        {
            Auction a = new Auction();
            // 50 -> 3 buy, 4 sell
            a.sell(2, 50);
            a.sell(2, 50);
            a.buy(1, 50);
            a.buy(1, 50);
            a.buy(1, 50);

            // 99 -> 5 sell
            a.sell(5, 99);
            // 100 -> 1 buy
            a.buy(1, 100);

            // 300 -> 10 buy
            a.buy(10, 300);

            assertDeal(9, 100, a.deal());
        }
    }

    @Test
    void testMiltipleExtremumDeal() {
        {
            Auction a = new Auction();
            // 50 -> 30 buy, 30 sell
            a.sell(30, 50);
            a.buy(30, 50);

            // 150 -> 30 buy, 30 sell
            a.sell(30, 150);
            a.buy(30, 150);

            a.sell(100, 200); // too high price

            assertDeal(30, 100, a.deal());
        }
        {
            Auction a = new Auction();
            // 50 -> 30 buy, 30 sell
            a.sell(30, 50);
            a.buy(30, 50);

            // 100 -> 7 sell, 30 buy
            a.sell(7, 100);
            a.buy(30, 100);

            // 150 -> 30 buy, 30 sell
            a.sell(30, 150);
            a.buy(30, 153);

            // 200 -> 30 sell, 7 buy
            a.sell(30, 200); // too high price
            a.buy(7, 200); // too high price

            assertDeal(37, 126, a.deal());
        }
    }


    private void assertDeal(int amount, int price, Auction.DealResult deal) {
        assertEquals(amount, deal.getAmount(), "amount");
        assertEquals(price, deal.getPrice(), "price");
    }

    @Test
    void stressTest() throws InterruptedException {
        long nanos;
        for (int n = 0; n < 100; n++) {
            nanos = nanoTime();
            Auction a = new Auction();
            for (int i = 0; i < 1000000; i++) {
                int amount = i % 1000;
                int price = i % 10000;
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

            assertDeal(12173696, 4321, result);
        }
    }
}