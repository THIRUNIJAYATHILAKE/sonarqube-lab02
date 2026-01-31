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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    private ListAppender<ILoggingEvent> listAppender;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        userService = new UserService("test-password");
        setupLogCapture();
    }

    private void setupLogCapture() {
        Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testFindUserWithNullUsernameReturnsFalse() {
        assertFalse(userService.findUser(null));

        // Verify warning was logged
        boolean warningLogged = listAppender.list.stream()
                .anyMatch(event -> event.getLevel() == Level.WARN &&
                        event.getFormattedMessage().contains("null or empty username"));
        assertTrue(warningLogged);
    }

    @Test
    void testFindUserWithEmptyUsernameReturnsFalse() {
        assertFalse(userService.findUser(""));
        assertFalse(userService.findUser("   "));
    }

    @Test
    void testFindUserWithValidUsernameWhenDatabaseFails() {
        // This will fail because no real database connection
        boolean result = userService.findUser("testuser");
        assertFalse(result);

        // Verify error was logged
        boolean errorLogged = listAppender.list.stream()
                .anyMatch(event -> event.getLevel() == Level.ERROR);
        assertTrue(errorLogged);
    }

    @Test
    void testLogActivityWithValidStringLogsInfo() {
        userService.logActivity("Test activity");

        boolean infoLogged = listAppender.list.stream()
                .anyMatch(event -> event.getLevel() == Level.INFO &&
                        event.getFormattedMessage().contains("Test activity"));
        assertTrue(infoLogged);
    }

    @Test
    void testLogActivityWithEmptyStringDoesNotLog() {
        userService.logActivity("");
        userService.logActivity("   ");
        userService.logActivity(null);

        long infoLogs = listAppender.list.stream()
                .filter(event -> event.getLevel() == Level.INFO)
                .count();
        assertEquals(0, infoLogs, "Should not log empty/null activities");
    }

    @Test
    void testDeleteUserWithNullUsernameReturnsFalse() {
        assertFalse(userService.deleteUser(null));

        boolean warningLogged = listAppender.list.stream()
                .anyMatch(event -> event.getLevel() == Level.WARN &&
                        event.getFormattedMessage().contains("null or empty username"));
        assertTrue(warningLogged);
    }

    @Test
    void testDeleteUserWithEmptyUsernameReturnsFalse() {
        assertFalse(userService.deleteUser(""));
        assertFalse(userService.deleteUser("   "));
    }

    @Test
    void testDeleteUserWhenDatabaseFails() {
        boolean result = userService.deleteUser("testuser");
        assertFalse(result);

        boolean errorLogged = listAppender.list.stream()
                .anyMatch(event -> event.getLevel() == Level.ERROR);
        assertTrue(errorLogged);
    }

    @Test
    void testCleanupLogsDebugMessage() {
        userService.cleanup();

        boolean debugLogged = listAppender.list.stream()
                .anyMatch(event -> event.getLevel() == Level.DEBUG &&
                        event.getFormattedMessage().contains("cleanup"));
        assertTrue(debugLogged);
    }

    @Test
    void testConstructorWithNullPassword() {
        UserService service = new UserService(null);
        assertNotNull(service);
    }

    @Test
    void testConstructorWithEmptyPassword() {
        UserService service = new UserService("");
        assertNotNull(service);
    }

    @Test
    void testGetSqlErrorDetailPrivateMethod() throws Exception {
        // Test private method using reflection
        var method = UserService.class.getDeclaredMethod("getSqlErrorDetail", SQLException.class);
        method.setAccessible(true);

        SQLException sqlEx = new SQLException("Test error", "25000", 1062);
        String result = (String) method.invoke(userService, sqlEx);

        assertNotNull(result);
        assertTrue(result.contains("25000"));
        assertTrue(result.contains("1062"));
    }
}
