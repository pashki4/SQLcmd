package ua.com.sqlcmd.command;

import ua.com.sqlcmd.view.View;

public class Help implements Command {
    private View view;

    public Help(View view) {
        this.view = view;
    }

    @Override
    public boolean canProcess(String command) {
        return command.equals("help");
    }

    @Override
    public void process(String command) {
        view.write("\thelp");
        view.write("\t\tсписок всіх команд");

        view.write("\ttables");
        view.write("\t\tсписок всіх таблиць");

        view.write("\tfind|tableName");
        view.write("\t\tвідображення вмісту таблиці");

        view.write("\texit");
        view.write("\t\tвихід з програми");
    }
}
