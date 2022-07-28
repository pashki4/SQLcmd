package ua.com.sqlcmd.view;

import java.util.Scanner;

public class Console implements View {
    @Override
    public String read() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return input;
    }

    @Override
    public void write(String message) {
        System.out.println(message);
    }
}
