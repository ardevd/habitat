package no.aegisdynamics.habitat.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private final static String FORMAT_FULL_TIMESTAMP = "yyyy-MM-dd HH:mm";
    private final static String FORMAT_SIMPLE_TIMESTAMP = "HH:mm";

    public static String getDefaultTimestampString() {
        return new SimpleDateFormat(FORMAT_SIMPLE_TIMESTAMP, Locale.getDefault()).format(new Date());
    }

    public static boolean isValidAutomationTimestamp(String timestamp) {
        DateFormat simpleFormat = new SimpleDateFormat(FORMAT_SIMPLE_TIMESTAMP, Locale.getDefault());
        try {
            simpleFormat.parse(timestamp);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
}
