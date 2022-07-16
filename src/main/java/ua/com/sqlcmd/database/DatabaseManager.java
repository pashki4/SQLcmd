package ua.com.sqlcmd.database;

public interface DatabaseManager {

    void connect(String database, String userName, String password);
    String[] getTables();

    DataSet[] getTableData(String tableName);

    void clear(String table);

    void insert(DataSet dataSet, String tableName);

    void createTable();

    void updateTableData(String tableName, int id, DataSet newValue);

    void printTableData(DataSet[] dataSets);

    boolean isConnected();

}
