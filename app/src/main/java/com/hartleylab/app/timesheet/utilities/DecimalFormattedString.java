package com.hartleylab.app.timesheet.utilities;

public class DecimalFormattedString {

    public static String getTwoDecimalFormattedString(String stringToFormat) {
        return String.format("%.2f", Double.valueOf(stringToFormat));
    }
}
