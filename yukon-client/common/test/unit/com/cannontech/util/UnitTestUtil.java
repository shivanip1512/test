package com.cannontech.util;

import org.joda.time.Duration;

public class UnitTestUtil {

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
