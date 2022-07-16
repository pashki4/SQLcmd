package ua.com.sqlcmd.controller;

import ua.com.sqlcmd.command.*;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Arrays;

public class MainController {
    private Command[] commands;
    private View view;
    private DatabaseManager manager;

    public MainController(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
        commands = new Command[]{
                new Help(view),
                new Exit(view),
                new Tables(view, manager),
                new Find(view, manager),
                new Unsupported(view)
        };
    }

    public void run() {
        connect();
        while (true) {
            view.write("Введіть команду або help для списку команд");
            String input = view.read();
            if (commands[2].canProcess(input)) {
                commands[2].process(input);
            } else if (commands[0].canProcess(input)) {
                commands[0].process(input);
            } else if (commands[1].canProcess(input)) {
                commands[1].process(input);
            } else if (commands[3].canProcess(input)) {
                commands[3].process(input);
            } else {
                commands[4].process(input);
            }
        }
        //...
        //...
    }

    private void connect() {
        view.write("Вітаю юзер!");
        view.write("Введи, будь ласка, данні для підключення у такому форматі: database|userName|password");
        while (true) {
            try {
                String input = view.read();
                String[] inputData = input.split("[|]");
                if (inputData.length != 3) {
                    throw new IllegalArgumentException("невірна кількість параметрів розділених символом '|'," + " очікується 3, але ввели: " + inputData.length);
                }
                String dataBase = inputData[0];
                String userName = inputData[1];
                String password = inputData[2];
                manager.connect(dataBase, userName, password);
                break;
            } catch (Exception e) {
                printError(e);
            }
        }
        view.write("Успіх!");
    }

    private void printError(Exception e) {
        String message = e.getMessage();
        if (e.getCause() != null) {
            message += " " + e.getCause().getMessage();
        }
        view.write("Невдача через: " + message);
        view.write("Спробуй ще: ");
    }
}
