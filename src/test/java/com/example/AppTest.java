package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppTest {

    @Mock
    private UserService mockUserService;

    @Mock
    private Calculator mockCalculator;

    private App app;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        app = new App(mockUserService, mockCalculator);
        setupLogCapture();
    }

    private void setupLogCapture() {
        Logger logger = (Logger) LoggerFactory.getLogger(App.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testConstructor() {
        assertNotNull(app);
    }

    @Test
    void testMainMethodWithValidExecution() {
        // Test that main method doesn't crash
        // This is a basic smoke test
        assertDoesNotThrow(() -> {
            // We can't easily test main without refactoring
            // So we test the components instead
            App.main(new String[] {});
        });
    }

    @Test
    void testGetDatabasePasswordReturnsDefaultWhenEnvNotSet() {
        // Simulate environment variable not set
        // Note: In real tests, use System Lambda or mock environment
        String result = invokePrivateGetDatabasePassword();
        assertNotNull(result);
        assertEquals("secure-default", result);
    }

    @Test
    void testDemonstrateCalculatorWithValidOperations() {
        when(mockCalculator.calculate(10, 5, Calculator.Operation.ADD))
                .thenReturn(15);
        when(mockCalculator.calculate(10, 5, "divide"))
                .thenReturn(2);

        // Use reflection to call private method
        invokePrivateMethod("demonstrateCalculator");

        // Verify interactions
        verify(mockCalculator).calculate(10, 5, Calculator.Operation.ADD);
        verify(mockCalculator).calculate(10, 5, "divide");
    }

    @Test
    void testDemonstrateUserServiceWhenUserExists() {
        when(mockUserService.findUser("admin")).thenReturn(true);
        when(mockUserService.deleteUser("admin")).thenReturn(true);

        invokePrivateMethod("demonstrateUserService");

        verify(mockUserService).findUser("admin");
        verify(mockUserService).deleteUser("admin");
        verify(mockUserService).logActivity("Application demo completed");
    }

    @Test
    void testDemonstrateUserServiceWhenUserNotFound() {
        when(mockUserService.findUser("admin")).thenReturn(false);

        invokePrivateMethod("demonstrateUserService");

        verify(mockUserService).findUser("admin");
        verify(mockUserService, never()).deleteUser(anyString());
        verify(mockUserService).logActivity("Application demo completed");
    }

    // Helper method to invoke private methods
    private void invokePrivateMethod(String methodName) {
        try {
            var method = App.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(app);
        } catch (Exception e) {
            fail("Failed to invoke private method: " + methodName, e);
        }
    }

    // Helper to test private static method
    private String invokePrivateGetDatabasePassword() {
        try {
            var method = App.class.getDeclaredMethod("getDatabasePassword");
            method.setAccessible(true);
            return (String) method.invoke(null);
        } catch (Exception e) {
            fail("Failed to invoke getDatabasePassword", e);
            return null;
        }
    }
}
