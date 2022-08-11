package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class TableListTest {
    private View view;
    private DatabaseManager manager;
    private Command command;

    @BeforeEach
    void setup() {
        view = Mockito.mock(View.class);
        manager = Mockito.mock(DatabaseManager.class);
        command = new TableList(view, manager);
    }

    @Test
    void canProcessList() {
        boolean canProcess = command.canProcess("list");
        assertTrue(canProcess);
    }

    @Test
    void getTablesList() {
        Set<String> tables = new LinkedHashSet<>(Arrays.asList("table1", "table2", "table3"));
        Mockito.when(manager.getTables()).thenReturn(tables);
        command.process("list");
        shouldPrint("[[table1, table2, table3]]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

}