package com.cannontech.dr.service;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class RuntimeCalcSchedulerServiceTest {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    
    @Test
    public void test_getLimitedStartOfRange_withNullStartOfRange() {
        DateTime currentTime = formatter.parseDateTime("17/08/2021 13:00:00");
        int historyLimitDays = 30;
        Instant startOfRange = null;
        
        Instant result = RuntimeCalcSchedulerService.getLimitedStartOfRange(startOfRange, historyLimitDays, currentTime);
        
        Instant historyLimitInstant = formatter.parseDateTime("18/07/2021 00:00:00").toInstant();
        assertEquals(result, historyLimitInstant, "Limited value incorrect with null start of range.");
    }
    
    @Test
    public void test_getLimitedStartOfRange_withHistoryLimitZero() {
        DateTime currentTime = formatter.parseDateTime("17/08/2021 13:00:00");
        int historyLimitDays = 0;
        Instant startOfRange = formatter.parseDateTime("17/01/2021 1:00:00").toInstant();
        
        Instant result = RuntimeCalcSchedulerService.getLimitedStartOfRange(startOfRange, historyLimitDays, currentTime);
        
        assertEquals(result, startOfRange, "Start of range not used when history limit is 0.");
    }
    
    @Test
    public void test_getLimitedStartOfRange_withHistoryLimitNegative() {
        DateTime currentTime = formatter.parseDateTime("17/08/2021 13:00:00");
        int historyLimitDays = -1;
        Instant startOfRange = formatter.parseDateTime("17/01/2021 1:00:00").toInstant();
        
        Instant result = RuntimeCalcSchedulerService.getLimitedStartOfRange(startOfRange, historyLimitDays, currentTime);
        
        assertEquals(result, startOfRange, "Start of range not used when history limit is negative.");
    }
    
    @Test
    public void test_getLimitedStartOfRange_withStartOfRangeBeforeHistoryLimit() {
        DateTime currentTime = formatter.parseDateTime("17/08/2021 13:00:00");
        int historyLimitDays = 30;
        Instant startOfRange = formatter.parseDateTime("17/01/2021 1:00:00").toInstant();
        
        Instant result = RuntimeCalcSchedulerService.getLimitedStartOfRange(startOfRange, historyLimitDays, currentTime);
        
        Instant historyLimitInstant = formatter.parseDateTime("18/07/2021 00:00:00").toInstant();
        assertEquals(result, historyLimitInstant, "History limit not used when it's after start of range.");
    }
    
    @Test
    public void test_getLimitedStartOfRange_withStartOfRangeAfterHistoryLimit() {
        DateTime currentTime = formatter.parseDateTime("17/08/2021 13:00:00");
        int historyLimitDays = 30;
        Instant startOfRange = formatter.parseDateTime("15/08/2021 1:00:00").toInstant();
        
        Instant result = RuntimeCalcSchedulerService.getLimitedStartOfRange(startOfRange, historyLimitDays, currentTime);
        
        assertEquals(result, startOfRange, "History limit not used when it's after start of range.");
    }
}
