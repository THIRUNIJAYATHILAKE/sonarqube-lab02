package com.example;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AppTest.class,
        CalculatorTest.class,
        UserServiceTest.class
})
public class TestSuite {
    // This class serves as a test suite to run all tests
}