package ua.com.sqlcmd.util;

import ua.com.sqlcmd.command.*;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.List;

public final class AvailableCommands {

    private AvailableCommands() {

    }

    public static List<Command> get(View view, DatabaseManager manager) {
        return List.of(
                new Connect(view, manager),
                new Help(view),
                new Exit(view),
                new CheckConnection(view, manager),
                new Create(view, manager),
                new Update(view, manager),
                new Insert(view, manager),
                new TableList(view, manager),
                new Find(view, manager),
                new Clear(view, manager),
                new Unsupported(view)
        );
    }
}
