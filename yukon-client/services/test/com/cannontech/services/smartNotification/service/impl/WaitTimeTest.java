package com.cannontech.services.smartNotification.service.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;

public class WaitTimeTest {
    
    @Test
    public void test_waitTime() {
        WaitTime.setIntervals(new Intervals("", "0,1,3,5,15,30"));
        
        //Jan 1, 2017 00:00:00.000 UTC
        DateTime startTime = new DateTime(2017, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        //Jan 1, 2017 00:00:55.000 UTC
        DateTime nextTime = new DateTime(2017, 1, 1, 0, 0, 55, 0, DateTimeZone.UTC);
        //Jan 1, 2017 00:01:55.000 UTC
        DateTime nextTime2 = new DateTime(2017, 1, 1, 0, 1, 55, 0, DateTimeZone.UTC);
        
        WaitTime waitTime = WaitTime.getFirst(startTime.toInstant());
        Assert.assertEquals("Incorrect interval from getFirst", 0, waitTime.getInterval());
        DateTime runTime = waitTime.getRunTime().withZone(DateTimeZone.UTC);
        Assert.assertEquals("Incorrect run time from getFirst", startTime, runTime);
        
        waitTime = waitTime.getNext(nextTime.toInstant());
        Assert.assertEquals("Incorrect interval from getNext", 1, waitTime.getInterval());
        runTime = waitTime.getRunTime().withZone(DateTimeZone.UTC);
        Assert.assertEquals("Incorrect run time from getNext", nextTime.plus(Duration.standardMinutes(1)), runTime);
        
        waitTime = waitTime.getNext(nextTime2.toInstant());
        Assert.assertEquals("Incorrect interval from getNext", 3, waitTime.getInterval());
        runTime = waitTime.getRunTime().withZone(DateTimeZone.UTC);
        Assert.assertEquals("Incorrect run time from getNext", nextTime2.plus(Duration.standardMinutes(3)), runTime);
    }
}
