package com.cannontech.amr.rfn.service;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public final class RfnDataValidator {
    
    private static final DateTime y2k = new LocalDate(2000, 1, 1).toDateTimeAtStartOfDay();
    private static final Duration year = Duration.standardDays(365);
    private static GlobalSettingDao globalSettingDao = YukonSpringHook.getBean("globalSettingDao", GlobalSettingDao.class);

    /**
     * Checks that the timestamp is between 1-Jan-2000 or calculated date and 1 year from now
     * Use this method only for checking for a "reasonable" timestamp.
     * Expectation is that when "not valid" the timestamp and quality may be adjusted. 
     * Examples of "unreasonable" would be the year 1970 or 2106).
     * @param timestamp The timestamp to validate
     * @param now The current time
     */
    public static boolean isTimestampValid(Instant timestamp, Instant now) {
        return timestamp.isAfter(y2k) && timestamp.isBefore(now.plus(year));
    }

    /**
     * Checks {@link #isTimestampValid(Instant, Instant)}
     * Then, checks to see if timestamp is within "recent" threshold such that we should process is.
     * Calculated date/threshold depends on the months value set in global setting.
     * Use this method for checking for a "reasonable" AND "recent" timestamp to consume.
     * Expectation is that when "not valid" AND "not recent" that data is thrown away.
     * @param timestamp The timestamp to validate
     * @param now The current time
     */
    public static boolean isTimestampRecent(Instant timestamp, Instant now) {
        int monthsToSubtract =
                globalSettingDao.getInteger(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT);

        if (monthsToSubtract == 0) {
            return isTimestampValid(timestamp, now);
        }
        Duration monthInDuration = Period.months(monthsToSubtract).toDurationTo(now);
        return timestamp.isAfter(now.minus(monthInDuration)) && timestamp.isBefore(now.plus(year));
    }
}
