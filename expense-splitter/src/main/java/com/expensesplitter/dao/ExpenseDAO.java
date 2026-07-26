package com.expensesplitter.dao;

import com.expensesplitter.db.DBConnection;
import com.expensesplitter.model.Expense;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    // Create Expense
    public Expense create(Expense expense) {

        String sql = "INSERT INTO expenses(group_id, paid_by, description, amount, split_type) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, expense.getGroupId());
ps.setInt(2, expense.getPaidBy());
ps.setString(3, expense.getDescription());
ps.setBigDecimal(4, expense.getAmount());
ps.setString(5, expense.getSplitType().name());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                expense.setId(rs.getInt(1));
            }

            return expense;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get Expense By Id
    public Expense findById(int expenseId) {

        String sql = "SELECT * FROM expenses WHERE expense_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, expenseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Expense(
                        rs.getInt("expense_id"),
                        rs.getString("description"),
                        rs.getBigDecimal("amount"),
                        rs.getInt("paid_by"),
                        rs.getInt("group_id")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get Expenses By Group
    public List<Expense> findByGroup(int groupId) {

        List<Expense> list = new ArrayList<>();

        String sql = "SELECT * FROM expenses WHERE group_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, groupId);

            ResultSet rs = ps.executeQuery();

            SplitDAO splitDAO = new SplitDAO();  

            while (rs.next()) {

                Expense expense = new Expense(
                        rs.getInt("expense_id"),
                        rs.getString("description"),
                        rs.getBigDecimal("amount"),
                        rs.getInt("paid_by"),
                        rs.getInt("group_id")
                );
              
// Load the splits for this expense
expense.setSplits(splitDAO.getSplitsByExpenseId(expense.getId()));

 list.add(expense);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Update Expense
    public boolean updateExpense(Expense expense) {

        String sql = "UPDATE expenses SET description=?, amount=?, paid_by=?, group_id=? WHERE expense_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, expense.getDescription());
            ps.setBigDecimal(2, expense.getAmount());
            ps.setInt(3, expense.getPaidBy());
            ps.setInt(4, expense.getGroupId());
            ps.setInt(5, expense.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete Expense
    public boolean deleteExpense(int expenseId) {

        String sql = "DELETE FROM expenses WHERE expense_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, expenseId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}