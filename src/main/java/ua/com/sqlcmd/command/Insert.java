package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DataSetImpl;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Set;

public class Insert implements Command {
    private final View view;
    private final DatabaseManager manager;

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
        if ((split.length <= 3) || (split.length % 2 != 0)) {
            throw new IllegalArgumentException(
                    String.format("Потрібно вводити парну кількість парамтерів, та кількість повинна бути >= 4",
                            split.length));
        }

        String tableName = split[1];
        Set<String> tables = manager.getTables();

        if (!tables.contains(tableName)) {
            throw new IllegalArgumentException("Введена невірна назва таблиці: " + tableName);
        }

        DataSet dataSet = new DataSetImpl();
        for (int i = 1; i < split.length / 2; i++) {
            dataSet.put(split[i * 2], split[i * 2 + 1]);
        }
        manager.insert(dataSet, tableName);
        view.write(String.format("Запис було успішно додано в таблицю '%s'", tableName));
    }
}
