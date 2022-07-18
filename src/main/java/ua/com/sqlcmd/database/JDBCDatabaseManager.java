package ua.com.sqlcmd.database;

import java.sql.*;
import java.util.Arrays;
import java.util.Objects;

public class JDBCDatabaseManager implements DatabaseManager {
    private boolean isConnected;
    private String database;
    private String userName;
    private String password;

    private static final String SQL_INSERT = "INSERT INTO employee (name, salary) VALUES (?, ?)";

    private static final String SQL_UPDATE = "UPDATE employee SET SALARY=? WHERE NAME=?";

    private static final String SQL_DELETE = "DELETE FROM employee WHERE salary < 12000";

    private static final String SQL_SELECT = "SELECT * FROM employee";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS employee\n" +
            "(\n" +
            "    ID serial,\n" +
            "    NAME varchar(100) NOT NULL,\n" +
            "    SALARY numeric(15, 2) NOT NULL,\n" +
            "    PRIMARY KEY (ID)\n" +
            ");";

    @Override
    public void connect(String database, String userName, String password) {
        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/" + database,
                        userName, password)) {
            this.database = database;
            this.userName = userName;
            this.password = password;
            isConnected = true;

        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Cant get connection for database: %s, userName: %s, password: %s",
                            database, userName, password), e);
        }
    }

    @Override
    public String[] getTables() {
        String getTables = "SELECT table_name\n" +
                "  FROM information_schema.tables\n" +
                " WHERE table_schema='public'\n" +
                "   AND table_type='BASE TABLE';";

        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/"
                        + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(getTables);
            ResultSet resultSet = preparedStatement.executeQuery();
            String[] result = new String[100];
            int index = 0;
            while (resultSet.next()) {
                result[index++] = resultSet.getString("table_name");
            }
            result = Arrays.copyOfRange(result, 0, index);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[]{"Empty"};
        }
    }

    @Override
    public DataSet[] getTableData(String tableName) {
        String sqlSelectRowCount = "SELECT COUNT(*) FROM " + tableName + ";";
        String sqlSelectAll = "SELECT * FROM " + tableName + ";";
        try (Connection connection =
                     DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/"
                             + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectRowCount);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            int dataSetCount = resultSet.getInt(1);

            DataSet[] dataSet = new DataSet[dataSetCount];
            preparedStatement = connection.prepareStatement(sqlSelectAll);
            resultSet = preparedStatement.executeQuery();
            int dataSetFreeIndex = 0;
            while (resultSet.next()) {
                DataSet newDataSet = new DataSet();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    newDataSet.valueOf(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                }
                dataSet[dataSetFreeIndex++] = newDataSet;
            }
            return dataSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return new DataSet[0];
        }
    }


    @Override
    public void clear(String tableName) {
        String sqlDelete = "DELETE FROM " + tableName + ";";
        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/" + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(DataSet dataSet, String tableName) {
        String columnNames = prepareColumnNames(dataSet);
        String values = prepareValues(dataSet);
        String sqlInsert = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + values + ")";
        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/"
                        + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
            Object[] input = dataSet.getValues();
            int parameterIndex = 1;
            for (int i = 0; i < dataSet.getValues().length; i++) {
                preparedStatement.setObject(parameterIndex++, input[i]);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String prepareColumnNames(DataSet dataSet) {
        return String.join(", ", dataSet.getColumnNames());
    }

    private static String prepareValues(DataSet dataSet) {
        String questionMark = "?";
        String[] questionMarkSeq = new String[dataSet.getValues().length];
        Arrays.fill(questionMarkSeq, questionMark);
        return String.join(", ", questionMarkSeq);
    }

    @Override
    public void createTable() {
        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/"
                        + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_TABLE);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTableData(String tableName, int id, DataSet newValue) {
        String[] columnNames = newValue.getColumnNames();
        Object[] values = newValue.getValues();
        String formattedColumnNames = String.join(" = ?, ", columnNames) + " = ?";

        String sqlUpdate = "UPDATE " + tableName + " SET " + formattedColumnNames +
                " WHERE id = " + id;

        try (Connection connection = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/"
                        + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
            int parameterIndex = 1;
            for (int i = 0; i < columnNames.length; i++) {
                preparedStatement.setObject(parameterIndex++, values[i]);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void printTableData(DataSet[] dataSet) {
        if (dataSet.length == 0) {

            System.out.println("*-------*");
        } else {
            String[] columnNames = dataSet[0].getColumnNames();
            StringBuilder sb = new StringBuilder();
            sb.append(String.join(" ", columnNames));
            sb.append("\n");
            for (DataSet data : dataSet) {
                Object[] values = data.getValues();
                String[] strings = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    strings[i] = String.valueOf(values[i]);
                }
                sb.append(String.join(" ", strings));
                sb.append("\n");
            }
            System.out.println(sb);
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
}


