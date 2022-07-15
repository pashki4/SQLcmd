package ua.com.sqlcmd.database;

public class InMemoryDatabaseManagerTest extends DatabaseManagerTest{

    @Override
    public DatabaseManager getDatabaseManager() {
        return new InMemoryDatabaseManager();
    }
}
