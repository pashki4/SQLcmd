package ua.com.sqlcmd.controller;

import ua.com.sqlcmd.command.Command;
import ua.com.sqlcmd.command.ExitException;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.util.AvailableCommands;
import ua.com.sqlcmd.view.View;

import java.util.List;

public class MainController {
    private final List<Command> commands;
    private final View view;


    public MainController(View view, DatabaseManager manager) {
        this.view = view;
        commands = AvailableCommands.get(view, manager);
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
        view.write("або help, для списку команд");

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
            view.write("Введіть команду або help для списку команд");
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
