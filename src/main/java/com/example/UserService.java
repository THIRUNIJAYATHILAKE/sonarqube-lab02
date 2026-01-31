package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class UserService {

    // FIXED: Remove hardcoded credentials, use configuration/encryption
    private String password;
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    // Constructor to inject configuration
    public UserService(String dbPassword) {
        this.password = dbPassword; // Should come from secure configuration
    }

    // FIXED: Use PreparedStatement to prevent SQL injection
    public boolean findUser(String username) {
        String query = "SELECT * FROM users WHERE name = ?";

        // FIXED: Use try-with-resources to auto-close connections
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/db", "root", password);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            return pstmt.executeQuery().next(); // Returns true if user exists

        } catch (SQLException e) {
            LOGGER.severe("Database error while finding user: " + e.getMessage());
            return false;
        }
    }

    // FIXED: Remove unused method or implement it
    public void logActivity(String activity) {
        LOGGER.info("Activity: " + activity);
    }

    // FIXED: Use PreparedStatement and proper error handling
    public boolean deleteUser(String username) {
        String query = "DELETE FROM users WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/db", "root", password);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.severe("Database error while deleting user: " + e.getMessage());
            return false;
        }
    }

    // Added: Close resource method if needed elsewhere
    public void cleanup() {
        // Any cleanup logic here
        LOGGER.info("Service cleanup completed");
    }
}