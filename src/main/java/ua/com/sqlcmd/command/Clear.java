package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Arrays;
import java.util.List;

public class Clear implements Command {
    private View view;
    private DatabaseManager manager;

    public Clear(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("clear|");
    }

    @Override
    public void process(String command) {
        try {
            String[] split = command.split("[|]");
            if (split.length != 2) {
                throw new
                        IllegalArgumentException("невірна кількість параметрів розділених символом '|'," +
                        " очікується 2, але ввели:" + split.length);
            }
            String tableName = split[1];
            List<String> tables = Arrays.asList(manager.getTables());
            if (!tables.contains(tableName)) {
                throw new IllegalAccessException("Введена невірна назва таблиці: " + tableName);
            }
            view.write("Ви дійсно хочете очистити всі данні з таблиці: " + tableName + "?");
            while (true) {
                view.write("Введіть: 'так' для очищення, або 'ні' для відміни");
                String input = view.read();
                if (input.equalsIgnoreCase("так")) {
                    manager.clear(tableName);
                    view.write("Таблиця: " + tableName + " очищенна");
                    break;
                } else if (input.equalsIgnoreCase("ні")) {
                    break;
                } else throw
                        new IllegalArgumentException("невірний формат данних, потрібно: 'так', або 'ні', а ввели: " + input);
            }
        } catch (Exception e) {
            printMessage(e);
        }
    }

    private void printMessage(Exception e) {
        String message = e.getMessage();
        if (e.getCause() != null) {
            message += " " + e.getCause().getMessage();
        }
        view.write("Невдача через " + message);
    }
}
