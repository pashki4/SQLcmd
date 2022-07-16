package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

public class Connect implements Command {
    private View view;
    private DatabaseManager manager;

    public Connect(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("connect|");
    }

    @Override
    public void process(String command) {
        try {
            String[] inputCommands = command.split("[|]");
            if (inputCommands.length != 4) {
                throw new IllegalArgumentException("невірна кількість параметрів розділених символом '|'," + " очікується 4, але ввели: " + inputCommands.length);
            }
            String dataBase = inputCommands[1];
            String userName = inputCommands[2];
            String password = inputCommands[3];
            manager.connect(dataBase, userName, password);
            view.write("Успіх!");
        } catch (Exception e) {
            printError(e);
        }
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
