package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

public class Connect implements Command {
    private final View view;
    private final DatabaseManager manager;

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
        String[] inputCommands = command.split("[|]");
        if (inputCommands.length != 4) {
            throw new IllegalArgumentException("невірна кількість параметрів розділених символом '|',"
                    + " очікується 4, але ввели: %d".formatted(inputCommands.length));
        }
        manager.connect(inputCommands[1], inputCommands[2], inputCommands[3]);
        view.write("Підключено до бази '%s'".formatted(inputCommands[1]));
    }
}
