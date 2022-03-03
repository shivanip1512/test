package com.cannontech.common.dr.setup;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HolidayUsageTest {

    @Test
    public void test_getForHoliday_valid() {
        HolidayUsage holidayUsage = HolidayUsage.getForHoliday('E');
        assertTrue(holidayUsage == HolidayUsage.EXCLUDE, "Holiday Usage mismatch");
        holidayUsage = HolidayUsage.getForHoliday('F');
        assertTrue(holidayUsage == HolidayUsage.FORCE, "Holiday Usage mismatch");
        holidayUsage = HolidayUsage.getForHoliday('N');
        assertTrue(holidayUsage == HolidayUsage.NONE, "Holiday Usage mismatch");
    }

    @Test
    public void test_getForHoliday_invalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            HolidayUsage.getForHoliday('A');
        });
    }
}
