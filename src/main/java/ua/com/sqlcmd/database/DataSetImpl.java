package ua.com.sqlcmd.database;

import java.util.*;

public class DataSetImpl implements DataSet {
    private Map<String, Object> data = new LinkedHashMap<>();

    @Override
    public void put(String name, Object value) {
        data.put(name, value);
    }

    @Override
    public Set<String> getColumnNames() {
        return data.keySet();
    }

    @Override
    public List<Object> getValues() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateValue(DataSet newValue) {
        Set<String> columnNames = newValue.getColumnNames();
        for (String columName : columnNames) {
            data.put(columName, newValue.get(columName));
        }
    }

    @Override
    public Object get(String name) {
        if (!data.containsKey(name)) {
            data.put(name, "");
        }
        return data.get(name);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
