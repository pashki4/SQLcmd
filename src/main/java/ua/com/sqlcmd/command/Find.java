package ua.com.sqlcmd.command;

import ua.com.sqlcmd.database.DataSet;
import ua.com.sqlcmd.database.DatabaseManager;
import ua.com.sqlcmd.view.View;

import java.util.*;

public class Find implements Command {
    private static final String PLUS = "+";
    private static final String MINUS = "-";
    private static final String DELIMITER = "|";
    private static final String WHITESPACE = " ";
    private static final String NEW_LINE = System.lineSeparator();
    private final View view;
    private final DatabaseManager manager;
    private final Map<String, Integer> maxLength = new HashMap<>();

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
            throw new IllegalArgumentException(String.format("Невірний формат, потрібно: find|tableName, а було: %s",
                    command));
        } else {
            String tableName = input[1];
            Set<String> strings = manager.getTables();
            if (!strings.contains(tableName)) {
                throw new IllegalArgumentException("Введена невірна назва таблиці: " + tableName);
            }
            List<DataSet> tableData = manager.getTableData(tableName);
            printTableData(tableData);
        }
    }

    private void printTableData(List<DataSet> dataSet) {
        findMaxLengthInRows(dataSet);
        Set<String> columnNames = dataSet.get(0).getColumnNames();
        StringBuilder sb = new StringBuilder();

        addContour(columnNames, sb);
        addColumnNames(dataSet, sb);
        addContour(columnNames, sb);
        addValues(dataSet, sb);
        addContour(columnNames, sb);
        view.write(sb.toString());
    }

    private void addValues(List<DataSet> dataSet, StringBuilder sb) {
        Set<String> columnNames = dataSet.get(0).getColumnNames();
        for (DataSet data : dataSet) {
            List<Object> objects = data.getValues();
            List<String> values = convertFromObjToString(objects);
            var listNames = new ArrayList<>(columnNames);
            for (int i = 0; i < values.size(); i++) {
                int maxWidth = maxLength.get(listNames.get(i));
                int spaceSize = maxWidth - values.get(i).length();
                int prefixSize = spaceSize / 2;
                int suffixSize = (spaceSize + 1) / 2;
                if (maxWidth > values.get(i).length()) {
                    sb.append(DELIMITER)
                            .append(WHITESPACE.repeat(prefixSize))
                            .append(values.get(i))
                            .append(WHITESPACE.repeat(suffixSize));
                } else {
                    sb.append(DELIMITER)
                            .append(values.get(i));
                }
            }
            sb.append(DELIMITER)
                    .append(NEW_LINE);
        }
    }

    private List<String> convertFromObjToString(List<Object> values) {
        List<String> result = new ArrayList<>(values.size());
        for (Object value : values) {
            result.add(String.valueOf(value));
        }
        return result;
    }

    private void addContour(Set<String> columnNames, StringBuilder sb) {
        for (String columnName : columnNames) {
            int width = maxLength.get(columnName);
            sb.append(PLUS).append(MINUS.repeat(width));
        }
        sb.append(PLUS).append(NEW_LINE);
    }

    private void addColumnNames(List<DataSet> dataSets, StringBuilder sb) {
        Set<String> columnNames = dataSets.get(0).getColumnNames();
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

    private void findMaxLengthInRows(List<DataSet> dataSet) {
        if (dataSet.size() != 0) {
            Set<String> columnNames = dataSet.get(0).getColumnNames();

            int maxLength;
            for (int i = 0; i < columnNames.size(); i++) {
                List<String> listNames = new ArrayList<>(columnNames);
                maxLength = listNames.get(i).length();
                for (DataSet data : dataSet) {
                    Object o = data.get(listNames.get(i));
                    String value = String.valueOf(o);
                    if (value.length() > maxLength) {
                        maxLength = value.length();
                    }
                }
                this.maxLength.put(listNames.get(i), maxLength);
            }
        }
    }
}
