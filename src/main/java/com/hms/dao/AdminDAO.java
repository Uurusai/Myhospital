package com.hms.dao;

import com.hms.model.Admin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.hms.dao.DatabaseConnection.getConnection;

public class AdminDAO {

    public boolean addAdmin(Admin admin) {
        String sql = "INSERT INTO admin (admin_id, admin_name, email, contactNo, address) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, admin.getId());
            statement.setString(2, admin.getName());
            statement.setString(3, admin.getEmail());
            statement.setString(4, admin.getContactNo());
            statement.setString(5, admin.getAddress());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Admin getAdminByName(String name) {
        String sql = "SELECT * FROM admin WHERE admin_name = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractAdminFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Admin> searchAdminsByName(String name) {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin WHERE LOWER(admin_name) LIKE LOWER(?)";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    admins.add(extractAdminFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    private Admin extractAdminFromResultSet(ResultSet resultSet) throws SQLException {
        return new Admin(
                resultSet.getString("admin_name"),
                resultSet.getInt("admin_id"),
                resultSet.getString("email"),
                resultSet.getString("contactNo"),
                resultSet.getString("address")
        );
    }
}