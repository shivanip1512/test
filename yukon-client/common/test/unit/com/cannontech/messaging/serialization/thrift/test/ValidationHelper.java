package com.cannontech.messaging.serialization.thrift.test;

import com.cannontech.messaging.serialization.thrift.test.validator.ValidationResult;

public class ValidationHelper {
    public static String getCauseTrace(Throwable e) {

        StringBuilder sb = new StringBuilder();

        while (e != null) {
            sb.append(e.getClass().getSimpleName());
            sb.append(" (" + e.getMessage() + ")");

            e = e.getCause();
            if (e != null) {
                sb.append(" Caused by: ");
            }
        }

        return sb.toString();
    }

    public static String formatErrorString(ValidationResult result) {
        return "\n" + formatErrorString(0, result);
    }

    public static String formatErrorString(int tabs, ValidationResult result) {
        StringBuilder sb = new StringBuilder();

        if (!result.hasError()) {
            return sb.toString();
        }

        sb.append(printTabbedLine(tabs, result.toString()));
        
        tabs++;

        for (String error : result.getErrors()) {
            sb.append(printTabbedLine(tabs, error));
        }

        for (ValidationResult nestedRes : result.getNestedResults()) {
            sb.append(formatErrorString(tabs, nestedRes));
        }
        return sb.toString();
    }

    public static String printTabbedLine(int tabCount, String text) {
        StringBuilder sb = new StringBuilder();

        for (int i = tabCount; i > 0; i--) {
            sb.append("    ");
        }
        
        sb.append(text);
        sb.append("\n");
        
        return sb.toString();
    }
}
