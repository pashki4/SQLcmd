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
        assertEquals("[User, employee]", Arrays.toString(tableNames));
    }

    @Test
    public void testGetTableData() {
        //given
        manager.clear("employee");

        //when
        DataSet coworker = new DataSet();
        coworker.valueOf("id", 1);
        coworker.valueOf("name", "Vasya");
        coworker.valueOf("salary", 15000.95);
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
        coworker.valueOf("id", 1);
        coworker.valueOf("name", "sadVasya");
        coworker.valueOf("salary", 1000.09);
        manager.insert(coworker, "employee");
        coworker = new DataSet();
        coworker.valueOf("name","happyVasya");
        coworker.valueOf("salary",34000.11);
        manager.updateTableData("employee", 1, coworker);
        assertEquals("[happyVasya, 34000.11]", Arrays.toString(coworker.getValues()));
    }

}