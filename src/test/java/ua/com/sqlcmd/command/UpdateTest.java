package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateTest {
    private View view;
    private DatabaseManager manager;
    private Command command;

    @BeforeEach
    void setup() {
        view = Mockito.mock(View.class);
        manager = Mockito.mock(DatabaseManager.class);
        command = new Update(view, manager);
    }

    @Test
    void canProcess() {
        boolean canProcess = command.canProcess("update|");
        assertTrue(canProcess);
    }

    @Test
    void cantProcess() {
        boolean canProcess = command.canProcess("update");
        assertFalse(canProcess);
    }

    @Test
    void cantProcessNotEnoughParameters() {
        Mockito.when(manager.getTables()).thenReturn(new String[]{"table1", "table2"});
        try {
            command.process("help|table1|columnName|columnValue|columnName1");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Ви ввели '5' параметрів, а потрібно '6' або більше парних значень", e.getMessage());
        }
    }

    @Test
    void cantProcessWrongTableName() {
        Mockito.when(manager.getTables()).thenReturn(new String[]{"table1", "table2"});
        try {
            command.process("update|wrongTableName|columnName|columnValue|column1|value1|column2|value2");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Введена невірна назва таблиці: wrongTableName", e.getMessage());
        }
    }

    @Test
    void correctUpdateMessage() {
        when(manager.getTables()).thenReturn(new String[]{"table1", "table2"});
        command.process("update|table1|id|123|column1|value1");
        shouldPrint("[Оновлено значення для рядків, де 'id' = '123']");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}