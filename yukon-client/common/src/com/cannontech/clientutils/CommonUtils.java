package com.cannontech.clientutils;

public final class CommonUtils {
    public static String createString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public static String formatDate(java.util.Date date) {
        if (date == null) {
            return "Null";
        }

        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        return formatter.format(date);
    }

    public static String formatDecimalPlaces(double value, int decPlaces) {
        java.text.DecimalFormat doubleToLong = new java.text.DecimalFormat();
        doubleToLong.setParseIntegerOnly(true);

        String pattern = null;

        if (decPlaces == 0) {
            pattern = new String("0");
        } else {
            pattern = new String("0.");

            for (int i = 0; i < decPlaces; i++) {
                pattern += "0";
            }
        }

        // apply the pattern
        doubleToLong.applyPattern(pattern);

        // By not returning a Double object, the column class becomes a String
        return doubleToLong.format(value);
    }

    public static String formatMonthString(int monthNumber) {
        if (monthNumber < 0 || monthNumber > 11) {
            throw new IllegalArgumentException(monthNumber + " is not a valid month number");
        }

        switch (monthNumber) {
        case 0:
            return "January";
        case 1:
            return "February";
        case 2:
            return "March";
        case 3:
            return "April";
        case 4:
            return "May";
        case 5:
            return "June";
        case 6:
            return "July";
        case 7:
            return "August";
        case 8:
            return "September";
        case 9:
            return "October";
        case 10:
            return "November";
        case 11:
            return "December";
        default:
            return "NUL";
        }
    }

    /**
     * This method will return the java.awt.Dialog associated with a component
     * If no parent dialog is found null will be returned
     * MAKE SURE A DIALOG OR JDIALOG IS THERE OR NULL IS RETURNED!!!
     */
    public final static java.awt.Dialog getParentDialog(java.awt.Component comp) {
        while (comp != null && !(comp instanceof java.awt.Dialog)) {
            comp = comp.getParent();
        }

        return (java.awt.Dialog) comp;
    }

    public static String stringDelimitor(String delimitor, Object[] strings) {
        if (strings == null) {
            return null;
        }

        String delimitedString = new String();

        for (int i = 0; i < strings.length; i++) {
            delimitedString += strings[i].toString();

            if (i != (strings.length - 1)) {
                delimitedString += delimitor;
            }
        }

        return delimitedString;
    }
}
