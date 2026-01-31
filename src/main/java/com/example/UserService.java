package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {

    private final String password;
    // Using java.util.logging with a small adapter to avoid external SLF4J
    // dependency
    private static final Logger JUL = Logger.getLogger(UserService.class.getName());
    private static final SimpleLogger LOGGER = new SimpleLogger(JUL);

    private static final class SimpleLogger {
        private final Logger jul;

        SimpleLogger(Logger jul) {
            this.jul = jul;
        }

        void info(String msg, Object... args) {
            jul.log(Level.INFO, format(msg, args));
        }

        void warn(String msg, Object... args) {
            jul.log(Level.WARNING, format(msg, args));
        }

        void debug(String msg, Object... args) {
            jul.log(Level.FINE, format(msg, args));
        }

        void error(String msg, Object... args) {
            if (args != null && args.length > 0 && args[args.length - 1] instanceof Throwable) {
                Throwable t = (Throwable) args[args.length - 1];
                Object[] trimmed = Arrays.copyOf(args, args.length - 1);
                jul.log(Level.SEVERE, format(msg, trimmed), t);
            } else {
                jul.log(Level.SEVERE, format(msg, args));
            }
        }

        private String format(String template, Object... args) {
            if (template == null)
                return null;
            String fmt = template.replace("{}", "%s");
            try {
                return String.format(fmt, args);
            } catch (Exception e) {
                return fmt;
            }
        }
    }

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