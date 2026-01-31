package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private final UserService userService;
    private final Calculator calculator;
    // FIXED: Use SLF4J logger instead of System.out/err
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public App(UserService userService, Calculator calculator) {
        this.userService = userService;
        this.calculator = calculator;
    }

    public static void main(String[] args) {
        try {
            // FIXED: Use logger for startup message
            LOGGER.info("Application starting...");

            String dbPassword = System.getenv("DB_PASSWORD");
            if (dbPassword == null || dbPassword.isEmpty()) {
                // FIXED: Use logger instead of System.err
                LOGGER.warn("DB_PASSWORD environment variable not set, using default");
                dbPassword = "secure-default";
            }

            UserService service = new UserService(dbPassword);
            Calculator calc = new Calculator();
            App app = new App(service, calc);

            app.run();

            LOGGER.info("Application completed successfully");

        } catch (Exception e) {
            // FIXED: Use logger for errors instead of System.err
            LOGGER.error("Application error occurred", e);
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
            // FIXED: Use logger instead of System.out
            LOGGER.info("10 + 5 = {}", result);

            result = calculator.calculate(10, 5, "divide");
            LOGGER.info("10 / 5 = {}", result);

            // Test error case
            try {
                calculator.calculate(10, 0, Calculator.Operation.DIVIDE);
            } catch (ArithmeticException e) {
                LOGGER.warn("Expected error caught: {}", e.getMessage());
            }

        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid calculation operation", e);
        }
    }

    private void demonstrateUserService() {
        String testUser = "admin";

        if (userService.findUser(testUser)) {
            LOGGER.info("User '{}' exists in the system", testUser);

            // In a real app, you would have proper user confirmation here
            boolean deleted = userService.deleteUser(testUser);
            LOGGER.info("Deletion attempt for user '{}': {}", testUser,
                    deleted ? "SUCCESS" : "FAILED");
        } else {
            LOGGER.info("User '{}' not found in the system", testUser);
        }

        userService.logActivity("Application demo completed");
    }
}