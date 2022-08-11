package ua.com.sqlcmd.database;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class InMemoryDatabaseManager implements DatabaseManager {
    private DataSet[] data = new DataSet[100];
    private int size;
    private final boolean isConnected = true;

    @Override
    public void connect(String database, String userName, String password) {
    }

    @Override
    public Set<String> getTables() {
        return new LinkedHashSet<>(Arrays.asList("employee", "airplane", "testtable"));
    }

    @Override
    public DataSet[] getTableData(String tableName) {
        validate(tableName);
        return Arrays.copyOf(data, size);
    }

    @Override
    public void clear(String table) {
        data = new DataSet[100];
        size = 0;
    }

    @Override
    public void insert(DataSet dataSet, String tableName) {
        validate(tableName);
        data[size] = dataSet;
        size++;
    }

    @Override
    public void createTable(String queryCreateTable) {

    }

    @Override
    public void updateTableData(String tableName, String columnName, String columnValue, DataSet newValue) {
        validate(tableName);
        for (int i = 0; i < size; i++) {
            if (data[i].get(columnName).equals(columnValue)) {
                data[i].updateValue(newValue);
            }
        }

    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    private void validate(String tableName) {
        if (!tableName.equals("employee")) {
            throw new UnsupportedOperationException("Works only with 'employee', but you try: " + tableName);
        }
    }
}
