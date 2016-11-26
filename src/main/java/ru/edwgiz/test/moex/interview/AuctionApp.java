package ru.edwgiz.test.moex.interview;


import com.gs.collections.api.block.procedure.primitive.IntIntProcedure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_UP;

public final class AuctionApp {

    private static final Pattern SPACE_SPLITTING_PATTERN = Pattern.compile("\\s+");
    private static final BigDecimal HUNDRED = new BigDecimal(100);

    public static void main(String[] args) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        Auction a = null;
        String l;
        while ((l = r.readLine()) != null) {
            l = l.trim();
            try {
                if (Objects.equals("INPUT:", l)) {
                    a = new Auction();
                    continue;
                } else if (a != null) {
                    if (Objects.equals("OUTPUT:", l)) {
                        Auction.DealResult deal = a.deal();
                        a = null;
                        if (deal == null) {
                            out.println("0 n/a");
                        } else {
                            out.print(deal.getAmount());
                            out.print(' ');
                            BigDecimal price = new BigDecimal(deal.getPrice()).setScale(2, HALF_UP).divide(HUNDRED, HALF_UP);
                            out.println(price);
                        }
                        out.println();
                        continue;
                    } else {
                        switch (l.charAt(0)) {
                            case 'B':
                                onBidRow(l, a::buy);
                                continue;
                            case 'S':
                                onBidRow(l, a::sell);
                                continue;
                        }
                    }
                }
                err.println("Unexpected line: '" + l + '\'');
            } catch (ParseException ex) {
                err.println(ex.getMessage());
            }
            out.println("Try again");
            out.println();
            a = null;
        }
    }

    private static void onBidRow(String l, IntIntProcedure consumer) throws ParseException {

        String[] values = SPACE_SPLITTING_PATTERN.split(l);
        if(values.length == 3) {
            int amount = parseInt(values[1]);
            BigDecimal price = new BigDecimal(values[2]);
            if(amount > 0 && amount <= 1000
                    && ONE.compareTo(price) < 0 && HUNDRED.compareTo(price) >= 0) {

                consumer.value(amount, price.multiply(HUNDRED).intValue());
                return;
            }
        }
        throw new ParseException("Unexpected bid line format: '" + l + '\'', 0);
    }
}
