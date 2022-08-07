package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class HelpTest {

    private View view;
    private Command command;

    @BeforeEach
    void setup() {
        view = Mockito.mock(View.class);
        command = new Help(view);
    }

    @Test
    void canProcess() {
        boolean canProcess = command.canProcess("help");
        assertTrue(canProcess);
    }

    @Test
    void printHelpInfo() {
        command.process("help");
        shouldPrint("[\thelp, \t\tсписок всіх команд," +
                " \tconnect|database|userName|password, \t\tпідключення до бази данних," +
                " \tclear|tableName, \t\tочищення таблиці," +
                " \tcreate|tableName|column1|column2|...|columnN, \t\tстворення таблиці," +
                " \tinsert|tableName|column1|value1|column2|value2|...|columnN|valueN," +
                " \t\tвставити одну строку в таблицю, " +
                "\tlist, \t\tвідображення списку всіх таблиць," +
                " \tfind|tableName, \t\tвідображення вмісту таблиці, \texit, \t\tвихід з програми]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, Mockito.atLeastOnce()).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}