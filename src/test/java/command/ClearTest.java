package command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.com.sqlcmd.command.Clear;
import ua.com.sqlcmd.command.Command;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.lang.reflect.Executable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClearTest {
    @Mock
    private View view;
    @Mock
    private DatabaseManager manager;
    private Command command;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        command = new Clear(view, manager);
    }

    @Test
    void clearCorrectTableNameConfirm() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});
        when(view.read()).thenReturn("yes");
        command.process("clear|employee");
        verify(manager).clear("employee");
        verify(view).write("Таблиця: employee очищенна");
    }
    @Test
    void clearCorrectTableNameNotConfirm() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});
        when(view.read()).thenReturn("no");
        command.process("clear|employee");
        verify(manager, never()).clear("employee");
    }
    @Test
    void clearCorrectTableNameIncorrectConfirmCommand() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});
        when(view.read()).thenReturn("i don't know");
        try {
            command.process("clear|employee");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("невірний формат данних, потрібно: 'yes', або 'no', а ввели: i don't know",
                    e.getMessage());
        }
        verify(manager, never()).clear("employee");
    }

    @Test
    void clearIncorrectTableName() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});
        try {
            command.process("clear|employ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Введена невірна назва таблиці: employ", e.getMessage());
        }
    }

    @Test
    void canProcessClearWithParameters() {
        boolean canProcess = command.canProcess("clear|");
        assertTrue(canProcess);
    }

    @Test
    void cantProcessCleanWithCorrectParameters() {
        boolean canProcess = command.canProcess("clean|employee");
        assertFalse(canProcess);
    }

    @Test
    void validationErrorMoreThanTwoParameters() {
        try {
            command.process("clear|asdf|asdfasf");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("невірна кількість параметрів розділених символом '|', очікується 2, але ввели:3",
                    e.getMessage());
        }
    }

    @Test
    void test() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});
        try {
            command.process("clear|notExistentTable");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Введена невірна назва таблиці: notExistentTable", e.getMessage());
        }
    }
}
