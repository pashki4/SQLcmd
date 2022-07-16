package ua.com.sqlcmd.database;

import javax.sql.DataSource;
import java.sql.*;

public class Main {

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS employee\n" +
            "(\n" +
            "    ID serial,\n" +
            "    NAME varchar(100) NOT NULL,\n" +
            "    SALARY numeric(15, 2) NOT NULL,\n" +
            "    CREATED_DATE timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "    PRIMARY KEY (ID)\n" +
            ");";

    private static final String SQL_INSERT = "INSERT INTO employee (name, salary) VALUES (?, ?)";

    private static final String SQL_UPDATE = "UPDATE employee SET SALARY=? WHERE NAME=?";

    private static final String SQL_DELETE = "DELETE FROM employee WHERE salary < 12000";

    private static final String SQL_SELECT = "SELECT * FROM employee";

    public static void main(String[] args) throws SQLException {

        //SELECT
        String getTables = "SELECT * FROM employee;";
        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/mytestdb",
                        "postgres",
                        "1234")) {
            PreparedStatement preparedStatement = connection.prepareStatement(getTables);
            ResultSet resultSet = preparedStatement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            resultSet.next();
            String result = "";
            for (int i = 1; i <= columnCount; i++) {
                result += resultSet.getMetaData().getColumnName(i);
            }
            System.out.println(result);
        }
    }
}

