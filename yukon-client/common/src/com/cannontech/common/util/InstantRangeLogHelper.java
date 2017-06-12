package com.cannontech.common.util;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class InstantRangeLogHelper {
    
    final public static DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");

    public static String getLogString(Range<Instant> range) {

        if (range == null) {
            return "";
        }
        String min = range.getMin() == null ? "" : getLogString(range.getMin());
        String max = range.getMax() == null ? "" : getLogString(range.getMax());
        String includesMin = " [exclusive] ";
        String includesMax = " [exclusive] ";
        if (range.isIncludesMinValue()) {
            includesMin = " [inclusive] ";
        }
        if (range.isIncludesMaxValue()) {
            includesMax = " [inclusive] ";
        }

        return "Range: " + includesMin + min + " - " + includesMax + max + " ";
    }

    public static String getLogString(Instant date) {
        if (date == null) {
            return "";
        }
        return date.toString(df.withZone(DateTimeZone.getDefault()));
    }
    
    public static String getPartialString(Range<Instant> range) {
        if (range == null) {
            return "";
        }
        String min = range.getMin() == null ? "" : getLogString(range.getMin());
        String max = range.getMax() == null ? "" : getLogString(range.getMax());
        return "Range: " + min + " - " +  max + " ";
    }
}
