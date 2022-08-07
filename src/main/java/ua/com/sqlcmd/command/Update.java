package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Arrays;
import java.util.List;

public class Update implements Command {
    private View view;
    private DatabaseManager manager;

    public Update(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("update|");
    }

    @Override
    public void process(String command) {
        String[] split = command.split("[|]");
        List<String> tableNames = Arrays.asList(manager.getTables());
        tableNameValidation(split, tableNames);
        countParametersValidation(split);

        String tableName = split[1];
        String columnName = split[2];
        String columnValue = split[3];

        DataSet newDataSet = new DataSet();
        for (int i = 1; i * 4 < split.length; i++) {
            newDataSet.put(split[i * 4], split[i * 4 + 1]);
        }
        manager.updateTableData(tableName, columnName, columnValue, newDataSet);
        view.write(String.format("Оновлено значення для рядків, де '%s' = '%s'", columnName, columnValue));
    }

    private static void countParametersValidation(String[] split) {
        if (split.length < 6 || split.length % 2 != 0)
            throw new IllegalArgumentException(
                    String.format("Ви ввели '%d' параметрів, а потрібно '6' або більше парних значень", split.length));
    }

    private static void tableNameValidation(String[] split, List<String> tableNames) {
        if (!tableNames.contains(split[1])) {
            throw new IllegalArgumentException("Введена невірна назва таблиці: " + split[1]);
        }
    }
}
