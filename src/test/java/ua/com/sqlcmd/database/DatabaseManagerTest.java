package ua.com.sqlcmd.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class DatabaseManagerTest {

    private DatabaseManager manager;

    public abstract DatabaseManager getDatabaseManager();

    @BeforeEach
    public void setUp() {
        manager = getDatabaseManager();
        manager.connect("mytestdb", "postgres", "1234");
    }

    @Test
    public void testGetAllTableNames() {
        String[] tableNames = manager.getTables();
        assertEquals("[employee, airplane]", Arrays.toString(tableNames));
    }

    @Test
    public void testGetTableData() {
        //given
        manager.clear("employee");

        //when
        DataSet coworker = new DataSet();
        coworker.put("id", 1);
        coworker.put("name", "Vasya");
        coworker.put("salary", 15000.95);
        manager.insert(coworker, "employee");

        //then
        DataSet[] users = manager.getTableData("employee");
        assertEquals(1, users.length);
        coworker = users[0];
        assertEquals("[id, name, salary]", Arrays.toString(coworker.getColumnNames()));
        assertEquals("[1, Vasya, 15000.95]", Arrays.toString(coworker.getValues()));
    }

    @Test
    public void testUpdateTableData() {
        manager.clear("employee");
        DataSet coworker = new DataSet();
        coworker.put("id", 1);
        coworker.put("name", "sadVasya");
        coworker.put("salary", 1000.09);
        manager.insert(coworker, "employee");
        coworker = new DataSet();
        coworker.put("name","happyVasya");
        coworker.put("salary",34000.11);
        manager.updateTableData("employee", 1, coworker);
        assertEquals("[happyVasya, 34000.11]", Arrays.toString(coworker.getValues()));
    }

}