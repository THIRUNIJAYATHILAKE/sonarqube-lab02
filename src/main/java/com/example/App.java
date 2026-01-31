package com.example;

import java.util.logging.Logger;
import java.util.logging.Level;

public class App {

    private final UserService userService;
    private final Calculator calculator;
    // Use java.util.logging to avoid requiring SLF4J on the classpath
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public App(UserService userService, Calculator calculator) {
        this.userService = userService;
        this.calculator = calculator;
    }

    public static void main(String[] args) {
        try {
            // Use logger for startup message
            LOGGER.info("Application starting...");

            String dbPassword = System.getenv("DB_PASSWORD");
            if (dbPassword == null || dbPassword.isEmpty()) {
                // Use logger instead of System.err
                LOGGER.warning("DB_PASSWORD environment variable not set, using default");
                dbPassword = "secure-default";
            }

            UserService service = new UserService(dbPassword);
            Calculator calc = new Calculator();
            App app = new App(service, calc);

            app.run();

            LOGGER.info("Application completed successfully");

        } catch (Exception e) {
            // Use logger for errors instead of System.err
            LOGGER.log(Level.SEVERE, "Application error occurred", e);
            System.exit(1);
        }
    }

    private void run() {
        LOGGER.info("=== Application Started ===");

        // Calculator demo
        demonstrateCalculator();

        // UserService demo
        demonstrateUserService();

        LOGGER.info("=== Application Finished ===");
    }

    private void demonstrateCalculator() {
        try {
            int result = calculator.calculate(10, 5, Calculator.Operation.ADD);
            // Use logger instead of System.out
            LOGGER.info("10 + 5 = " + result);

            result = calculator.calculate(10, 5, "divide");
            LOGGER.info("10 / 5 = " + result);

            // Test error case
            try {
                calculator.calculate(10, 0, Calculator.Operation.DIVIDE);
            } catch (ArithmeticException e) {
                LOGGER.warning("Expected error caught: " + e.getMessage());
            }

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid calculation operation", e);
        }
    }

    private void demonstrateUserService() {
        String testUser = "admin";

        if (userService.findUser(testUser)) {
            LOGGER.info(String.format("User '%s' exists in the system", testUser));

            // In a real app, you would have proper user confirmation here
            boolean deleted = userService.deleteUser(testUser);
            LOGGER.info(String.format("Deletion attempt for user '%s': %s", testUser,
                    deleted ? "SUCCESS" : "FAILED"));
        } else {
            LOGGER.info(String.format("User '%s' not found in the system", testUser));
        }

        userService.logActivity("Application demo completed");
    }
}