package com.cannontech.util;

/**
 * Gets and return values based on type. Returns the given
 * default value in case of an exception.
 *
 */
public class GetterUtil {

    public static int get(String value, int defaultValue) {
        try {
            return Integer.parseInt(_trim(value));
        } catch (Exception e) {}

        return defaultValue;
    }

    public static String get(String value, String defaultValue) {
        if (Validator.isNotNull(value)) {
            value = value.trim();

            return value;
        }

        return defaultValue;
    }

    private static String _trim(String value) {
        if (value != null) {
            value = value.trim();

            StringBuffer sb = new StringBuffer();

            char[] charArray = value.toCharArray();

            for (int i = 0; i < charArray.length; i++) {
                if ((Character.isDigit(charArray[i])) || (charArray[i] == '-' && i == 0) || (charArray[i] == '.')) {

                    sb.append(charArray[i]);
                }
            }

            value = sb.toString();
        }

        return value;
    }
}