package ua.com.sqlcmd.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Testing {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        try (Connection connection =
                     DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/"
                             + "mytestdb", "postgres", "1234");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM airplane")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            int index = 1;
            while (index <= columnCount) {
                list.add(resultSet.getMetaData().getColumnName(index++));
            }
            System.out.println(list);
        } catch (Exception e) {

        }
    }
}
