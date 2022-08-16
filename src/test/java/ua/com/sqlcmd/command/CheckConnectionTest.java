package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

class CheckConnectionTest {

    private View view;
    private DatabaseManager manager;
    private Command command;

    @BeforeEach
    void setup() {
        view = Mockito.mock(View.class);
        manager = Mockito.mock(DatabaseManager.class);
        command = new CheckConnection(view, manager);
    }

    @Test
    void processCheckConnectionWhenConnectionIsFalse() {
        Mockito.when(manager.isConnected()).thenReturn(false);
        command.process("list");
        shouldPrint("[Ви не можете користуватися командою 'list',"
                          + " спочатку підключіться до бази: connect|database|userName|password]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

    @Test
    void processCheckConnectionWhenConnectionIsTrue() {
        Mockito.when(manager.isConnected()).thenReturn(true);
        boolean canProcess = command.canProcess("list");
        assertFalse(canProcess);
    }
}