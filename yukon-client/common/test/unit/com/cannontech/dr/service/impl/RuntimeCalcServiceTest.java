package com.cannontech.dr.service.impl;

import static org.junit.jupiter.api.Assertions.*;

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
    private static DateTime hour1115;
    private static DateTime hour1130;
    private static DateTime hour1135;
    private static DateTime hour1140;
    private static DateTime hour1145;
    private static DateTime hour1150;
    private static DateTime hour1155;
    private static DateTime hour12;
    private static DateTime hour13;
    
    private static RuntimeCalcService runtimeCalcService;
    private static List<DatedRuntimeStatus> statuses;
    
    @BeforeAll
    public static void init() {
        hour11 = dtFormatter.parseDateTime("10/10/2016 11:00:00");
        hour1115 = dtFormatter.parseDateTime("10/10/2016 11:15:00");
        hour1130 = dtFormatter.parseDateTime("10/10/2016 11:30:00");
        hour1135 = dtFormatter.parseDateTime("10/10/2016 11:35:00");
        hour1140 = dtFormatter.parseDateTime("10/10/2016 11:40:00");
        hour1145 = dtFormatter.parseDateTime("10/10/2016 11:45:00");
        hour1150 = dtFormatter.parseDateTime("10/10/2016 11:50:00");
        hour1155 = dtFormatter.parseDateTime("10/10/2016 11:55:00");
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


    @Test
    public void test_get30mRuntimeSeconds_noStatuses() {

        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.get30MinuteRuntimeSeconds(statuses);

        assertEquals(hourlyRuntimeSeconds.size(), 0);
    }

    @Test
    public void test_get30mRuntimeSeconds_oneStatus() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:10:00")));

        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.get30MinuteRuntimeSeconds(statuses);

        assertEquals(hourlyRuntimeSeconds.size(), 0);
    }

    @Test
    public void test_get30mRuntimeSeconds_outOfOrderStatusTimes() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:30:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:10:00")));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            runtimeCalcService.get30MinuteRuntimeSeconds(statuses);
        });
    }

    @Test
    public void test_get30mRuntimeSeconds_offWithinHour() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:35:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:55:00")));

        Map<DateTime, Integer> thirtyMinRuntimeSeconds = runtimeCalcService.get30MinuteRuntimeSeconds(statuses);

        // device is off during measured period, runtime of 0 should be recorded
        assertEquals(thirtyMinRuntimeSeconds.get(hour11), 0, "10:30-11:00 halfhour runtime");
        assertEquals(thirtyMinRuntimeSeconds.size(), 1, "30m runtimes size");
    }

    @Test
    public void test_get30mRuntimeSeconds_runningWithinHour() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:35:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:55:00")));

        Map<DateTime, Integer> thirtyMinRuntimeSeconds = runtimeCalcService.get30MinuteRuntimeSeconds(statuses);

        // 10:35-10:55 = 20 minutes (1200 seconds)
        assertEquals(thirtyMinRuntimeSeconds.get(hour11), 1200, "10:30-11:00 halfhour runtime");
        assertEquals(thirtyMinRuntimeSeconds.size(), 1, "30m runtimes size");
    }

    @Test
    public void test_get30mRuntimeSeconds_offAcrossHour() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:10:00")));

        Map<DateTime, Integer> thirtyMinRuntimeSeconds = runtimeCalcService.get30MinuteRuntimeSeconds(statuses);

        // device is off during measured periods, runtimes of 0 should be recorded
        assertEquals(thirtyMinRuntimeSeconds.get(hour11), 0, "11:00 hour runtime");
        assertEquals(thirtyMinRuntimeSeconds.get(hour1130), 0, "11:30 hour runtime");
        assertEquals(thirtyMinRuntimeSeconds.size(), 2, "30m runtimes size");
    }

    @Test
    public void test_get30mRuntimeSeconds_onAcrossHour() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:10:00")));

        Map<DateTime, Integer> thirtyMinRuntimeSeconds = runtimeCalcService.get30MinuteRuntimeSeconds(statuses);

        // 10:50-11:00 = 10 minutes (600 seconds)
        // 11:00-11:10 = 10 minutes (600 seconds)
        assertEquals(thirtyMinRuntimeSeconds.get(hour11), 600, "11:00 hour runtime");
        assertEquals(thirtyMinRuntimeSeconds.get(hour1130), 600, "11:30 hour runtime");
        assertEquals(thirtyMinRuntimeSeconds.size(), 2, "30m runtimes size");
    }

    @Test
    public void test_get30mRuntimeSeconds_onAcrossMultiHour() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 09:57:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:57:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:57:00")));

        Map<DateTime, Integer> thirtyMinRuntimeSeconds = runtimeCalcService.get30MinuteRuntimeSeconds(statuses);

        // 10:30-11:00 = 30 minutes (1800 seconds)
        // 11:00-11:30 = 30 minutes (1800 seconds)
        // 11:30-12:00 = 30 minutes (1800 seconds)
        assertEquals(thirtyMinRuntimeSeconds.get(hour11), 1800, "11:00 hour runtime");
        assertEquals(thirtyMinRuntimeSeconds.get(hour1130), 1800, "11:30 hour runtime");
        assertEquals(thirtyMinRuntimeSeconds.get(hour12), 1620, "12:00 hour runtime");
        assertEquals(thirtyMinRuntimeSeconds.size(), 5, "30m runtimes size");
    }

    @Test
    public void test_get15mRuntimeSeconds_onAcrossMultiHour() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 09:57:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:57:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:57:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 12:00:00")));

        Map<DateTime, Integer> fifteenMinRuntimeSeconds = runtimeCalcService.get15MinuteRuntimeSeconds(statuses);

        // 10:45-11:00 = 15 minutes (900 seconds)
        // 11:00-11:15 = 15 minutes (900 seconds)
        // 11:15-11:30 = 15 minutes (900 seconds)
        // 11:30-11:45 = 15 minutes (900 seconds)
        // 11:45-12:00 = 15 minutes (900 seconds)
        assertEquals(fifteenMinRuntimeSeconds.get(hour11), 900, "11:00 hour runtime");
        assertEquals(fifteenMinRuntimeSeconds.get(hour1115), 900, "11:15 hour runtime");
        assertEquals(fifteenMinRuntimeSeconds.get(hour1130), 900, "11:30 hour runtime");
        assertEquals(fifteenMinRuntimeSeconds.get(hour1145), 900, "11:45 hour runtime");
        assertEquals(fifteenMinRuntimeSeconds.get(hour12), 900, "12:00 hour runtime");
        assertEquals(fifteenMinRuntimeSeconds.size(), 9, "30m runtimes size");
    }

    @Test
    public void test_get5mRuntimeSeconds_onAcrossMultiHour() {

        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 09:57:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:57:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:57:00")));

        Map<DateTime, Integer> fiveMinRuntimeSeconds = runtimeCalcService.get5MinuteRuntimeSeconds(statuses);

        // 11:25-11:30 = 5 minutes (300 seconds)
        // 11:30-11:35 = 5 minutes (300 seconds)
        // 11:35-11:40 = 5 minutes (300 seconds)
        // 11:40-11:45 = 5 minutes (300 seconds)
        // 11:45-11:50 = 5 minutes (300 seconds)
        // 11:50-11:55 = 5 minutes (300 seconds)
        // 11:55-12:00 = 5 minutes (300 seconds)
        assertEquals(fiveMinRuntimeSeconds.get(hour1130), 300, "11:30 hour runtime");
        assertEquals(fiveMinRuntimeSeconds.get(hour1135), 300, "11:35 hour runtime");
        assertEquals(fiveMinRuntimeSeconds.get(hour1140), 300, "11:40 hour runtime");
        assertEquals(fiveMinRuntimeSeconds.get(hour1145), 300, "11:45 hour runtime");
        assertEquals(fiveMinRuntimeSeconds.get(hour1150), 300, "11:50 hour runtime");
        assertEquals(fiveMinRuntimeSeconds.get(hour1155), 300, "11:55 hour runtime");
        assertEquals(fiveMinRuntimeSeconds.get(hour12), 120, "12:00 hour runtime");
        assertEquals(fiveMinRuntimeSeconds.size(), 25, "30m runtimes size");
    }

}
