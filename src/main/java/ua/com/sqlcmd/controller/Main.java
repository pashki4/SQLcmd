package ua.com.sqlcmd.controller;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.database.JDBCDatabaseManager;
import ua.com.sqlcmd.view.Console;
import ua.com.sqlcmd.view.View;

public class Main {
    public static void main(String[] args) {
        View view = new Console();
        DatabaseManager manager = new JDBCDatabaseManager();
        MainController controller = new MainController(view, manager);
        controller.run();
    }
}
