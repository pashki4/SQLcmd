package ua.com.sqlcmd.database;

import java.util.Arrays;

public class Testing {
    public static void main(String[] args) {
        int[] array = new int[5];
        array[0] = 9;
        array[1] = 8;
        System.out.println(Arrays.toString(array));
        array = Arrays.copyOfRange(array, 0, 2);
        System.out.println(Arrays.toString(array));
    }
}
