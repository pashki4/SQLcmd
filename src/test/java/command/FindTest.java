package command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ua.com.sqlcmd.command.Command;
import ua.com.sqlcmd.command.Find;
import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FindTest {

    private View view;
    private DatabaseManager manager;
    private Command command;

    @BeforeEach
    public void setup() {
        view = mock(View.class);
        manager = mock(DatabaseManager.class);
        command = new Find(view, manager);
    }


    @Test
    void printTableData() {
        when(manager.getTables())
                .thenReturn(new String[]{"employee", "airplane"});

        DataSet employee1 = new DataSet();
        employee1.put("id", 11);
        employee1.put("name", "Piter");
        employee1.put("email", "piter@gmail.com");

        DataSet employee2 = new DataSet();
        employee2.put("id", 15);
        employee2.put("name", "Leyla");
        employee2.put("email", "leyla@gmail.com");

        DataSet[] data = new DataSet[]{employee1, employee2};

        when(manager.getTableData("employee"))
                .thenReturn(data);

        //when
        command.process("find|employee");

        //then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view).write(captor.capture());
        assertEquals("[id name email\n" +
                "11 Piter piter@gmail.com\n" +
                "15 Leyla leyla@gmail.com\n" +
                "]", captor.getAllValues().toString());
    }
}
