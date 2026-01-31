package com.example;

import java.util.logging.Logger;
import java.util.logging.Level;

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
        if (e instanceof RuntimeException runtimeEx) {
            String errorMessage = String.format("Runtime error in application: %s", runtimeEx.getMessage());
            LOGGER.log(Level.SEVERE, errorMessage, runtimeEx);
        } else {
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

    private void performCalculatorOperations() {
        int addResult = calculator.calculate(10, 5, Calculator.Operation.ADD);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "10 + 5 = {0}", addResult);
        }

        int divideResult = calculator.calculate(10, 5, "divide");
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "10 / 5 = {0}", divideResult);
        }

        testDivisionByZero();
    }

    private void testDivisionByZero() {
        try {
            calculator.calculate(10, 0, Calculator.Operation.DIVIDE);
        } catch (ArithmeticException e) {
            String warningMessage = String.format("Expected error caught: %s", e.getMessage());
            LOGGER.warning(warningMessage);
        }
    }

    private void demonstrateUserService() {
        String testUser = "admin";
        boolean userExists = userService.findUser(testUser);

        if (userExists) {
            String userExistsMessage = String.format("User '%s' exists in the system", testUser);
            LOGGER.info(userExistsMessage);

            boolean deleted = userService.deleteUser(testUser);
            String deletionStatus = deleted ? "SUCCESS" : "FAILED";
            String deletionMessage = String.format("Deletion attempt for user '%s': %s", testUser, deletionStatus);
            LOGGER.info(deletionMessage);
        } else {
            String userNotFoundMessage = String.format("User '%s' not found in the system", testUser);
            LOGGER.info(userNotFoundMessage);
        }

        userService.logActivity("Application demo completed");
    }
}