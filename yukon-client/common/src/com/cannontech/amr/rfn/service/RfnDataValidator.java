package com.cannontech.amr.rfn.service;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

public final class RfnDataValidator {
    
    private static final DateTime y2k = new LocalDate(2000, 1, 1).toDateTimeAtStartOfDay();
    private static final Duration year = Duration.standardDays(365);

    /**
     * Checks that the timestamp is between 1-Jan-2000 and 1 year from now
     * @param timestamp The timestamp to validate
     * @param now The current time
     */
    public static boolean isTimestampValid(Instant timestamp, Instant now) {
        return timestamp.isAfter(y2k) && timestamp.isBefore(now.plus(year)); 
    }
}
