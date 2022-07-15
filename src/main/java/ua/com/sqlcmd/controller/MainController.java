package ua.com.sqlcmd.controller;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

public class MainController {
    private View view;
    private DatabaseManager manager;

    public MainController(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    public void run() {
        connect();
        //...
        //...
        //...
    }

    private void connect() {
        view.write("Привіт юзер!");
        view.write("Введи, будь ласка, данні для підключення у такому форматі: database|userName|password");
        while (true) {
            try {
                String input = view.read();
                String[] inputData = input.split("[|]");
                if (inputData.length != 3) {
                    throw new IllegalArgumentException("невірна кількість параметрів розділених символом '|'," +
                            " очікується 3, але ввели: " + inputData.length);
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
