package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

public class Create implements Command {
    private View view;
    private DatabaseManager manager;

    public Create(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("create|");
    }

    @Override
    public void process(String command) {
        String[] input = command.split("[|]");
        if (input.length <= 2) {
            throw new IllegalArgumentException(String.format("Неправильна кількість параметрів," +
                    " потрібно більше 2, а було введено: %s", input.length));
        }
        manager.createTable(command);
        view.write(String.format("Створено таблицю: %s", input[1]));
    }
}
