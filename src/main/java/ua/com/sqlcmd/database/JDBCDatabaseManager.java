package ua.com.sqlcmd.database;

import java.sql.*;
import java.util.*;

public class JDBCDatabaseManager implements DatabaseManager {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String QUESTION_MARK = "?";
    private static final String DATABASE_URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static final String SQL_SELECT_TABLE_NAMES_SQL = "SELECT table_name" + NEW_LINE
            + " FROM information_schema.tables" + NEW_LINE
            + " WHERE table_schema='public'" + NEW_LINE
            + " AND table_type='BASE TABLE';";
    private static final String SELECT_ALL_SQL = "SELECT * FROM public.";
    private static final String SELECT_ALL_COUNT_SQL = "SELECT COUNT(*) FROM public.";
    private static final String DELETE_SQL = "DELETE FROM public.";
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
            throw new RuntimeException("Не можу отримати з'єднання з такими параметрами: %n"
                    + "база: %s, юзер: %s, пароль: %s%n".formatted(database, userName, password), e);
        }
    }

    @Override
    public Set<String> getTables() {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_TABLE_NAMES_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            Set<String> result = new LinkedHashSet<>();
            while (resultSet.next()) {
                result.add(resultSet.getString("table_name"));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("помилка отримання списку таблиць%n".formatted(), e);
        }
    }

    @Override
    public List<DataSet> getTableData(String tableName) {
        try (Connection connection =
                     DriverManager.getConnection(DATABASE_URL + database, userName, password)) {
            PreparedStatement preparedStatementSelectRowCount = connection.prepareStatement(SELECT_ALL_SQL + tableName);
            ResultSet resultSet = preparedStatementSelectRowCount.executeQuery();

            int rowCount = getRowCount(tableName);
            if (rowCount == 0) {
                DataSet emptyDataSetWithColumnNames = createEmptyDataSetWithNames(resultSet);
                return getEmptyListWithNames(emptyDataSetWithColumnNames);
            } else {
                return getAllData(resultSet, rowCount);
            }
        } catch (SQLException e) {
            throw new RuntimeException("помилка отримання вмісту таблиці '%s'%n".formatted(tableName), e);
        }
    }

    private static List<DataSet> getAllData(ResultSet resultSet, int rowCount) throws SQLException {
        List<DataSet> result = new ArrayList<>(rowCount);
        while (resultSet.next()) {
            DataSet temp = new DataSetImpl();
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                temp.put(resultSet.getMetaData().getColumnName(i + 1),
                        resultSet.getObject(i + 1));
            }
            result.add(temp);
        }
        return result;
    }

    private static List<DataSet> getEmptyListWithNames(DataSet temp) {
        List<DataSet> columnNamesWithEmptyValues = new ArrayList<>(1);
        columnNamesWithEmptyValues.add(temp);
        return columnNamesWithEmptyValues;
    }

    private DataSet createEmptyDataSetWithNames(ResultSet resultSet) throws SQLException {
        Set<String> columnNames = getColumnNames(resultSet);

        DataSet temp = new DataSetImpl();
        for (String columnName : columnNames) {
            temp.put(columnName, "");
        }
        return temp;
    }

    private Set<String> getColumnNames(ResultSet resultSet) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        Set<String> columnNames = new LinkedHashSet<>();
        int index = 1;
        while (index <= columnCount) {
            columnNames.add(resultSet.getMetaData().getColumnName(index++));
        }
        return columnNames;
    }

    private int getRowCount(String tableName) {
        try (Connection connection =
                     DriverManager.getConnection(DATABASE_URL + database, userName, password)) {
            PreparedStatement preparedStatementSelectRowCount = connection.prepareStatement(SELECT_ALL_COUNT_SQL + tableName);
            ResultSet resultSet = preparedStatementSelectRowCount.executeQuery();
            resultSet.next();
            int columnIndex = 1;
            return resultSet.getInt(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear(String tableName) {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL + tableName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("помилка очищення таблиці '%s'%n".formatted(tableName), e);
        }
    }

    @Override
    public void insert(DataSet dataSet, String tableName) {
        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password)) {
            PreparedStatement insertStatement = prepareInsertStatement(connection, dataSet, tableName);
            fillInsertStatementWithData(dataSet, insertStatement);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("помилка додавання запису до таблиці '%s'%n".formatted(tableName), e);
        }
    }

    private static void fillInsertStatementWithData(DataSet dataSet, PreparedStatement insertStatement) throws SQLException {
        List<Object> input = dataSet.getValues();
        int parameterIndex = 1;
        for (int i = 0; i < dataSet.getValues().size(); i++) {
            insertStatement.setObject(parameterIndex++, input.get(i));
        }
    }

    private PreparedStatement prepareInsertStatement(Connection connection, DataSet dataSet, String tableName) {
        try {
            String insertIntoSQL = collectSQLInsertQuery(dataSet, tableName);
            return connection.prepareStatement(insertIntoSQL);
        } catch (SQLException e) {
            throw new IllegalArgumentException("помилка при створенні insertStatement", e);
        }
    }

    private static String collectSQLInsertQuery(DataSet dataSet, String tableName) {
        String columnNames = prepareColumnNames(dataSet);
        String values = prepareValues(dataSet);
        String sqlInsert = "INSERT INTO public." + tableName + " (" + columnNames + ") VALUES (" + values + ")";
        return sqlInsert;
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
    public void createTable(String command) {
        String[] splitCommand = command.split("[|]");
        String tableName = splitCommand[1];
        String createSQL = collectSqlCreateQuery(splitCommand, tableName);

        try (Connection connection = DriverManager
                .getConnection(DATABASE_URL + database, userName, password)) {
            PreparedStatement createStatement = prepareCreateStatement(createSQL, connection);
            createStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("помилка створення таблиці '%s'%n".formatted(tableName), e);
        }
    }

    private PreparedStatement prepareCreateStatement(String createSQL, Connection connection) {
        try {
            return connection.prepareStatement(createSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Can't prepare statement to create table", e);
        }
    }

    private String collectSqlCreateQuery(String[] input, String tableName) {
        String result = "CREATE TABLE public." + tableName + " (ID serial, ";
        String[] varcharColumnNames = addVarcharToColumnNames(input);

        result += String.join(", ", varcharColumnNames);
        result += ", PRIMARY KEY(id));";
        return result;
    }

    private String[] addVarcharToColumnNames(String[] input) {
        String[] columnNames = Arrays.copyOfRange(input, 2, input.length);
        String[] result = new String[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            result[i] = columnNames[i].toUpperCase() + " varchar(30) NOT NULL";
        }
        return result;
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
                .getConnection(DATABASE_URL + database, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
            int parameterIndex = 1;
            for (int i = 0; i < listNames.size(); i++) {
                preparedStatement.setObject(parameterIndex++, values.get(i));
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("помилка оновлення значення в таблиці '%s'%n".formatted(tableName), e);
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
}


