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
     * Calculated date depends on the months value set in global setting.
     * @param timestamp The timestamp to validate
     * @param now The current time
     */
    public static boolean isTimestampValid(Instant timestamp, Instant now) {
        int monthsToSubstact =
                globalSettingDao.getInteger(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT);

        if (monthsToSubstact == 0) {
            return timestamp.isAfter(y2k) && timestamp.isBefore(now.plus(year));
        }
        Duration monthInDuration = Period.months(monthsToSubstact).toDurationTo(now);
        return timestamp.isAfter(now.minus(monthInDuration)) && timestamp.isBefore(now.plus(year));
    }
}
