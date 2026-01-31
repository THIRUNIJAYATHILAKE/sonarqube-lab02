package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @ParameterizedTest
    @CsvSource({
            "10, 5, ADD, 15",
            "10, 5, SUBTRACT, 5",
            "10, 5, MULTIPLY, 50",
            "10, 5, DIVIDE, 2",
            "10, 3, MODULUS, 1",
            "2, 3, POWER, 8",
            "0, 5, ADD, 5",
            "-10, 5, ADD, -5"
    })
    void testCalculateWithEnum(int a, int b, Calculator.Operation op, int expected) {
        assertEquals(expected, calculator.calculate(a, b, op));
    }

    @ParameterizedTest
    @CsvSource({
            "10, 5, add, 15",
            "10, 5, SUBTRACT, 5",
            "10, 5, MuLtIpLy, 50",
            "10, 5, divide, 2",
            "10, 5, DIVIDE, 2",
            "2, 3, power, 8"
    })
    void testCalculateWithString(int a, int b, String opStr, int expected) {
        assertEquals(expected, calculator.calculate(a, b, opStr));
    }

    @Test
    void testCalculateDivisionByZeroThrowsException() {
        Exception exception = assertThrows(ArithmeticException.class, () -> {
            calculator.calculate(10, 0, Calculator.Operation.DIVIDE);
        });
        assertEquals("Division by zero is not allowed", exception.getMessage());
    }

    @Test
    void testCalculateInvalidOperationEnum() {
        // Create invalid operation (not in enum)
        Calculator.Operation invalidOp = Calculator.Operation.valueOf("ADD"); // Valid
        // We need to test with null or create a test-only invalid operation

        // Test with string instead
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(10, 5, "INVALID_OPERATION");
        });
        assertTrue(exception.getMessage().contains("Invalid operation string"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "add", "SUBTRACT", "multiply", "DIVIDE", "modulus", "POWER" })
    void testIsOperationSupportedWithValidOperations(String opStr) {
        assertTrue(calculator.isOperationSupported(opStr));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "invalid", "ADDITION", "subtraction", "test" })
    void testIsOperationSupportedWithInvalidOperations(String opStr) {
        assertFalse(calculator.isOperationSupported(opStr));
    }

    @Test
    void testCalculateInvalidOperationString() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(10, 5, "invalid");
        });
        assertTrue(exception.getMessage().contains("Invalid operation string"));
    }

    @Test
    void testGetOperationsReturnsEnumMap() {
        Map<Calculator.Operation, ?> operations = calculator.getOperations();
        assertNotNull(operations);
        assertTrue(operations instanceof java.util.EnumMap);
        assertEquals(6, operations.size());
        assertTrue(operations.containsKey(Calculator.Operation.ADD));
        assertTrue(operations.containsKey(Calculator.Operation.DIVIDE));
    }

    @Test
    void testPowerWithNegativeExponentThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(2, -1, Calculator.Operation.POWER);
        });
        assertEquals("Exponent must be non-negative", exception.getMessage());
    }

    @Test
    void testPowerOptimizationForPowerOfTwo() {
        assertEquals(8, calculator.calculate(2, 3, Calculator.Operation.POWER));
        assertEquals(16, calculator.calculate(2, 4, Calculator.Operation.POWER));
        assertEquals(32, calculator.calculate(2, 5, Calculator.Operation.POWER));
        assertEquals(1, calculator.calculate(2, 0, Calculator.Operation.POWER));
    }

    @Test
    void testModulusWithZeroReturnsZero() {
        assertEquals(0, calculator.calculate(10, 0, Calculator.Operation.MODULUS));
        assertEquals(0, calculator.calculate(0, 0, Calculator.Operation.MODULUS));
    }

    @Test
    void testCalculateWithNullOperationString() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            calculator.calculate(10, 5, (String) null);
        });
        // Null will cause NPE in toUpperCase()
    }
}
