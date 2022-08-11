package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Set;

public class Update implements Command {
    private final View view;
    private final DatabaseManager manager;

    public Update(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    private static void countParametersValidation(String[] split) {
        if (split.length < 6 || split.length % 2 != 0)
            throw new IllegalArgumentException(
                    String.format("Ви ввели '%d' параметрів, а потрібно '6' або більше парних значень", split.length));
    }

    private static void tableNameValidation(String[] split, Set<String> tableNames) {
        if (!tableNames.contains(split[1])) {
            throw new IllegalArgumentException("Введена невірна назва таблиці: " + split[1]);
        }
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("update|");
    }

    @Override
    public void process(String command) {
        String[] split = command.split("[|]");
        Set<String> tableNames = manager.getTables();
        tableNameValidation(split, tableNames);
        countParametersValidation(split);

        String tableName = split[1];
        String columnName = split[2];
        String columnValue = split[3];

        DataSet newDataSet = new DataSet();
        for (int i = 4; i + 1 < split.length; i = i + 2) {
            newDataSet.put(split[i], split[i + 1]);
        }
        manager.updateTableData(tableName, columnName, columnValue, newDataSet);
        view.write(String.format("Оновлено значення для рядків, де '%s' = '%s'", columnName, columnValue));
    }
}
