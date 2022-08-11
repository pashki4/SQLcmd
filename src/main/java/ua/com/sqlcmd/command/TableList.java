package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Set;

public class TableList implements Command {

    private final DatabaseManager manager;
    private final View view;

    public TableList(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.equals("list");
    }

    @Override
    public void process(String command) {
        Set<String> tables = manager.getTables();
        view.write(tables.toString());
    }
}
