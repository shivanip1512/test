package com.cannontech.dr.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.dr.service.RelayLogInterval;
import com.cannontech.dr.service.RuntimeCalcService;

public class RuntimeCalcServiceTest {
    
    private static final DateTimeFormatter dtFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZoneUTC(); 
    private static DateTime hour11;
    private static DateTime hour12;
    private static DateTime hour13;
    
    private static RuntimeCalcService runtimeCalcService;
    private static List<DatedRuntimeStatus> statuses;
    
    @BeforeAll
    public static void init() {
        hour11 = dtFormatter.parseDateTime("10/10/2016 11:00:00");
        hour12 = dtFormatter.parseDateTime("10/10/2016 12:00:00");
        hour13 = dtFormatter.parseDateTime("10/10/2016 13:00:00");
    }
    
    @BeforeEach
    public void initEach() {
        runtimeCalcService = new RuntimeCalcServiceImpl();
        statuses = new ArrayList<>();
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_noStatuses() {
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        assertEquals(hourlyRuntimeSeconds.size(), 0);
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_oneStatus() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        assertEquals(hourlyRuntimeSeconds.size(), 0);
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_outOfOrderStatusTimes() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:30:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        });
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_offWithinHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:30:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // device is off during measured period, runtime of 0 should be recorded
        assertEquals(hourlyRuntimeSeconds.get(hour11), 0, "11:00 hour runtime"); 
        assertEquals(hourlyRuntimeSeconds.size(), 1, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_runningWithinHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:30:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:10-10:30 = 20 minutes (1200 seconds)
        assertEquals(hourlyRuntimeSeconds.get(hour11), 1200, "11:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 1, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_offAcrossHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // device is off during measured periods, runtimes of 0 should be recorded
        assertEquals(hourlyRuntimeSeconds.get(hour11), 0, "11:00 hour runtime"); 
        assertEquals(hourlyRuntimeSeconds.get(hour12), 0, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 2, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_onAcrossHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:50-11:00 = 10 minutes (600 seconds)
        // 11:00-11:10 = 10 minutes (600 seconds)
        assertEquals(hourlyRuntimeSeconds.get(hour11), 600, "11:00 hour runtime"); 
        assertEquals(hourlyRuntimeSeconds.get(hour12), 600, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 2, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_offAcrossMultipleHours() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 12:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // device is off during measured periods, runtimes of 0 should be recorded
        assertEquals(hourlyRuntimeSeconds.get(hour11), 0, "11:00 hour runtime"); 
        assertEquals(hourlyRuntimeSeconds.get(hour12), 0, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour13), 0, "13:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 3, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_onAcrossMultipleHours() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 12:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:50-11:00 = 10 minutes (600 seconds)
        // 11:00-12:00 = 60 minutes (3600 seconds)
        // 12:00-12:10 = 10 minutes (600 seconds)
        assertEquals(hourlyRuntimeSeconds.get(hour11), 600, "11:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour12), 3600, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour13), 600, "13:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 3, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_multiSegmentHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:00:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:20:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:30:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:59:59")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 11:00-11:10 = 10 minutes (600 seconds)
        // 11:20-11:30 = 10 minutes (600 seconds)
        // 11:50-11:59 = 9m 59s (599 seconds)
        //   hour 11 total: 29m 59s (1799 seconds)
        assertEquals(hourlyRuntimeSeconds.get(hour12), 1799, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 1, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_multiSegmentMultiHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:20:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:30:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 12:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 12:20:00")));

        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:50-11:00 = 10 minutes (600 seconds)
        // 11:00-11:10 = 10 minutes (600 seconds)
        // 11:20-11:30 = 10 minutes (600 seconds)
        //  (hour 11 total: 20 minutes (1200 seconds))
        // 12:10-12:20 = 10 minutes (600 seconds)
        assertEquals(hourlyRuntimeSeconds.get(hour11), 600, "11:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour12), 1200, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour13), 600, "13:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 3, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_startAndEndOnHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:00:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:00:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 12:00:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 13:00:00")));

        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:00-11:00 = 60 minutes (3600 seconds)
        // 11:00-12:00 = 0 minutes (0 seconds)
        // 12:00-13:00 = 60 minutes (3600 seconds)
        assertEquals(hourlyRuntimeSeconds.get(hour11), 3600, "11:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour12), 0, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour13), 3600, "13:00 hour runtime");
        assertEquals( hourlyRuntimeSeconds.size(), 3, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_consecutiveStopped() {
        
        // Within an hour
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:00:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:20:00")));
        
        // Across hours
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:40:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 12:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:00-10:10 = 10 minutes (600 seconds)
        // 11:40-11:50 = 10 minutes (600 seconds)
        // hour12 = 0 minutes (0 seconds)
        assertEquals(hourlyRuntimeSeconds.get(hour11), 600, "11:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour12), 600, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour13), 0, "13:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 3, "hourly runtimes size");
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_consecutiveRunning() {
        
        // Within an hour
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:00:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:20:00")));
        
        // Across hours
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 12:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 12:20:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:00-10:10 = 10 minutes (600 seconds)
        // 10:10-10:20 = 10 minutes (600 seconds)
        //  (hour 10 total = 20 minutes (1200 seconds)
        // 11:50-12:00 = 10 minutes (600 seconds)
        //  (hour 10 total = 10 minutes (600 seconds)
        // 12:00-12:10 = 10 minutes (600 seconds)
        // 12:10-12:20 = 10 minutes (600 seconds)
        //  (hour 12 total = 20 minutes (1200 seconds)
        assertEquals(hourlyRuntimeSeconds.get(hour11), 1200, "11:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour12), 600, "12:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.get(hour13), 1200, "13:00 hour runtime");
        assertEquals(hourlyRuntimeSeconds.size(), 3, "hourly runtimes size");
    }
    
    @Test
    public void test_getActiveSecondsWithinInterval() {
        
        var start = dtFormatter.parseDateTime("03/12/2019 09:30:00");
        var end = start.plus(RelayLogInterval.LOG_5_MINUTE.getDuration());

        var activeSeconds = ReflectionTestUtils.invokeMethod(runtimeCalcService, "getActiveSecondsWithinInterval", true, start, end);
        
        assertEquals(activeSeconds, 300, "Active seconds");

        var inactiveSeconds = ReflectionTestUtils.invokeMethod(runtimeCalcService, "getActiveSecondsWithinInterval", false, start, end);
        
        assertEquals(inactiveSeconds, 0, "Inactive seconds");
    }
    
    @Test
    public void test_getActiveSecondsWithinInterval_overDST() {
        
        var start = new DateTime(2019, 3, 10, 1, 30, 0, DateTimeZone.forID("America/Chicago"));
        var end = start.plus(RelayLogInterval.LOG_30_MINUTE.getDuration());

        var endExpected = new DateTime(2019, 3, 10, 3, 00, 0, DateTimeZone.forID("America/Chicago"));

        assertEquals(end, endExpected, "End date calculated across DST");
        
        var seconds = ReflectionTestUtils.invokeMethod(runtimeCalcService, "getActiveSecondsWithinInterval", true, start, end);
        
        assertEquals(seconds, 1800, "Active seconds calculated across DST");
    }
}
