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

                // FIXED: Log messages built outside of condition
                if (userExists) {
                    String foundMessage = String.format("User '%s' found in database", username);
                    LOGGER.info(foundMessage);
                } else {
                    String notFoundMessage = String.format("User '%s' not found in database", username);
                    LOGGER.fine(notFoundMessage);
                }

                return userExists;
            }

        } catch (SQLException e) {
            String errorMessage = String.format("Database error while finding user: %s", username);
            LOGGER.log(Level.SEVERE, errorMessage, e);
            return false;
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost/db", "root", password);
    }

    public void logActivity(String activity) {
        if (activity != null && !activity.trim().isEmpty()) {
            String activityMessage = String.format("Activity logged: %s", activity.trim());
            LOGGER.info(activityMessage);
        }
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

            logDeletionResult(username, rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            logDeletionError(username, e);
            return false;
        }
    }

    private void logDeletionResult(String username, int rowsAffected) {
        if (rowsAffected > 0) {
            String successMessage = String.format("Successfully deleted user '%s' (%d rows affected)", username,
                    rowsAffected);
            LOGGER.info(successMessage);
        } else {
            String warningMessage = String.format("No user '%s' found to delete", username);
            LOGGER.warning(warningMessage);
        }
    }

    private void logDeletionError(String username, SQLException e) {
        String errorDetail = getSqlErrorDetail(e);
        String errorMessage = String.format("Database error while deleting user '%s': %s", username, errorDetail);
        LOGGER.log(Level.SEVERE, errorMessage, e);
    }

    private String getSqlErrorDetail(SQLException e) {
        return String.format("SQL State: %s, Error Code: %d", e.getSQLState(), e.getErrorCode());
    }

    public void cleanup() {
        LOGGER.fine("Service cleanup initiated");
    }
}