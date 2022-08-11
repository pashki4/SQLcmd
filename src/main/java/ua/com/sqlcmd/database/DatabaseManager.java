package ua.com.sqlcmd.database;

import java.util.Set;

public interface DatabaseManager {

    void connect(String database, String userName, String password);

    Set<String> getTables();

    DataSet[] getTableData(String tableName);

    void clear(String table);

    void insert(DataSet dataSet, String tableName);

    void createTable(String queryCreateTable);

    void updateTableData(String tableName, String columnName, String columnValue, DataSet newValue);

    boolean isConnected();

}
