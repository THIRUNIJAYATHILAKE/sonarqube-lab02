package com.example;

public class Calculator {

    // Code Smell: Long method + high complexity
    public int calculate(int a, int b, String op) {

        if(op.equals("add")) {
            return a + b;
        }
        if(op.equals("sub")) {
            return a - b;
        }
        if(op.equals("mul")) {
            return a * b;
        }
        if(op.equals("div")) {
            if(b == 0) {
                return 0;
            }
            return a / b;
        }
        if(op.equals("mod")) {
            return a % b;
        }

        return 0;
    }

    // Code Duplication (students must remove)
    public int addNumbers(int x, int y) {
        return x + y;
    }

    public int sumValues(int a, int b) {
        return a + b;
    }
}
