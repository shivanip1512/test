package com.cannontech.common.dr.setup;

import org.junit.Assert;
import org.junit.Test;

public class HolidayUsageTest {

    @Test
    public void test_getForHoliday_valid() {
        HolidayUsage holidayUsage = HolidayUsage.getForHoliday('E');
        Assert.assertTrue("Holiday Usage mismatch", holidayUsage == HolidayUsage.EXCLUDE);
        holidayUsage = HolidayUsage.getForHoliday('F');
        Assert.assertTrue("Holiday Usage mismatch", holidayUsage == HolidayUsage.FORCE);
        holidayUsage = HolidayUsage.getForHoliday('N');
        Assert.assertTrue("Holiday Usage mismatch", holidayUsage == HolidayUsage.NONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getForHoliday_invalid() {
        HolidayUsage.getForHoliday('A');
    }
}
