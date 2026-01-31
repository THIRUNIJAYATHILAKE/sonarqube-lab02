package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {

    private final String password;
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    public UserService(String dbPassword) {
        this.password = dbPassword;
    }

    public boolean findUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            LOGGER.warning("Attempted to find user with null or empty username");
            return false;
        }

        String query = "SELECT id, name, email FROM users WHERE name = ?";

        try (Connection conn = createConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean userExists = rs.next();

                // FIXED: Conditional logging
                if (userExists) {
                    LOGGER.info(String.format("User '%s' found in database", username));
                } else {
                    LOGGER.fine(String.format("User '%s' not found in database", username));
                }

                return userExists;
            }

        } catch (SQLException e) {
            // FIXED: Parameterized logging with conditional exception inclusion
            String errorMessage = String.format("Database error while finding user: %s", username);
            LOGGER.log(Level.SEVERE, errorMessage, e);
            return false;
        }
    }

    // FIXED: Extracted connection creation
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost/db", "root", password);
    }

    // FIXED: Invoke method only conditionally
    public void logActivity(String activity) {
        if (shouldLogActivity(activity)) {
            LOGGER.info(String.format("Activity logged: %s", activity.trim()));
        }
    }

    private boolean shouldLogActivity(String activity) {
        return activity != null && !activity.trim().isEmpty();
    }

    public boolean deleteUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            LOGGER.warning("Attempted to delete user with null or empty username");
            return false;
        }

        String query = "DELETE FROM users WHERE name = ?";

        try (Connection conn = createConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username.trim());
            int rowsAffected = pstmt.executeUpdate();

            // FIXED: Conditional logging based on result
            logDeletionResult(username, rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            logDeletionError(username, e);
            return false;
        }
    }

    // FIXED: Extracted logging methods
    private void logDeletionResult(String username, int rowsAffected) {
        if (rowsAffected > 0) {
            LOGGER.info(String.format("Successfully deleted user '%s' (%d rows affected)", username, rowsAffected));
        } else {
            LOGGER.warning(String.format("No user '%s' found to delete", username));
        }
    }

    private void logDeletionError(String username, SQLException e) {
        String errorDetail = getSqlErrorDetail(e);
        LOGGER.log(Level.SEVERE, String.format("Database error while deleting user '%s': %s", username, errorDetail), e);
    }

    private String getSqlErrorDetail(SQLException e) {
        return String.format("SQL State: %s, Error Code: %d", e.getSQLState(), e.getErrorCode());
    }

    public void cleanup() {
        LOGGER.fine("Service cleanup initiated");
    }
}