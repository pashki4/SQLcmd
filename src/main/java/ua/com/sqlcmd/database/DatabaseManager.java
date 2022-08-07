package ua.com.sqlcmd.database;

import java.sql.SQLException;

public interface DatabaseManager {

    void connect(String database, String userName, String password);
    String[] getTables();

    DataSet[] getTableData(String tableName);

    void clear(String table);

    void insert(DataSet dataSet, String tableName);

    void createTable(String queryCreateTable);

    void updateTableData(String tableName, int id, DataSet newValue);

    boolean isConnected();

}
