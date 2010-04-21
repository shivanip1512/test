package com.cannontech.core.roleproperties;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class RolePropertyTypeHelper {
    public static LocalTime asLocalTime(String rolePropertyValue) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.UTC);
        DateTime dateTime = formatter.parseDateTime(rolePropertyValue);
        LocalTime localTime = dateTime.toLocalTime();
        return localTime;
    }
}
