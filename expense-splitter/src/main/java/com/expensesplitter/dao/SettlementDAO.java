package com.expensesplitter.dao;

import com.expensesplitter.model.Settlement;
import com.expensesplitter.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
  import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class SettlementDAO {

    // Add Settlement
    public boolean addSettlement(Settlement settlement) {

        String sql = "INSERT INTO settlements(payer_id, receiver_id, amount) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, settlement.getPayerId());
            ps.setInt(2, settlement.getReceiverId());
            ps.setDouble(3, settlement.getAmount());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get Settlement By ID
    public Settlement getSettlementById(int settlementId) {

        String sql = "SELECT * FROM settlements WHERE settlement_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, settlementId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Settlement(
                        rs.getInt("settlement_id"),
                        rs.getInt("payer_id"),
                        rs.getInt("receiver_id"),
                        rs.getDouble("amount")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
  

    // Get All Settlements
    public List<Settlement> getAllSettlements() {

        List<Settlement> settlements = new ArrayList<>();

        String sql = "SELECT * FROM settlements";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Settlement settlement = new Settlement(
                        rs.getInt("settlement_id"),
                        rs.getInt("payer_id"),
                        rs.getInt("receiver_id"),
                        rs.getDouble("amount")
                );

                settlements.add(settlement);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return settlements;
    }
        // Update Settlement
    public boolean updateSettlement(Settlement settlement) {

        String sql = "UPDATE settlements SET payer_id = ?, receiver_id = ?, amount = ? WHERE settlement_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, settlement.getPayerId());
            ps.setInt(2, settlement.getReceiverId());
            ps.setDouble(3, settlement.getAmount());
            ps.setInt(4, settlement.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete Settlement
    public boolean deleteSettlement(int settlementId) {

        String sql = "DELETE FROM settlements WHERE settlement_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, settlementId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}