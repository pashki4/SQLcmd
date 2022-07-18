package ua.com.sqlcmd.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Testing {
    public static void main(String[] args) {
        while (true) {
            System.out.println("1");
            try {
                Connection connection = DriverManager.getConnection("");
                System.out.println("SELECT * FROM employee");
            } catch (SQLException e) {
                System.out.println("Can't get connection");
            }
        }
    }
}
