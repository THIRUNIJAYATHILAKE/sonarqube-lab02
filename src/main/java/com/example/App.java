package com.example;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private final UserService userService;
    private final Calculator calculator;
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public App(UserService userService, Calculator calculator) {
        this.userService = userService;
        this.calculator = calculator;
    }

    public static void main(String[] args) {
        LOGGER.info("Application starting...");

        try {
            String dbPassword = getDatabasePassword();
            UserService service = new UserService(dbPassword);
            Calculator calc = new Calculator();
            App app = new App(service, calc);

            app.run();

            LOGGER.info("Application completed successfully");

        } catch (Exception e) {
            logApplicationError(e);
            System.exit(1);
        }
    }

    private static String getDatabasePassword() {
        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword == null || dbPassword.isEmpty()) {
            LOGGER.warning("DB_PASSWORD environment variable not set, using default");
            return "secure-default";
        }
        return dbPassword;
    }

    private static void logApplicationError(Exception e) {
        // Use traditional instanceof checks to maintain compatibility
        if (e instanceof RuntimeException) {
            RuntimeException runtimeEx = (RuntimeException) e;
            LOGGER.log(Level.SEVERE, String.format("Runtime error in application: %s", runtimeEx.getMessage()),
                    runtimeEx);
        } else if (e instanceof Exception) {
            LOGGER.log(Level.SEVERE, "Application error occurred", e);
        }
    }

    private void run() {
        LOGGER.info("=== Application Started ===");

        demonstrateCalculator();
        demonstrateUserService();

        LOGGER.info("=== Application Finished ===");
    }

    private void demonstrateCalculator() {
        try {
            performCalculatorOperations();
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid calculation operation", e);
        }
    }

    // FIXED: Extracted nested try block into separate method
    private void performCalculatorOperations() {
        int result = calculator.calculate(10, 5, Calculator.Operation.ADD);
        LOGGER.info(String.format("10 + 5 = %d", result));

        result = calculator.calculate(10, 5, "divide");
        LOGGER.info(String.format("10 / 5 = %d", result));

        testDivisionByZero();
    }

    private void testDivisionByZero() {
        try {
            calculator.calculate(10, 0, Calculator.Operation.DIVIDE);
        } catch (ArithmeticException e) {
            LOGGER.warning(String.format("Expected error caught: %s", e.getMessage()));
        }
    }

    private void demonstrateUserService() {
        String testUser = "admin";

        if (userService.findUser(testUser)) {
            LOGGER.info(String.format("User '%s' exists in the system", testUser));

            // FIXED: Conditional logging (only log if condition is met)
            boolean deleted = userService.deleteUser(testUser);
            String deletionStatus = deleted ? "SUCCESS" : "FAILED";
            LOGGER.info(String.format("Deletion attempt for user '%s': %s", testUser, deletionStatus));
        } else {
            LOGGER.info(String.format("User '%s' not found in the system", testUser));
        }

        userService.logActivity("Application demo completed");
    }
}