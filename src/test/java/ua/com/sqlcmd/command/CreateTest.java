package ua.com.sqlcmd.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CreateTest {
    @Mock
    private View view;
    @Mock
    private DatabaseManager manager;
    private Command command;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        command = new Create(view, manager);
    }

    @Test
    void getCantProcessCreateWithoutParameters() {
        boolean canProcess = command.canProcess("create|");
        assertTrue(canProcess);
    }

    @Test
    void cantProcessCreateWithoutParameters() {
        boolean canProcess = command.canProcess("create");
        assertFalse(canProcess);
    }

    @Test
    void validationCreateTableIncorrectParametersCount() {
        try {
            command.process("create|tableName");
        } catch (IllegalArgumentException e) {
            String expected = "Неправильна кількість параметрів, потрібно більше 2, а було введено: 2";
            assertEquals(expected, e.getMessage());
        }
    }

    @Test
    void validationCreateTableCorrectParametersCount() {
        command.process("create|newTable|columnName1|columnName2");
        shouldPrint("[Створено таблицю: newTable]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}