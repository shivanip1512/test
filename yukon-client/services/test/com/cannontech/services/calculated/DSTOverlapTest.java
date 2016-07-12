package com.cannontech.services.calculated;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.user.YukonUserContext;

public class DSTOverlapTest {
    @Test
    public void beforeOverlapTime() {
        final TimeZone timeZone = YukonUserContext.system.getTimeZone();
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(2016, 10, 6, 0, 10, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(cal.getTime().toInstant(), timeZone.toZoneId());
        Assert.assertEquals(false, isInOverlap(zonedDateTime));
    }

    @Test
    public void inOverlapTime() {
        final TimeZone timeZone = YukonUserContext.system.getTimeZone();
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(2016, 10, 6, 1, 10, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(cal.getTime().toInstant(), timeZone.toZoneId());
        Assert.assertEquals(true, isInOverlap(zonedDateTime));
    }

    @Test
    public void afterOverlapTime() {
        final TimeZone timeZone = YukonUserContext.system.getTimeZone();
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(2016, 10, 6, 2, 10, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(cal.getTime().toInstant(), timeZone.toZoneId());
        Assert.assertEquals(false, isInOverlap(zonedDateTime));
    }

    public boolean isInOverlap(ZonedDateTime zonedDateTime) {
        ZonedDateTime withEarlierOffset = zonedDateTime.withEarlierOffsetAtOverlap();
        ZonedDateTime withLaterOffset = zonedDateTime.withLaterOffsetAtOverlap();
        return withEarlierOffset.getOffset() != withLaterOffset.getOffset(); // trick offset is always different in overlap hours
    }

}
