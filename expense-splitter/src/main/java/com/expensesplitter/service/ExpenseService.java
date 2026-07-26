package com.expensesplitter.service;

import com.expensesplitter.dao.ExpenseDAO;
import com.expensesplitter.dao.GroupDAO;
import com.expensesplitter.exception.InvalidSplitException;
import com.expensesplitter.model.Expense;
import com.expensesplitter.dao.SplitDAO;
import com.expensesplitter.model.Split;
import com.expensesplitter.model.SplitType;
import com.expensesplitter.model.User;
import com.expensesplitter.util.InputValidator;
import com.expensesplitter.util.SplitCalculator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Business-logic layer that validates input and coordinates {@link SplitCalculator}
 * with {@link ExpenseDAO} to persist a fully-formed {@link Expense}.
 */
public class ExpenseService {

    private final ExpenseDAO expenseDAO;
    private final GroupDAO groupDAO;

    public ExpenseService() {
        this.expenseDAO = new ExpenseDAO();
        this.groupDAO = new GroupDAO();
    }

    /** Adds an expense split equally among all current members of the group. */
    public Expense addEqualExpense(int groupId, int paidBy, String description, BigDecimal amount)
            throws SQLException, InvalidSplitException {

        validateCommon(amount, description);
       
        List<Integer> memberIds = new ArrayList<>();
        for (User u : groupDAO.getGroupMembers(groupId)) {
    memberIds.add(u.getId());
}

        List<Split> splits = SplitCalculator.splitEqually(amount, memberIds);
        return buildAndSave(groupId, paidBy, description, amount, SplitType.EQUAL, splits);
    }

    /** Adds an expense with an exact amount specified per user. */
    public Expense addExactExpense(int groupId, int paidBy, String description, BigDecimal amount,
                                    Map<Integer, BigDecimal> exactShares) throws SQLException, InvalidSplitException {

        validateCommon(amount, description);
        List<Split> splits = SplitCalculator.splitExact(amount, exactShares);
        return buildAndSave(groupId, paidBy, description, amount, SplitType.EXACT, splits);
    }

    /** Adds an expense with a percentage specified per user. */
    public Expense addPercentExpense(int groupId, int paidBy, String description, BigDecimal amount,
                                      Map<Integer, BigDecimal> percentages) throws SQLException, InvalidSplitException {

        validateCommon(amount, description);
        List<Split> splits = SplitCalculator.splitByPercent(amount, percentages);
        return buildAndSave(groupId, paidBy, description, amount, SplitType.PERCENT, splits);
    }

    private void validateCommon(BigDecimal amount, String description) throws InvalidSplitException {
        if (!InputValidator.isPositiveAmount(amount)) {
            throw new InvalidSplitException("Expense amount must be greater than zero.");
        }
        if (!InputValidator.isNonEmpty(description)) {
            throw new InvalidSplitException("Expense description cannot be empty.");
        }
    }
private Expense buildAndSave(int groupId, int paidBy, String description,
                             BigDecimal amount, SplitType type,
                             List<Split> splits) throws SQLException {

    Expense expense = new Expense(
            description,
            amount,
            paidBy,
            groupId
    );

    // Add these lines
    expense.setSplitType(type);
    expense.setSplits(splits);

expense = expenseDAO.create(expense);

SplitDAO splitDAO = new SplitDAO();

for (Split split : splits) {
    split.setExpenseId(expense.getId());
    splitDAO.addSplit(split);
}
return expenseDAO.create(expense);
}
    public List<Expense> getExpensesForGroup(int groupId) throws SQLException {
    return expenseDAO.findByGroup(groupId);
    }
}
