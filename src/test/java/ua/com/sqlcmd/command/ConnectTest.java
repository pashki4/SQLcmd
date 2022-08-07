package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class ConnectTest {
    @Mock
    private View view;
    @Mock
    private DatabaseManager manager;
    private Command command;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        command = new Connect(view, manager);
    }

    @Test
    void canProcessConnectWithParameters() {
        boolean canProcess = command.canProcess("connect|");
        assertTrue(canProcess);
    }

    @Test
    void validationLessThanFourParameters() {
        try {
            command.process("connect|database|password");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("невірна кількість параметрів розділених символом '|', очікується 4, але ввели: 3",
                    e.getMessage());
        }
    }

    @Test
    void printWhenConnected() {
        command.process("connect|mytestdb|postgres|1234");
        shouldPrint("[Підключено до бази 'mytestdb']");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}