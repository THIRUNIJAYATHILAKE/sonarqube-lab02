package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private final String password;
    // FIXED: Use SLF4J logger instead of java.util.logging
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(String dbPassword) {
        this.password = dbPassword;
    }

    // FIXED: Don't use SELECT *, specify columns explicitly
    public boolean findUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            LOGGER.warn("Attempted to find user with null or empty username");
            return false;
        }

        // FIXED: Specify exact columns instead of SELECT *
        String query = "SELECT id, name, email FROM users WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/db", "root", password);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean userExists = rs.next();

                // FIXED: Use parameterized logging
                if (userExists) {
                    LOGGER.info("User '{}' found in database", username);
                } else {
                    LOGGER.debug("User '{}' not found in database", username);
                }

                return userExists;
            }

        } catch (SQLException e) {
            // FIXED: Use parameterized logging with exception
            LOGGER.error("Database error while finding user: {}", username, e);
            return false;
        }
    }

    // FIXED: Use parameterized logging
    public void logActivity(String activity) {
        if (activity != null && !activity.trim().isEmpty()) {
            LOGGER.info("Activity logged: {}", activity.trim());
        }
    }

    public boolean deleteUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            LOGGER.warn("Attempted to delete user with null or empty username");
            return false;
        }

        String query = "DELETE FROM users WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/db", "root", password);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username.trim());
            int rowsAffected = pstmt.executeUpdate();

            // FIXED: Use parameterized logging
            if (rowsAffected > 0) {
                LOGGER.info("Successfully deleted user '{}' ({} rows affected)",
                        username, rowsAffected);
                return true;
            } else {
                LOGGER.warn("No user '{}' found to delete", username);
                return false;
            }

        } catch (SQLException e) {
            // FIXED: Use parameterized logging with exception
            LOGGER.error("Database error while deleting user: {}", username, e);
            return false;
        }
    }

    public void cleanup() {
        LOGGER.debug("Service cleanup initiated");
        // Any cleanup logic here
    }
}