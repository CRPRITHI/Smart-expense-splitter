package com.expensesplitter.dao;

import com.expensesplitter.model.Group;
import com.expensesplitter.model.User;
import com.expensesplitter.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class GroupDAO {

    // Create Group
    public boolean createGroup(Group group) {

        String sql = "INSERT INTO expense_groups(group_name) VALUES(?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, group.getGroupName());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Add Member
    public boolean addMember(int groupId, int userId) {

        String sql = "INSERT INTO group_members(group_id, user_id) VALUES(?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, groupId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Remove Member
    public boolean removeMember(int groupId, int userId) {

        String sql = "DELETE FROM group_members WHERE group_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, groupId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    

    // Get Group By ID
    public Group getGroupById(int groupId) {

        String sql = "SELECT * FROM expense_groups WHERE group_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, groupId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Group(
                        rs.getInt("group_id"),
                        rs.getString("group_name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get All Groups
    public List<Group> getAllGroups() {

        List<Group> groups = new ArrayList<>();

        String sql = "SELECT * FROM expense_groups";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Group group = new Group(
                        rs.getInt("group_id"),
                        rs.getString("group_name")
                );

                groups.add(group);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

    // Get Group Members
    public List<User> getGroupMembers(int groupId) {

        List<User> members = new ArrayList<>();

        String sql = "SELECT u.* FROM users u " +
                     "JOIN group_members gm ON u.user_id = gm.user_id " +
                     "WHERE gm.group_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, groupId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                );

                members.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }
    // Update Group
    public boolean updateGroup(Group group) {

        String sql = "UPDATE expense_groups SET group_name = ? WHERE group_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, group.getGroupName());
            ps.setInt(2, group.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete Group
    public boolean deleteGroup(int groupId) {

    String deleteMembers = 
            "DELETE FROM group_members WHERE group_id=?";

    String deleteGroup = 
            "DELETE FROM expense_groups WHERE group_id=?";

    try(Connection con = DBConnection.getConnection()) {

        PreparedStatement ps1 = con.prepareStatement(deleteMembers);
        ps1.setInt(1, groupId);
        ps1.executeUpdate();

        PreparedStatement ps2 = con.prepareStatement(deleteGroup);
        ps2.setInt(1, groupId);

        return ps2.executeUpdate() > 0;

    } catch(SQLException e) {
        e.printStackTrace();
    }

    return false;
}

}