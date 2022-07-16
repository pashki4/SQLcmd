package ua.com.sqlcmd.command;

public interface Command {

    boolean canProcess(String command);

    void process(String command);
}
