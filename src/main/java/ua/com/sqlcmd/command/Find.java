package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Arrays;
import java.util.List;

public class Find implements Command {
    private View view;
    private DatabaseManager manager;

    public Find(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("find|");
    }

    @Override
    public void process(String command) {
        try {
            String[] input = command.split("[|]");
            if (input.length != 2) {
                throw new IllegalArgumentException(
                        String.format("Невірний формат, потрібно: find|tableName, а було: %s", command));
            } else {
                String tableName = input[1];
                List<String> strings = Arrays.asList(manager.getTables());
                if (!strings.contains(tableName)) {
                    throw new IllegalAccessException("Введена невірна назва таблиці: " + tableName);
                }
                DataSet[] tableData = manager.getTableData(tableName);
                manager.printTableData(tableData);
            }
        } catch (Exception e) {
            printMessage(e);
        }

    }

    private void printMessage(Exception e) {
        String message = e.getMessage();
        if (e.getCause() != null) {
            message += e.getCause().getMessage();
        }
        view.write(message);
    }
}
