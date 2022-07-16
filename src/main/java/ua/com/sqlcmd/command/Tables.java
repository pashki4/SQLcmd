package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Arrays;

public class Tables implements Command {

    private final DatabaseManager manager;
    private final View view;

    public Tables(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.equals("tables");
    }

    @Override
    public void process(String command) {
        String[] tables = manager.getTables();
        String result = Arrays.toString(tables);
        view.write(result);
    }
}
