package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DataSetImpl;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FindTest {

    @Mock
    private View view;
    @Mock
    private DatabaseManager manager;
    private Command command;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        command = new Find(view, manager);
    }

    @Test
    void printTableData() {
        when(manager.getTables()).thenReturn(new LinkedHashSet<>(Arrays.asList("employee", "airplane")));

        DataSet employee1 = new DataSetImpl();
        employee1.put("id", 11);
        employee1.put("name", "Piter");
        employee1.put("email", "piter@gmail.com");

        DataSet employee2 = new DataSetImpl();
        employee2.put("id", 15);
        employee2.put("name", "Leyla");
        employee2.put("email", "leyla@gmail.com");

        List<DataSet> data = new ArrayList<>(Arrays.asList(employee1, employee2));

        when(manager.getTableData("employee")).thenReturn(data);

        //when
        command.process("find|employee");

        //then
        shouldPrint("[+--+-----+---------------+\n" +
                "|id|name |     email     |\n" +
                "+--+-----+---------------+\n" +
                "|11|Piter|piter@gmail.com|\n" +
                "|15|Leyla|leyla@gmail.com|\n" +
                "+--+-----+---------------+\n" +
                "]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

    @Test
    void printEmptyTableData() {
        when(manager.getTables()).thenReturn(new LinkedHashSet<>(Arrays.asList("employee", "airplane")));

        DataSet empty = new DataSetImpl();
        empty.put("id", "");
        empty.put("name", "");
        empty.put("email", "");
        List<DataSet> data = new ArrayList<>(Arrays.asList(empty));

        when(manager.getTableData("employee")).thenReturn(data);

        //when
        command.process("find|employee");

        //then
        shouldPrint("[+--+----+-----+\n" +
                "|id|name|email|\n" +
                "+--+----+-----+\n" +
                "|  |    |     |\n" +
                "+--+----+-----+\n" +
                "]");
    }

    @Test
    void canProcessFindWithParameters() {
        boolean canProcess = command.canProcess("find|");
        assertTrue(canProcess);
    }

    @Test
    void cantProcessFindWithoutParameters() {
        boolean cantProcess = command.canProcess("find");
        assertFalse(cantProcess);
    }

    @Test
    void cantProcessQweWithWrongParameters() {
        boolean cantProcess = command.canProcess("qwe|asd");
        assertFalse(cantProcess);
    }

    @Test
    void cantProcessIncorrectParameterNumber() {
        try {
            command.process("clear|asd|someValue");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("невірний формат, потрібно: find|tableName, а ввели 'clear|asd|someValue'",
                    e.getMessage());
        }
    }

    @Test
    void cantProcessIncorrectTableName() {
        when(manager.getTables()).thenReturn(new LinkedHashSet<>(Arrays.asList("employee", "airplane")));
        try {
            command.process("clear|employ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("невірна назва таблиці 'employ'", e.getMessage());
        }
    }
}
