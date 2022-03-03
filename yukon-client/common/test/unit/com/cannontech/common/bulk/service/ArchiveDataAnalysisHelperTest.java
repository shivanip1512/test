package com.cannontech.common.bulk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

import com.cannontech.core.service.SystemDateFormattingService;
import com.google.common.collect.Lists;

public class ArchiveDataAnalysisHelperTest {
    
    @Test
    public void testGetListOfRelevantDateTimes() {
        ArchiveDataAnalysisHelper helper = getHelper();
        
        DateTimeZone central = DateTimeZone.forID("America/Chicago");
        DateTime start = new DateTime(2011, 11, 4, 0, 0, 0, 0, central); // selected to be right before DST ends
        DateTime end = new DateTime(2011, 11, 7, 0, 0, 0, 0, central);
        Interval interval = new Interval(start, end);
        List<Instant> relevantDateTimes = helper.getListOfRelevantDateTimes(interval, Period.days(1));
        
        List<Instant> expected = Lists.newArrayList(
            new DateTime(2011, 11, 5, 0, 0, 0, 0, central).toInstant(),
            new DateTime(2011, 11, 6, 0, 0, 0, 0, central).toInstant(),
            new DateTime(2011, 11, 7, 0, 0, 0, 0, central).toInstant());  // this is actually 25 hours after the previous
        
        assertEquals(expected, relevantDateTimes);
        
        DateTime start2 = new DateTime(2011, 3, 11, 0, 0, 0, 0, central); // selected to be right before DST ends
        DateTime end2 = new DateTime(2011, 3, 14, 0, 0, 0, 0, central);
        Interval interval2 = new Interval(start2, end2);
        
        List<Instant> relevantDateTimes2 = helper.getListOfRelevantDateTimes(interval2, Period.days(1));
        
        List<Instant> expected2 = Lists.newArrayList(
            new DateTime(2011, 3, 12, 0, 0, 0, 0, central).toInstant(),
            new DateTime(2011, 3, 13, 0, 0, 0, 0, central).toInstant(),
            new DateTime(2011, 3, 14, 0, 0, 0, 0, central).toInstant());  // this is actually 23 hours after the previous
        
        assertEquals(expected2, relevantDateTimes2);
    }
    
    private ArchiveDataAnalysisHelper getHelper() {
        ArchiveDataAnalysisHelper helper = new ArchiveDataAnalysisHelper();
        SystemDateFormattingService formattingService = getFormattingServiceStub();
        helper.setSystemDateFormattingService(formattingService);
        
        return helper;
    }
    
    private SystemDateFormattingService getFormattingServiceStub() {
        return new SystemDateFormattingService() {
            public TimeZone getSystemTimeZone() {
                return TimeZone.getTimeZone("America/Chicago");
            }
            
            public DateFormat getSystemDateFormat(DateFormatEnum dateFormat) {
                return null;
            }
            
            public DateTimeFormatter getCommandTimeFormatter() {
                return null;
            }
            
            public Calendar getSystemCalendar() {
                return null;
            }
        };
    }
}
