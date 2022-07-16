package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

public class IsConnected implements Command {
    private DatabaseManager manager;
    private View view;

    public IsConnected(View view, DatabaseManager manager) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public boolean canProcess(String command) {
        return !manager.isConnected();
    }

    @Override
    public void process(String command) {
        view.write(String.format("Ви не можете користуватися командою '%s', " +
                "доки не підключитесь до бази: connect|database|userName|password", command));
    }
}
