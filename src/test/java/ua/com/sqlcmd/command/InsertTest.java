package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class InsertTest {
    private View view;
    private Command command;
    private DatabaseManager manager;

    @BeforeEach
    void setup() {
        view = Mockito.mock(View.class);
        manager = Mockito.mock(DatabaseManager.class);
        command = new Insert(view, manager);
    }

    @Test
    void canProcess() {
        boolean canProcess = command.canProcess("insert|");
        assertTrue(canProcess);
    }

    @Test
    void cantProcessNotEvenNumberParameters() {
        try {
            command.process("insert|tableName|column1|value1|column2");
        } catch (IllegalArgumentException e) {
            assertEquals("Потрібно вводити парну кількість парамтерів, та кількість повинна бути >= 4",
                    e.getMessage());
        }
    }

    @Test
    void cantProcessWrongTableName() {
        Mockito.when(manager.getTables()).thenReturn(new LinkedHashSet<>(Arrays.asList("table1","table2")));
        try {
            command.process("insert|wrongTableName|column1|value1|column2|value2");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Введена невірна назва таблиці: wrongTableName", e.getMessage());
        }
    }

    @Test
    void addDataToTable() {
        Mockito.when(manager.getTables()).thenReturn(new LinkedHashSet<>(Arrays.asList("table1", "table2")));
        command.process("insert|table1|column1|value1|column2|value2");
        shouldPrint("[Запис було успішно додано в таблицю 'table1']");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}