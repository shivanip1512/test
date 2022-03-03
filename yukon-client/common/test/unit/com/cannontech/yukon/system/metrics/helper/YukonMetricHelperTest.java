package com.cannontech.yukon.system.metrics.helper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class YukonMetricHelperTest {
    @Test
    public void testCheckForEscapeValve() {
        YukonMetricHelper helper = new YukonMetricHelper();
        int escapeValveTime = 15;
        int intervalAfterEscapeValve = 5;

        // Lets assume current time is 12:15 AM for all the below scenarios.
        DateTime firstNotifiedTime = DateTime.now();
        // current time == notified time. so should return true.
        assertTrue(helper.checkForEscapeValve(escapeValveTime, firstNotifiedTime, intervalAfterEscapeValve), "Must be true");

        // current time(12:15 AM) = notified time(12:05AM) + 10 minutes. Should return true as it's within escape valve limit.
        assertTrue(helper.checkForEscapeValve(escapeValveTime, firstNotifiedTime.minusMinutes(10), intervalAfterEscapeValve),
                "Must be true");

        // current time(12:15 AM) = notified time(12:00AM) + 15 minutes. Should return true as it's within escape valve limit.
        assertTrue(helper.checkForEscapeValve(escapeValveTime, firstNotifiedTime.minusMinutes(15), intervalAfterEscapeValve),
                "Must be true");

        // current time(12:15 AM) = notified time(11:59PM) + 16 minutes. As time gap after escape valve limit is 1 and its not multiple of intervalAfterEscapeValve it should return false.
        assertFalse(helper.checkForEscapeValve(escapeValveTime, firstNotifiedTime.minusMinutes(16), intervalAfterEscapeValve),
                "Must be false");

        // current time(12:15 AM) = notified time(11:55PM) + 20 minutes. As time gap after escape valve limit is 5 and its multiple of intervalAfterEscapeValve it should return true.
        assertTrue(helper.checkForEscapeValve(escapeValveTime, firstNotifiedTime.minusMinutes(20), intervalAfterEscapeValve),
                "Must be true");

        // current time(12:15 AM) = notified time(11:54PM) + 21 minutes. As time gap after escape valve limit is 21 and its not multiple of intervalAfterEscapeValve it should return false.
        assertFalse(helper.checkForEscapeValve(escapeValveTime, firstNotifiedTime.minusMinutes(21), intervalAfterEscapeValve),
                "Must be false");

        // current time(12:15 AM) = notified time(11:20PM) + 55 minutes. As time gap after escape valve limit is 5 and its multiple of intervalAfterEscapeValve it should return true.
        assertTrue(helper.checkForEscapeValve(escapeValveTime, firstNotifiedTime.minusMinutes(55), intervalAfterEscapeValve),
                "Must be true");

    }
}
