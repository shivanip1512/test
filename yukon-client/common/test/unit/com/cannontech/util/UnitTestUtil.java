package com.cannontech.util;

import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;

public class UnitTestUtil {

    private static AtomicLong accumulatedTimeAdjustments = new AtomicLong();

    /**
     * Causes calls to new Instant() to return a value this number of seconds adjusted
     */
    public static void adjustSystemTimeBySeconds(int numberOfSeconds) {
        DateTimeUtils.setCurrentMillisOffset(accumulatedTimeAdjustments.addAndGet((long) numberOfSeconds * 1000));
    }

    /**
     * There are cases in this test where we need handle "now" changing while the test is running.
     * This method will return true if the two durations are within a single second of each other.
     */
    public static boolean areClose(Duration duration1, Duration duration2, Duration allowedDeviation) {
        return duration1.minus(allowedDeviation).isShorterThan(duration2)
            && duration1.plus(allowedDeviation).isLongerThan(duration2);
    }

    public static boolean withinOneSecond(Duration duration1, Duration duration2) {
        return areClose(duration1, duration2, Duration.standardSeconds(1));
    }

    public static boolean withinOneMinute(Duration duration1, Duration duration2) {
        return areClose(duration1, duration2, Duration.standardMinutes(1));
    }
}
