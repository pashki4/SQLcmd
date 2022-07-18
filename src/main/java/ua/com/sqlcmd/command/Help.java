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

        view.write("\tconnect|database|userName|password");
        view.write("\t\tпідключення до бази данних");

        view.write("\tclear|tableName");
        view.write("\t\tочищення таблиці");

        view.write("\tcreate|tableName|column1|column2|...|columnN");
        view.write("\t\tстворення таблиці");

        view.write("\tlist");
        view.write("\t\tвідображення списку всіх таблиць");

        view.write("\tfind|tableName");
        view.write("\t\tвідображення вмісту таблиці");

        view.write("\texit");
        view.write("\t\tвихід з програми");
    }
}
