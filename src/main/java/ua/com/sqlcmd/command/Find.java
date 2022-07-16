package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

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
        String[] input = command.split("[|]");
        if (input.length != 2) {
            view.write(String.format("Невірний формат, потрібно: find|tableName, а було: %s", command));
        } else {
            String tableName = input[1];
            manager.printTableData(manager.getTableData(tableName));
        }
    }
}
