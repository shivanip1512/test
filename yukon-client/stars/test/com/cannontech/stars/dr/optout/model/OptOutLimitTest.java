package com.cannontech.stars.dr.optout.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class OptOutLimitTest {

    List<OptOutLimit> optOutLimits = null;

    @Before
    public void setup() {
        optOutLimits = new ArrayList<OptOutLimit>();

        OptOutLimit limit = new OptOutLimit();
        limit.setLimit(2);
        limit.setStartMonth(6);
        limit.setStopMonth(9);
        optOutLimits.add(limit);

        OptOutLimit limit2 = new OptOutLimit();
        limit2.setLimit(2);
        limit2.setStartMonth(11);
        limit2.setStopMonth(4);
        optOutLimits.add(limit2);
    }

    @Test
    public void testIsMonthUnderLimit() {
        int currentMonth = 5;
        OptOutLimit limit = getOptOutLimit(currentMonth);
        Assert.assertTrue("Incorrect limit", limit == null);

        currentMonth = 6;
        limit = getOptOutLimit(currentMonth);
        Assert.assertTrue("Incorrect limit", limit != null);

        currentMonth = 10;
        limit = getOptOutLimit(currentMonth);
        Assert.assertTrue("Incorrect limit", limit == null);

        currentMonth = 12;
        limit = getOptOutLimit(currentMonth);
        Assert.assertTrue("Incorrect limit", limit != null);

        currentMonth = 2;
        limit = getOptOutLimit(currentMonth);
        Assert.assertTrue("Incorrect limit", limit != null);
    }

    @Test
    public void testGetStartDate() {
        TimeZone userTimeZone = TimeZone.getDefault();
        int currentMonth = 6;
        OptOutLimit limit = getOptOutLimit(currentMonth);
        Assert.assertTrue("Incorrect limit", limit != null);

        Date startDate = limit.getOptOutLimitStartDate(currentMonth,
                                                       userTimeZone);
        Date expectedDate = new Date(new Date().getYear(), 5, 1);
        Assert.assertTrue("Incorrect startDate", startDate.equals(expectedDate));

        currentMonth = 12;
        limit = getOptOutLimit(currentMonth);
        Assert.assertTrue("Incorrect limit", limit != null);

        startDate = limit.getOptOutLimitStartDate(currentMonth, userTimeZone);
        expectedDate = new Date(new Date().getYear(), 10, 1);
        Assert.assertTrue("Incorrect startDate", startDate.equals(expectedDate));

        currentMonth = 2;
        limit = getOptOutLimit(currentMonth);
        Assert.assertTrue("Incorrect limit", limit != null);

        startDate = limit.getOptOutLimitStartDate(currentMonth, userTimeZone);
        expectedDate = new Date(new Date().getYear() - 1, 10, 1);
        Assert.assertTrue("Incorrect startDate", startDate.equals(expectedDate));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStartDateInvalid() {
        TimeZone userTimeZone = TimeZone.getDefault();
        int currentMonth = 5;
        Date startDate = null;
        OptOutLimit limit = optOutLimits.get(0);
        startDate = limit.getOptOutLimitStartDate(currentMonth, userTimeZone);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStartDateInvalid2() {
        TimeZone userTimeZone = TimeZone.getDefault();
        int currentMonth = 10;
        Date startDate = null;
        OptOutLimit limit = optOutLimits.get(1);
        startDate = limit.getOptOutLimitStartDate(currentMonth, userTimeZone);
    }

    private OptOutLimit getOptOutLimit(int currentMonth) {
        OptOutLimit result = null;
        for (OptOutLimit limit : optOutLimits) {
            if (limit.isMonthUnderLimit(currentMonth)) {
                result = limit;
                break;
            }
        }
        return result;
    }
}
