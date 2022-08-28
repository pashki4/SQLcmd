package ua.com.sqlcmd.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.sqlcmd.util.PropertiesUtil;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class DatabaseManagerTest {

    private DatabaseManager manager;
    private static final String DB_NAME_KEY = "db.name";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    public abstract DatabaseManager getDatabaseManager();

    @BeforeEach
    void setUp() {
        manager = getDatabaseManager();
        manager.connect(PropertiesUtil.get(DB_NAME_KEY),
                        PropertiesUtil.get(USERNAME_KEY),
                        PropertiesUtil.get(PASSWORD_KEY)
        );
    }

    @Test
    void testGetAllTableNames() {
        Set<String> tableNames = manager.getTables();
        assertEquals("[employee, airplane, testtable]", tableNames.toString());
    }

    @Test
    void testGetTableData() {
        //given
        manager.clear("employee");

        //when
        DataSet coworker = new DataSetImpl();
        coworker.put("id", 1);
        coworker.put("name", "Vasya");
        coworker.put("salary", 15000.95);
        manager.insert(coworker, "employee");

        //then
        List<DataSet> users = manager.getTableData("employee");
        assertEquals(1, users.size());
        coworker = users.get(0);
        assertEquals("[id, name, salary]", coworker.getColumnNames().toString());
        assertEquals("[1, Vasya, 15000.95]", coworker.getValues().toString());
    }

    @Test
    void testUpdateTableData() {
        manager.clear("employee");
        DataSet coworker = new DataSetImpl();
        coworker.put("id", 1);
        coworker.put("name", "sadVasya");
        coworker.put("salary", 1000.09);
        manager.insert(coworker, "employee");
        coworker = new DataSetImpl();
        coworker.put("name", "happyVasya");
        coworker.put("salary", 34000.11);
        manager.updateTableData("employee", "id", "1", coworker);
        assertEquals("[happyVasya, 34000.11]", coworker.getValues().toString());
    }

}