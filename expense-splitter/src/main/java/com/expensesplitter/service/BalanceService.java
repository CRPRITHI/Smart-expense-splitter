package com.expensesplitter.service;

import com.expensesplitter.dao.ExpenseDAO;
import com.expensesplitter.exception.InsufficientDataException;
import com.expensesplitter.model.Expense;
import com.expensesplitter.model.Split;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes net balances for every member of a group.
 *
 * For each expense: the payer's balance goes UP by the full amount (they are
 * owed money), and every participant's balance goes DOWN by their share
 * (they owe money) - including the payer's own share, since they also
 * consumed part of what they paid for.
 *
 * A positive final balance means "this user is owed money overall".
 * A negative final balance means "this user owes money overall".
 */
public class BalanceService {

    private final ExpenseDAO expenseDAO;

    public BalanceService() {
        this.expenseDAO = new ExpenseDAO();
    }

    public BalanceService(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    /**
     * Returns a map of userId -> net balance for the given group.
     * Uses a LinkedHashMap so balances print in a stable, first-seen order.
     */
    public Map<Integer, BigDecimal> calculateGroupBalances(int groupId)
            throws SQLException, InsufficientDataException {

       List<Expense> expenses = expenseDAO.findByGroup(groupId);
        if (expenses.isEmpty()) {
            throw new InsufficientDataException("No expenses recorded for this group yet.");
        }

        Map<Integer, BigDecimal> balances = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            // Payer is credited the full amount they fronted.
            balances.merge(expense.getPaidBy(), expense.getAmount(), BigDecimal::add);

            // Every participant is debited their own share.
            for (Split split : expense.getSplits()) {
                balances.merge(split.getUserId(), split.getShareAmount().negate(), BigDecimal::add);
            }
        }

        // Round every balance to 2 decimal places for display/consistency.
        balances.replaceAll((userId, amount) -> amount.setScale(2, java.math.RoundingMode.HALF_UP));
        return balances;
    }
}
