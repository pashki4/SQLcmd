package ua.com.sqlcmd.database;

import java.util.Arrays;

public class InMemoryDatabaseManager implements DatabaseManager {
    private DataSet[] data = new DataSet[100];
    private int size;

    @Override
    public boolean connect(String database, String userName, String password) {
        return true;
    }

    @Override
    public String[] getTables() {
        return new String[]{"User", "employee"};
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
    public void createTable() {

    }

    @Override
    public void updateTableData(String tableName, int id, DataSet newValue) {
        validate(tableName);
        for (int i = 0; i < size; i++) {
            if (data[i].get("id").equals(id)) {
                data[i].updateValue(newValue);
            }
        }
    }

    private void validate(String tableName) {
        if (!tableName.equals("employee")) {
            throw new UnsupportedOperationException("Works only with 'employee', but you try: " + tableName);
        }
    }
}
