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
        String[] inputCommands = command.split("[|]");
        if (inputCommands.length != 4) {
            throw new IllegalArgumentException("невірна кількість параметрів розділених символом '|',"
                    + " очікується 4, але ввели: " + inputCommands.length);
        }
        manager.connect(inputCommands[1], inputCommands[2], inputCommands[3]);
        view.write(String.format("Підключено до бази '%s'", inputCommands[1]));
    }
}
