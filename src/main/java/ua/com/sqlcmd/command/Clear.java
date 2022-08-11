package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Set;

public class Clear implements Command {
    private final View view;
    private final DatabaseManager manager;

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
        String[] split = command.split("[|]");
        if (split.length != 2) {
            throw new
                    IllegalArgumentException("невірна кількість параметрів розділених символом '|'," +
                    " очікується 2, але ввели:" + split.length);
        }
        String tableName = split[1];
        Set<String> tables = manager.getTables();

        if (!tables.contains(tableName)) {
            throw new IllegalArgumentException("Введена невірна назва таблиці: " + tableName);
        }
        view.write("Ви дійсно хочете очистити всі данні з таблиці: " + tableName + "?");
        while (true) {
            view.write("Введіть: 'yes' для очищення, або 'no' для відміни");
            String input = view.read();
            if (input.equalsIgnoreCase("yes")) {
                manager.clear(tableName);
                view.write("Таблиця: " + tableName + " очищенна");
                break;
            } else if (input.equalsIgnoreCase("no")) {
                break;
            } else throw
                    new IllegalArgumentException("невірний формат данних, потрібно: 'yes', або 'no', а ввели: " + input);
        }
    }
}
