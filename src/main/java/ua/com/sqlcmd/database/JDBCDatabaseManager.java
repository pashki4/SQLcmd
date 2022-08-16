package ua.com.sqlcmd.database;

import java.sql.*;
import java.util.*;

public class JDBCDatabaseManager implements DatabaseManager {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String QUESTION_MARK = "?";
    private static final String DATABASE_URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static final String SQL_SELECT_TABLE_NAMES =
            "SELECT table_name" + NEW_LINE
            + " FROM information_schema.tables" + NEW_LINE
            + " WHERE table_schema='public'" + NEW_LINE
            + " AND table_type='BASE TABLE';";

    private String database;
    private String userName;
    private String password;
    private boolean isConnected;

    @Override
    public void connect(String database, String userName, String password) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL + database, userName, password)) {
            this.database = database;
            this.userName = userName;
            this.password = password;
            isConnected = true;
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Не можу отримати з'єднання з такими параметрами: " +
                                    NEW_LINE + "база: %s, юзер: %s, пароль: %s",
                            database, userName, password), e);
        }
    }

    @Override
    public Set<String> getTables() {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_TABLE_NAMES)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            Set<String> result = new LinkedHashSet<>();
            while (resultSet.next()) {
                result.add(resultSet.getString("table_name"));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка отримання списку таблиць ", e);
        }
    }

    @Override
    public List<DataSet> getTableData(String tableName) {
        String sqlSelectAll = "SELECT * FROM public." + tableName + ";";
        try (Connection connection =
                     DriverManager.getConnection(DATABASE_URL + database, userName, password);
             PreparedStatement preparedStatementSelectRowCount = connection.prepareStatement(sqlSelectAll)) {
            ResultSet resultSet = preparedStatementSelectRowCount.executeQuery();

            Set<String> columnNames = getColumnNames(resultSet);
            var listNames = new ArrayList<>(columnNames);
            List<DataSet> columnNamesWithEmptyValues = new ArrayList<>(1);
            DataSet temp = new DataSetImpl();
            for (int i = 0; i < columnNames.size(); i++) {
                temp.put(listNames.get(i), "");
            }
            columnNamesWithEmptyValues.add(temp);

            int rowCount = getRowCount(tableName);
            if (rowCount == 0) {
                return columnNamesWithEmptyValues;
            } else {
                List<DataSet> result = new ArrayList<>(rowCount);
                while (resultSet.next()) {
                    DataSet newDataSet = new DataSetImpl();
                    for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                        newDataSet.put(resultSet.getMetaData().getColumnName(i + 1), resultSet.getObject(i + 1));
                    }
                    result.add(newDataSet);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Помилка отримання вмісту таблиці '%s'", tableName), e);
        }
    }

    private int getRowCount(String tableName) {
        String sqlSelectRowCount = "SELECT COUNT(*) FROM public." + tableName + ";";
        try (Connection connection =
                     DriverManager.getConnection(DATABASE_URL + database, userName, password);
             PreparedStatement preparedStatementSelectRowCount = connection.prepareStatement(sqlSelectRowCount)) {
            ResultSet resultSet = preparedStatementSelectRowCount.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> getColumnNames(ResultSet resultSet) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        Set<String> columnNames = new LinkedHashSet<>();
        var listNames = new ArrayList<>(columnNames);
        int index = 1;
        while (index <= columnCount) {
            listNames.add(resultSet.getMetaData().getColumnName(index++));
        }
        return columnNames;
    }

    @Override
    public void clear(String tableName) {
        String sqlDelete = "DELETE FROM " + tableName + ";";
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Помилка очищення таблиці '%s'", tableName), e);
        }
    }

    @Override
    public void insert(DataSet dataSet, String tableName) {
        String columnNames = prepareColumnNames(dataSet);
        String values = prepareValues(dataSet);
        String sqlInsert = "INSERT INTO public." + tableName + " (" + columnNames + ") VALUES (" + values + ")";
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
            List<Object> input = dataSet.getValues();
            int parameterIndex = 1;
            for (int i = 0; i < dataSet.getValues().size(); i++) {
                preparedStatement.setObject(parameterIndex++, input.get(i));
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Помилка додавання запису до таблиці '%s'", tableName), e);
        }
    }

    private static String prepareColumnNames(DataSet dataSet) {
        return String.join(", ", dataSet.getColumnNames());
    }

    private static String prepareValues(DataSet dataSet) {
        String[] questionMarkSeq = new String[dataSet.getValues().size()];
        Arrays.fill(questionMarkSeq, QUESTION_MARK);
        return String.join(", ", questionMarkSeq);
    }

    @Override
    public void createTable(String queryCreateTable) {
        String[] input = queryCreateTable.split("[|]");
        String tableName = input[1];
        String sqlCreateTable = createSqlCreateQuery(input, tableName);

        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateTable)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Помилка створення таблиці '%s'", tableName), e);
        }
    }

    private String createSqlCreateQuery(String[] input, String tableName) {
        String sqlCreateTable = "CREATE TABLE public." + tableName + " (ID serial, ";
        String[] varcharColumnNames = addVarcharToColumnNames(input);

        sqlCreateTable += String.join(", ", varcharColumnNames);
        sqlCreateTable += ", PRIMARY KEY(id));";
        return sqlCreateTable;
    }

    private String[] addVarcharToColumnNames(String[] input) {
        String[] columnNames = Arrays.copyOfRange(input, 2, input.length);
        String[] columnNamesVarchar = new String[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNamesVarchar[i] = columnNames[i].toUpperCase() + " varchar(30) NOT NULL";
        }
        return columnNamesVarchar;
    }

    @Override
    public void updateTableData(String tableName, String columnName, String columnValue, DataSet newValue) {
        Set<String> columnNames = newValue.getColumnNames();
        var listNames = new ArrayList<>(columnNames);
        List<Object> values = newValue.getValues();
        String formattedColumnNames = "";
        if (columnNames.size() == 1) {
            formattedColumnNames = listNames.get(0) + " = ?";
        } else {
            formattedColumnNames = String.join(" = ?, ", columnNames) + " = ?";
        }
        String sqlUpdate = "UPDATE " + tableName + " SET " + formattedColumnNames +
                " WHERE " + columnName + " = '" + columnValue + "'";

        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            int parameterIndex = 1;
            for (int i = 0; i < listNames.size(); i++) {
                preparedStatement.setObject(parameterIndex++, values.get(i));
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Помилка оновлення значення в таблиці '%s' ", tableName), e);
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
}


