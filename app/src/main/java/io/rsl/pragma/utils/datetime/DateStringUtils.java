package io.rsl.pragma.utils.datetime;

import java.util.Calendar;
import java.util.Locale;

public class DateStringUtils {

    public static String getHM(Calendar calendar) {
        return String.format(Locale.getDefault(),  "%s:%s",
                addZeroIfSingle(calendar.get(Calendar.HOUR_OF_DAY)),
                addZeroIfSingle(calendar.get(Calendar.MINUTE)));
    }

    public static String getDMY(Calendar calendar) {
        return String.format(Locale.getDefault(),  "%s.%s.%d",
                addZeroIfSingle(calendar.get(Calendar.DAY_OF_MONTH)),
                addZeroIfSingle(calendar.get(Calendar.MONTH)),
                calendar.get(Calendar.YEAR));
    }

    public static String getFullDate(Calendar calendar) {
        return getDMY(calendar) + " " + getHM(calendar);
    }

    private static String addZeroIfSingle(int num) {
        if(num < 10) {
            return "0" + String.valueOf(num);
        } else {
            return String.valueOf(num);
        }
    }

}
