package com.cannontech.common.util;

import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Iso8601DateUtil {

    /**
     * Helper method to parse Iso8601 date string into Date
     * Parses ISO date string into a Date.
     * The ISO string may either contain a UTC zone indicator of "Z"
     * Ex: 2008-10-13T12:30:00Z
     * Or a time zone offset in the format "+|-HH:mm"
     * Ex: 2008-10-13T06:30:00-06:00
     * @param dateString
     * @return
     */
    public static Date parseIso8601Date(String dateString) {
    	DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();
		return dateTimeFormatter.parseDateTime(dateString).toDate();
    }

    /**
     * Helper method to format date into Iso8601 string
     * Returns date formatted as ISO with UTC zone.
     * Ex: 2008-10-13T12:30:00Z
     * @param d
     * @return
     */
    public static String formatIso8601Date(Date date) {
        return formatIso8601Date(date, false);
    }

    /**
     * Helper method to format date into Iso8601 string
     * Returns date formatted as ISO with UTC zone.
     * Ex: 2018-11-30T20:01:00.000Z
     */
    public static String formatIso8601Date(Date date, boolean includeMillis) {
        DateTime dateTime = new DateTime(date, DateTimeZone.UTC);
        if (includeMillis) {
            return ISODateTimeFormat.dateTime().print(dateTime);
        } else {
            return ISODateTimeFormat.dateTimeNoMillis().print(dateTime);
        }
    }
    
    /**
     * Helper method to format date into Iso8601 string with timezone.
     * Used to output a date as local time with no time zone suffix.  
     * @param date
     * @param tz
     * @return
     */
    public static String formatIso8601Date(Date date, TimeZone tz) {

    	DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis().withZone(DateTimeZone.forTimeZone(tz));

    	String formattedString = dateTimeFormatter.print(date.getTime());
    	formattedString = formattedString.substring(0, 19);

    	return formattedString;
	}
}
