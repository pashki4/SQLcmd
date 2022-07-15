package ua.com.sqlcmd.database;

public class DataSet {
    private Data[] data = new Data[100]; //TODO change magic number
    private int freeIndex = 0;

    static class Data {
        private String name;
        private Object value;

        Data(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name + " : " + value;
        }
    }

    public void valueOf(String name, Object value) {
        data[freeIndex++] = new Data(name, value);
    }

    public String[] getColumnNames() {
        String[] result = new String[freeIndex];
        for (int i = 0; i < freeIndex; i++) {
            result[i] = data[i].getName();
        }
        return result;
    }

    public Object[] getValues() {
        Object[] result = new Object[freeIndex];
        for (int i = 0; i < freeIndex; i++) {
            result[i] = data[i].getValue();
        }
        return result;
    }

    public void updateValue(DataSet newValue) {
        for (int i = 0; i < newValue.freeIndex; i++) {
            Data newData = newValue.data[i];
            for (int j = 0; j < freeIndex; j++) {
                if (newData.name.equals(data[j].name)) {
                    data[i].value = newData.value;
                }
            }
        }
    }

    public Object get(String name) {
        for (int i = 0; i < freeIndex; i++) {
            if (data[i].name.equals(name)) {
                return data[i].value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < freeIndex; i++) {
            sb.append(data[i]);
            sb.append("\n");
        }
        return sb.toString();
    }
}
