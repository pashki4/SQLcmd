package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ua.com.sqlcmd.view.View;

import static org.junit.jupiter.api.Assertions.*;

public class ExitTest {
    private View view;
    private Command command;

    @BeforeEach
    void setup() {
        view = Mockito.mock(View.class);
        command = new Exit(view);
    }

    @Test
    void canProcessExit() {
        boolean canProcess = command.canProcess("exit");
        assertTrue(canProcess);
    }

    @Test
    void cantProcessExit() {
        boolean cantProcess = command.canProcess("qwe");
        assertFalse(cantProcess);
    }

    @Test
    void precessExitCommand_throwExitException() {
        try {
            command.process("exit");
            fail("Expected ExitException");
        } catch (ExitException e) {
            //do nothing
        }
        Mockito.verify(view).write("До зустрічі. Нехай щастить!");
    }
}
