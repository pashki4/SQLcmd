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
        DataSource dataSource = null;
//        try (Connection connection = DriverManager
//                .getConnection("jdbc:postgresql://127.0.0.1:5432/mytestdb",
//                        "postgres",
//                        "1234")) {
//
//            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_TABLE);
//            preparedStatement.execute();
//        }

        //UPDATE
        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/mytestdb",
                        "postgres",
                        "1234")) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE);
            preparedStatement.setLong(1, 20000);
            preparedStatement.setString(2, "Nataly");
            preparedStatement.executeUpdate();
        }

//        preparedStatement.execute() – Normally for DDL like CREATE or DROP
//        preparedStatement.executeUpdate() – Normally for DML like INSERT, UPDATE, DELETE
//        preparedStatement.executeQuery() – Run SELECT query and return a ResultSet
//        preparedStatement.executeBatch() – Run SQL commands as a batch


//        //INSERT | UPDATE
//        PreparedStatement preparedStatement =
//                connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
//        preparedStatement.setString(1, "Nataly");
//        preparedStatement.setDouble(2, 12999.57);
//        preparedStatement.executeUpdate();


//        //SELECT
        String getTables = "SELECT table_name\n" +
                "  FROM information_schema.tables\n" +
                " WHERE table_schema='public'\n" +
                "   AND table_type='BASE TABLE';";
        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/mytestdb",
                        "postgres",
                        "1234")) {
            PreparedStatement preparedStatement = connection.prepareStatement(getTables);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("table_name"));
            }
        }
    }
}

