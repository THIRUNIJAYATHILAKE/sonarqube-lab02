package com.example;

import java.util.Scanner;

public class App {

    // FIXED: Use dependency injection for service
    private final UserService userService;
    private final Calculator calculator;

    public App(UserService userService, Calculator calculator) {
        this.userService = userService;
        this.calculator = calculator;
    }

    public static void main(String[] args) {
        try {
            // FIXED: Get password from secure source (in real app: config file, env var,
            // etc.)
            String dbPassword = System.getenv("DB_PASSWORD");
            if (dbPassword == null || dbPassword.isEmpty()) {
                dbPassword = "secure-default"; // In production, fail if not configured
            }

            UserService service = new UserService(dbPassword);
            Calculator calc = new Calculator();
            App app = new App(service, calc);

            app.run();

        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void run() {
        System.out.println("=== Application Started ===");

        // Calculator demo
        try {
            int result = calculator.calculate(10, 5, Calculator.Operation.ADD);
            System.out.println("10 + 5 = " + result);

            // Example with string (for backward compatibility)
            result = calculator.calculate(10, 5, "divide");
            System.out.println("10 / 5 = " + result);

        } catch (ArithmeticException e) {
            System.out.println("Calculation error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid operation: " + e.getMessage());
        }

        // UserService demo
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("\nEnter username to check: ");
            String username = scanner.nextLine().trim();

            if (userService.findUser(username)) {
                System.out.println("User '" + username + "' exists.");

                // Ask for deletion (with confirmation in real app)
                System.out.print("Delete user? (yes/no): ");
                String response = scanner.nextLine().trim();

                if ("yes".equalsIgnoreCase(response)) {
                    boolean deleted = userService.deleteUser(username);
                    System.out.println("Deletion " + (deleted ? "successful" : "failed"));
                }
            } else {
                System.out.println("User '" + username + "' not found.");
            }

            userService.logActivity("Application completed successfully");

        } catch (Exception e) {
            System.err.println("Runtime error: " + e.getMessage());
        }

        System.out.println("\n=== Application Finished ===");
    }
}