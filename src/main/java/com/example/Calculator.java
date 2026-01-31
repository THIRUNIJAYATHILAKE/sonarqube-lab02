package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Calculator {

    // FIXED: Use enum for operations instead of string
    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULUS, POWER
    }

    // FIXED: Use Map to avoid code duplication and long if-else chains
    private final Map<Operation, BiFunction<Integer, Integer, Integer>> operations = new HashMap<>();

    public Calculator() {
        initializeOperations();
    }

    private void initializeOperations() {
        operations.put(Operation.ADD, (a, b) -> a + b);
        operations.put(Operation.SUBTRACT, (a, b) -> a - b);
        operations.put(Operation.MULTIPLY, (a, b) -> a * b);
        operations.put(Operation.DIVIDE, this::safeDivide);
        operations.put(Operation.MODULUS, (a, b) -> b != 0 ? a % b : 0);
        operations.put(Operation.POWER, this::power);
    }

    // FIXED: Main calculation method with proper validation
    public int calculate(int a, int b, Operation op) {
        if (!operations.containsKey(op)) {
            throw new IllegalArgumentException("Invalid operation: " + op);
        }
        return operations.get(op).apply(a, b);
    }

    // FIXED: Overloaded method for backward compatibility
    public int calculate(int a, int b, String opStr) {
        try {
            Operation op = Operation.valueOf(opStr.toUpperCase());
            return calculate(a, b, op);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid operation string: " + opStr, e);
        }
    }

    // FIXED: Safe division with proper error handling
    private int safeDivide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        return a / b;
    }

    // FIXED: Optimized power calculation
    private int power(int base, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative");
        }
        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }

    // REMOVED: Duplicate method
    // public int addAgain(int a, int b) was removed as it duplicated functionality
}