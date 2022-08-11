package ua.com.sqlcmd.database;

import java.util.*;

public class InMemoryDatabaseManager implements DatabaseManager {
    private final boolean isConnected = true;
    private List<DataSet> data = new LinkedList<>();
    private int size;

    @Override
    public void connect(String database, String userName, String password) {
    }

    @Override
    public Set<String> getTables() {
        return new LinkedHashSet<>(Arrays.asList("employee", "airplane", "testtable"));
    }

    @Override
    public List<DataSet> getTableData(String tableName) {
        validate(tableName);
        return data;
    }

    @Override
    public void clear(String table) {
        data.clear();
    }

    @Override
    public void insert(DataSet dataSet, String tableName) {
        validate(tableName);
        data.add(dataSet);
    }

    @Override
    public void createTable(String queryCreateTable) {

    }

    @Override
    public void updateTableData(String tableName, String columnName, String columnValue, DataSet newValue) {
        validate(tableName);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).get(columnName).equals(columnValue)) {
                data.get(i).updateValue(newValue);
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
