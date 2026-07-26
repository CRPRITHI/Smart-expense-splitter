package com.expensesplitter.dao;

import com.expensesplitter.db.DBConnection;
import com.expensesplitter.model.Split;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SplitDAO {

    // Add Split
    public boolean addSplit(Split split) {

        String sql = "INSERT INTO expense_splits(expense_id, user_id,share_amount) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, split.getExpenseId());
            ps.setInt(2, split.getUserId());
           ps.setBigDecimal(3, split.getAmount());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get Splits by Expense ID
    public List<Split> getSplitsByExpenseId(int expenseId) {

        List<Split> splits = new ArrayList<>();

        String sql = "SELECT * FROM expense_splits WHERE expense_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, expenseId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Split split = new Split(
                        rs.getInt("split_id"),
                        rs.getInt("expense_id"),
                        rs.getInt("user_id"),
                      rs.getBigDecimal("share_amount")
                );

                splits.add(split);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return splits;
    }

    // Delete Splits by Expense ID
    public boolean deleteSplitsByExpenseId(int expenseId) {

        String sql = "DELETE FROM expense_splits WHERE expense_id = ?";

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
