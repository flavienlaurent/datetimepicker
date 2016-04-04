package com.fourmob.datetimepicker;

public final class ArrayUtils {

    private ArrayUtils() {
    }

    public static boolean contains(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return true;
            }
        }
        return false;
    }
}
