package com.cannontech.stars.dr.optout.model;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.google.common.collect.Lists;

public class OptOutLimitTest {

    private static final List<OptOutLimit> optOutLimits = Lists.newArrayList();

    @Before
    public void setup() {
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

    private OptOutLimit getOptOutLimit(int currentMonth) {
        OptOutLimit result = null;
        for (OptOutLimit limit : optOutLimits) {
            if (limit.isReleventMonth(currentMonth)) {
                result = limit;
                break;
            }
        }
        return result;
    }
}
