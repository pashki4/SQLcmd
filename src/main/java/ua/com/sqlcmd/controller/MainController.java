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
        view.write("Привет юзер!");
        view.write("Введи, пожалуйста, данные для подключения в таком формате: database|userName|password");
        while (true) {
            String input = view.read();
            String[] inputData = input.split("[|]");
            String dataBase = inputData[0];
            String userName = inputData[1];
            String password = inputData[2];

            try {
                manager.connect(dataBase, userName, password);
                break;
            } catch (Exception e) {
                String message = e.getMessage();
                if (e.getCause() != null) {
                    message += " " + e.getCause().getMessage();
                }
                view.write("Неудача по причине: " + message);
                view.write("Повтори попытку: ");
            }
        }
        view.write("Успех!");
    }
}
