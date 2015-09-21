package com.fourmob.datetimepicker.util;

import android.support.v4.util.Pair;

import java.util.Calendar;
import java.util.List;

public class DatePickerUtils {
    public static Pair<Integer, Integer> getYearRange(List<Long> dates) {
        if (dates == null || dates.isEmpty()) {
            throw  new IllegalArgumentException("Dates can't be null or empty");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dates.get(0));

        int minYear = calendar.get(Calendar.YEAR);
        int maxYear = minYear;

        for (Long time: dates) {
            calendar.clear();
            calendar.setTimeInMillis(time);

            minYear = Math.min(minYear, calendar.get(Calendar.YEAR));
            maxYear = Math.max(maxYear, calendar.get(Calendar.YEAR));
        }

        return Pair.create(minYear, maxYear);
    }
}
