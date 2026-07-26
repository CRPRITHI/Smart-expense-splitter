package com.expensesplitter.service;

import com.expensesplitter.model.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Simplifies a set of net balances into the minimum number of payments needed
 * to settle everyone up ("debt simplification" / "optimal account balancing").
 *
 * Algorithm (greedy, using two heaps):
 *   1. Split users into creditors (balance > 0) and debtors (balance < 0).
 *   2. Repeatedly match the biggest creditor with the biggest debtor.
 *   3. The debtor pays the creditor min(|debt|, credit).
 *   4. Push whichever side still has a remaining balance back onto its heap.
 *
 * This does not guarantee the theoretical minimum number of transactions in
 * every case (that variant is NP-hard), but it is the standard, efficient
 * greedy approximation used by real expense-splitting apps and runs in
 * O(n log n) for n participants.
 */
public class SettlementService {

    public List<Transaction> simplifyDebts(Map<Integer, BigDecimal> balances) {
        // Max-heap for creditors (people who are owed money) ordered by amount owed, descending.
        PriorityQueue<Map.Entry<Integer, BigDecimal>> creditors =
                new PriorityQueue<>(Comparator.<Map.Entry<Integer, BigDecimal>, BigDecimal>comparing(Map.Entry::getValue).reversed());

        // Max-heap for debtors ordered by absolute amount owed, descending.
        PriorityQueue<Map.Entry<Integer, BigDecimal>> debtors =
                new PriorityQueue<>(Comparator.<Map.Entry<Integer, BigDecimal>, BigDecimal>comparing(e -> e.getValue().abs()).reversed());

        BigDecimal epsilon = new BigDecimal("0.01");

        for (Map.Entry<Integer, BigDecimal> entry : balances.entrySet()) {
            BigDecimal amount = entry.getValue();
            if (amount.compareTo(epsilon) >= 0) {
                creditors.add(entry);
            } else if (amount.negate().compareTo(epsilon) >= 0) {
                debtors.add(entry);
            }
            // balances within +/- 0.01 are considered already settled
        }

        List<Transaction> transactions = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Map.Entry<Integer, BigDecimal> creditor = creditors.poll();
            Map.Entry<Integer, BigDecimal> debtor = debtors.poll();

            BigDecimal owed = creditor.getValue();
            BigDecimal owes = debtor.getValue().abs();

            BigDecimal settled = owed.min(owes);

           transactions.add(new Transaction(
        debtor.getKey(),
        creditor.getKey(),
        settled.doubleValue()
));

            BigDecimal creditorRemaining = owed.subtract(settled);
            BigDecimal debtorRemaining = owes.subtract(settled);

            if (creditorRemaining.compareTo(epsilon) >= 0) {
                creditors.add(Map.entry(creditor.getKey(), creditorRemaining));
            }
            if (debtorRemaining.compareTo(epsilon) >= 0) {
                debtors.add(Map.entry(debtor.getKey(), debtorRemaining.negate()));
            }
        }

        return transactions;
    }
}
