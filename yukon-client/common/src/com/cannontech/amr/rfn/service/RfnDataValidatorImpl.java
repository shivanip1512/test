package com.cannontech.amr.rfn.service;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public final class RfnDataValidatorImpl implements RfnDataValidator {

    private static final DateTime y2k = new LocalDate(2000, 1, 1).toDateTimeAtStartOfDay();
    private static final Duration year = Duration.standardDays(365);

    @Autowired private GlobalSettingDao globalSettingDao;

    /**
     * Checks that the timestamp is "reasonable," that is, between 1-Jan-2000 and now + 1 year.
     */
    @Override
    public boolean isTimestampValid(Instant timestamp, Instant now) {
        return timestamp.isAfter(y2k) && timestamp.isBefore(now.plus(year));
    }

    /**
     * Checks that the timestamp is within the "recent" interval defined by the Global setting. If the global setting
     * is set to 0, then {@link #isTimestampValid(Instant, Instant)} is checked instead.
     */
    @Override
    public boolean isTimestampRecent(Instant timestamp, Instant now) {
        int monthsToSubtract =
                globalSettingDao.getInteger(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT);

        if (monthsToSubtract == 0) {
            return isTimestampValid(timestamp, now);
        }
        Duration monthInDuration = Period.months(monthsToSubtract).toDurationTo(now);
        return timestamp.isAfter(now.minus(monthInDuration)) && timestamp.isBefore(now.plus(year));
    }
}