package command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.com.sqlcmd.command.Command;
import ua.com.sqlcmd.command.Find;
import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});

        DataSet employee1 = new DataSet();
        employee1.put("id", 11);
        employee1.put("name", "Piter");
        employee1.put("email", "piter@gmail.com");

        DataSet employee2 = new DataSet();
        employee2.put("id", 15);
        employee2.put("name", "Leyla");
        employee2.put("email", "leyla@gmail.com");

        DataSet[] data = new DataSet[]{employee1, employee2};

        when(manager.getTableData("employee")).thenReturn(data);

        //when
        command.process("find|employee");

        //then
        shouldPrint("[id name email\n" +
                            "11 Piter piter@gmail.com\n" +
                            "15 Leyla leyla@gmail.com\n" +
                            "]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

    @Test
    void printEmptyTableData() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});

        DataSet empty = new DataSet();
        empty.put("id", "*");
        empty.put("name", "*");
        empty.put("email", "*");
        DataSet[] data = new DataSet[]{empty};

        when(manager.getTableData("employee")).thenReturn(data);

        //when
        command.process("find|employee");

        //then
        shouldPrint("[id name email\n" +
                            "* * *\n" +
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
            command.process("clear|asd|2asdf");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Невірний формат, потрібно: find|tableName, а було: clear|asd|2asdf", e.getMessage());
        }
    }

    @Test
    void cantProcessIncorrectTableName() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});
        try {
            command.process("clear|employ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Введена невірна назва таблиці: employ", e.getMessage());
        }
    }

}
