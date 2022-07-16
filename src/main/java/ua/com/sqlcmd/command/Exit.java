package ua.com.sqlcmd.command;

import ua.com.sqlcmd.view.View;

public class Exit implements Command{

    private final View view;

    public Exit(View view) {
        this.view = view;
    }
    @Override
    public boolean canProcess(String command) {
        return command.equals("exit");
    }

    @Override
    public void process(String command) {
        view.write("До зустрічі. Нехай щастить!");
        System.exit(0);
    }
}
