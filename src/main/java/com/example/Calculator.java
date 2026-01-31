package com.example;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Calculator {

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULUS, POWER
    }

    // FIXED: Use EnumMap instead of HashMap for better performance with enum keys
    private final EnumMap<Operation, BiFunction<Integer, Integer, Integer>> operations;

    public Calculator() {
        operations = new EnumMap<>(Operation.class);
        initializeOperations();
    }

    private void initializeOperations() {
        operations.put(Operation.ADD, Integer::sum); // Method reference
        operations.put(Operation.SUBTRACT, (a, b) -> a - b);
        operations.put(Operation.MULTIPLY, (a, b) -> a * b);
        operations.put(Operation.DIVIDE, this::safeDivide);
        operations.put(Operation.MODULUS, (a, b) -> b != 0 ? a % b : 0);
        operations.put(Operation.POWER, this::power);
    }

    public int calculate(int a, int b, Operation op) {
        if (!operations.containsKey(op)) {
            throw new IllegalArgumentException("Invalid operation: " + op);
        }
        return operations.get(op).apply(a, b);
    }

    public int calculate(int a, int b, String opStr) {
        try {
            Operation op = Operation.valueOf(opStr.toUpperCase());
            return calculate(a, b, op);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid operation string: " + opStr, e);
        }
    }

    private int safeDivide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        return a / b;
    }

    private int power(int base, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative");
        }
        // Optimized power calculation using bit operations for powers of 2
        if (base == 2 && exponent < 31) {
            return 1 << exponent;
        }

        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }

    // Helper method to get all supported operations
    public Map<Operation, BiFunction<Integer, Integer, Integer>> getOperations() {
        return new EnumMap<>(operations);
    }
}