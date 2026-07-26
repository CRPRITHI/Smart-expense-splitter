package com.expensesplitter.util;

import com.expensesplitter.exception.InvalidSplitException;
import com.expensesplitter.model.Split;
import com.expensesplitter.model.SplitType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains the logic that turns a total expense amount into individual
 * {@link Split} entries, depending on the chosen {@link SplitType}.
 */
public final class SplitCalculator {

    private SplitCalculator() {
    }

    /**
     * EQUAL split: divide the amount evenly. Any leftover paise/cents caused by
     * rounding are distributed one unit at a time to the first few participants
     * so the splits always sum exactly to the original amount.
     */
    public static List<Split> splitEqually(BigDecimal amount, List<Integer> userIds) throws InvalidSplitException {
        if (userIds == null || userIds.isEmpty()) {
            throw new InvalidSplitException("Cannot split an expense among zero participants.");
        }

        int n = userIds.size();
        BigDecimal base = amount.setScale(2, RoundingMode.DOWN)
                .divide(BigDecimal.valueOf(n), 2, RoundingMode.DOWN);

        BigDecimal distributed = base.multiply(BigDecimal.valueOf(n));
        BigDecimal remainder = amount.setScale(2, RoundingMode.HALF_UP).subtract(distributed);
        // remainder is a small amount like 0.03 left after equal division; hand
        // out one extra cent/paisa to the first `remainderUnits` participants.
        BigDecimal cent = new BigDecimal("0.01");
        int remainderUnits = remainder.divide(cent, 0, RoundingMode.HALF_UP).intValue();

        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            BigDecimal share = base;
            if (i < remainderUnits) {
                share = share.add(cent);
            }
            splits.add(new Split(userIds.get(i), share));
        }
        return splits;
    }

    /**
     * EXACT split: caller supplies the exact share for every user.
     * The shares must add up to the total amount (validated to the cent).
     */
    public static List<Split> splitExact(BigDecimal amount, Map<Integer, BigDecimal> exactShares)
            throws InvalidSplitException {
        if (exactShares == null || exactShares.isEmpty()) {
            throw new InvalidSplitException("Exact shares cannot be empty.");
        }

        BigDecimal sum = BigDecimal.ZERO;
        List<Split> splits = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : exactShares.entrySet()) {
            sum = sum.add(entry.getValue());
            splits.add(new Split(entry.getKey(), entry.getValue().setScale(2, RoundingMode.HALF_UP)));
        }

        if (sum.setScale(2, RoundingMode.HALF_UP).compareTo(amount.setScale(2, RoundingMode.HALF_UP)) != 0) {
            throw new InvalidSplitException(
                    String.format("Exact shares (%.2f) do not add up to the expense amount (%.2f).", sum, amount));
        }
        return splits;
    }

    /**
     * PERCENT split: caller supplies a percentage (0-100) for every user.
     * Percentages must add up to 100.
     */
    public static List<Split> splitByPercent(BigDecimal amount, Map<Integer, BigDecimal> percentages)
            throws InvalidSplitException {
        if (percentages == null || percentages.isEmpty()) {
            throw new InvalidSplitException("Percentages cannot be empty.");
        }

        BigDecimal totalPercent = BigDecimal.ZERO;
        for (BigDecimal pct : percentages.values()) {
            totalPercent = totalPercent.add(pct);
        }
        if (totalPercent.setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal("100.00")) != 0) {
            throw new InvalidSplitException(
                    String.format("Percentages must add up to 100 (got %.2f).", totalPercent));
        }

        List<Split> splits = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : percentages.entrySet()) {
            BigDecimal share = amount.multiply(entry.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            splits.add(new Split(entry.getKey(), share));
        }
        return splits;
    }
}
