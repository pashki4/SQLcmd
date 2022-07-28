package ua.com.sqlcmd.command;

public class ExitException extends RuntimeException {
    public ExitException(String message) {
        super(message);
    }
}
