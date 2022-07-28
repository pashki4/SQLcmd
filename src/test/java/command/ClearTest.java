package command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.com.sqlcmd.command.Clear;
import ua.com.sqlcmd.command.Command;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

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
    void clearCorrectTableName() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});
        when(view.read()).thenReturn("yes");
        command.process("clear|employee");
        verify(manager).clear("employee");
        verify(view).write("Таблиця: employee очищенна");
    }

    @Test
    void clearIncorrectTableName() {
        when(manager.getTables()).thenReturn(new String[]{"employee", "airplane"});
        when(view.read()).thenReturn("yes");
        command.process("clear|employ");
        verify(manager, times(0)).clear("employee");
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
    void cantProcessClearWithoutParameters() {
        boolean canProcess = command.canProcess("clean|");
        assertFalse(canProcess);
    }
}
