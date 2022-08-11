package ua.com.sqlcmd.database;

import java.util.List;
import java.util.Set;

public interface DataSet {
    void put(String name, Object value);

    Set<String> getColumnNames();

    List<Object> getValues();

    void updateValue(DataSet newValue);

    Object get(String name);
}
