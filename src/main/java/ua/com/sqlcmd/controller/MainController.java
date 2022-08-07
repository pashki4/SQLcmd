package ua.com.sqlcmd.controller;

import ua.com.sqlcmd.command.*;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

public class MainController {
    private java.util.List<Command> commands;
    private View view;
    private DatabaseManager manager;

    public MainController(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
        commands = java.util.List.of(
                new Connect(view, manager),
                new Help(view),
                new Exit(view),
                new CheckConnection(view, manager),
                new Create(view, manager),
                new Update(view, manager),
                new Insert(view, manager),
                new List(view, manager),
                new Find(view, manager),
                new Clear(view, manager),
                new Unsupported(view)
        );
    }

    public void run() {
        try {
            doWork();
        } catch (ExitException e) {
            //do nothing
        }
    }

    private void doWork() {
        view.write("Вітаю юзер!");
        view.write("Введи, данні для підключення у такому форматі: connect|database|userName|password");
        view.write("або help, для переліку команд");

        while (true) {
            try {
                String input = view.read();
                for (Command command : commands) {
                    if (command.canProcess(input)) {
                        command.process(input);
                        break;
                    }
                }
            } catch (ExitException e) {
                System.exit(0);
            } catch (Exception e) {
                printMessage(e);
            }
            view.write("Введіть команду або help для переліку команд");
        }
    }

    private void printMessage(Exception e) {
        String message = e.getMessage();
        if (e.getCause() != null) {
            message += e.getCause().getMessage();
        }
        view.write("Невдача у зв'язку з: " + message);
    }
}
