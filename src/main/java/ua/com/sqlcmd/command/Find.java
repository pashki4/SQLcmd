package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Find implements Command {
    private View view;
    private DatabaseManager manager;
    private Map<String, Integer> maxLength = new HashMap<>();
    private static final String PLUS = "+";
    private static final String MINUS = "-";
    private static final String DELIMITER = "|";
    private static final String WHITESPACE = " ";
    private static final String NEW_LINE = "\n";

    public Find(View view, DatabaseManager manager) {
        this.view = view;
        this.manager = manager;
    }

    @Override
    public boolean canProcess(String command) {
        return command.startsWith("find|");
    }

    @Override
    public void process(String command) {
        String[] input = command.split("[|]");
        if (input.length != 2) {
            throw new IllegalArgumentException(String.format("Невірний формат, потрібно: find|tableName, а було: %s", command));
        } else {
            String tableName = input[1];
            List<String> strings = Arrays.asList(manager.getTables());
            if (!strings.contains(tableName)) {
                throw new IllegalArgumentException("Введена невірна назва таблиці: " + tableName);
            }
            DataSet[] tableData = manager.getTableData(tableName);
            printTableData(tableData);
        }
    }

    private void printTableData(DataSet[] dataSet) {
        findMaxLengthInRows(dataSet);
        String[] columnNames = dataSet[0].getColumnNames();
        StringBuilder sb = new StringBuilder();

        addContour(columnNames, sb);
        addColumnNames(dataSet, sb);
        addContour(columnNames, sb);
        addValues(dataSet, sb);
        addContour(columnNames, sb);
        view.write(sb.toString());
    }

    private void addValues(DataSet[] dataSet, StringBuilder sb) {
        String[] columnNames = dataSet[0].getColumnNames();
        for (DataSet data : dataSet) {
            Object[] objects = data.getValues();
            String[] values = convertFromObjToString(objects);

            for (int i = 0; i < values.length; i++) {
                int maxWidth = maxLength.get(columnNames[i]);
                int spaceSize = maxWidth - values[i].length();
                int prefixSize = spaceSize / 2;
                int suffixSize = (spaceSize + 1) / 2;
                if (maxWidth > values[i].length()) {
                    sb.append(DELIMITER)
                            .append(WHITESPACE.repeat(prefixSize))
                            .append(values[i])
                            .append(WHITESPACE.repeat(suffixSize));
                } else {
                    sb.append(DELIMITER)
                            .append(values[i]);
                }
            }
            sb.append(DELIMITER)
                    .append(NEW_LINE);
        }
    }

    private String[] convertFromObjToString(Object[] values) {
        String[] result = new String[values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.valueOf(values[i]);
        }
        return result;
    }

    private void addContour(String[] columnNames, StringBuilder sb) {
        for (String columnName : columnNames) {
            int width = maxLength.get(columnName);
            sb.append(PLUS).append(MINUS.repeat(width));
        }
        sb.append(PLUS)
                .append(NEW_LINE);
    }

    private void addColumnNames(DataSet[] dataSets, StringBuilder sb) {
        String[] columnNames = dataSets[0].getColumnNames();
        for (String columnName : columnNames) {
            int maxWidth = maxLength.get(columnName);
            int spaceSize = maxWidth - columnName.length();
            int prefixSize = spaceSize / 2;
            int suffixSize = (spaceSize + 1) / 2;
            if (maxWidth > columnName.length()) {
                sb.append(DELIMITER)
                        .append(WHITESPACE.repeat(prefixSize))
                        .append(columnName)
                        .append(WHITESPACE.repeat(suffixSize));
            } else {
                sb.append(DELIMITER)
                        .append(columnName);
            }
        }
        sb.append(DELIMITER)
                .append(NEW_LINE);
    }

    private void findMaxLengthInRows(DataSet[] dataSets) {
        String[] columnNames = null;
        int maxLength;
        if (dataSets.length != 0) {
            columnNames = dataSets[0].getColumnNames();

            for (int i = 0; i < columnNames.length; i++) {
                maxLength = columnNames[i].length();
                for (DataSet data : dataSets) {
                    Object o = data.get(columnNames[i]);
                    String value = String.valueOf(o);
                    if (value.length() > maxLength) {
                        maxLength = value.length();
                    }
                }
                this.maxLength.put(columnNames[i], maxLength);
            }
        }
    }
}
