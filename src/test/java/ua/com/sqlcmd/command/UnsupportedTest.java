package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class UnsupportedTest {
    private View view;
    private Command command;

    @BeforeEach
    void setup() {
        view = Mockito.mock(View.class);
        command = new Unsupported(view);
    }

    @Test
    void canProcess() {
        boolean canProcess = command.canProcess("randomCommand");
        assertTrue(canProcess);
    }

    @Test
    void processValidation() {
        command.process("randomCommand");
        shouldPrint("[Не існує такої команди: randomCommand]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

}