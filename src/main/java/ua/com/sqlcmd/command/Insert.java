package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

public class Insert implements Command {
    private View view;
    private DatabaseManager manager;

    public Insert(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("insert|");
    }

    @Override
    public void process(String command) {
        String[] split = command.split("[|]");
//        insert | tableName | column1 | value1 | column2 | value2 | ... | columnN | valueN
        if (split.length <= 2 || split.length % 2 != 0) {
            throw new IllegalArgumentException(
                    String.format("Невірна кількість параметрів, було %s, а потрібно >= 4", split.length));
        }
        String tableName = split[1];
        DataSet dataSet = new DataSet();
        for (int i = 1; i < split.length / 2; i++) {
            dataSet.put(split[i * 2], split[i * 2 + 1]);
        }
        manager.insert(dataSet, tableName);
    }
}
