package ua.com.sqlcmd.command;

import ua.com.sqlcmd.view.View;

public class Unsupported implements Command {
    private final View view;

    public Unsupported(View view) {
        this.view = view;
    }

    @Override
    public boolean canProcess(String command) {
        return true;
    }

    @Override
    public void process(String command) {
        view.write("Не існує такої команди: " + command);
    }
}
