package com.cannontech.dr.service.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cannontech.dr.honeywellWifi.DatedRuntimeStatus;
import com.cannontech.dr.service.RuntimeCalcService;

public class RuntimeCalcServiceTest {
    
    private static final DateTimeFormatter dtFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZoneUTC(); 
    private static DateTime hour10;
    private static DateTime hour11;
    private static DateTime hour12;
    
    private static RuntimeCalcService runtimeCalcService;
    private static List<DatedRuntimeStatus> statuses;
    
    @BeforeClass
    public static void init() {
        hour10 = dtFormatter.parseDateTime("10/10/2016 10:00:00");
        hour11 = dtFormatter.parseDateTime("10/10/2016 11:00:00");
        hour12 = dtFormatter.parseDateTime("10/10/2016 12:00:00");
    }
    
    @Before
    public void initEach() {
        runtimeCalcService = new RuntimeCalcServiceImpl();
        statuses = new ArrayList<>();
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_noStatuses() {
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        assertThat(hourlyRuntimeSeconds.size(), equalTo(0));
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_oneStatus() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        assertThat(hourlyRuntimeSeconds.size(), equalTo(0));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_getHourlyRuntimeSeconds_outOfOrderStatusTimes() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:30:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        
        runtimeCalcService.getHourlyRuntimeSeconds(statuses);
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_offWithinHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:30:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // device is off during measured period, runtime of 0 should be recorded
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(0)); 
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(1));
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_runningWithinHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:10:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:30:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:10-10:30 = 20 minutes (1200 seconds)
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(1200));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(1));
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_offAcrossHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 11:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // device is off during measured periods, runtimes of 0 should be recorded
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(0)); 
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(0));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(2));
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_onAcrossHour() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 11:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:50-11:00 = 10 minutes (600 seconds)
        // 11:00-11:10 = 10 minutes (600 seconds)
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(600)); 
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(600));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(2));
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_offAcrossMultipleHours() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 12:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // device is off during measured periods, runtimes of 0 should be recorded
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(0)); 
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(0));
        assertThat("12:00 hour runtime", hourlyRuntimeSeconds.get(hour12), equalTo(0));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(3));
    }
    
    @Test
    public void test_getHourlyRuntimeSeconds_onAcrossMultipleHours() {
        
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.RUNNING, dtFormatter.parseDateTime("10/10/2016 10:50:00")));
        statuses.add(new DatedRuntimeStatus(RuntimeStatus.STOPPED, dtFormatter.parseDateTime("10/10/2016 12:10:00")));
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
        
        // 10:50-11:00 = 10 minutes (600 seconds)
        // 11:00-12:00 = 60 minutes (3600 seconds)
        // 12:00-12:10 = 10 minutes (600 seconds)
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(600));
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(3600));
        assertThat("12:00 hour runtime", hourlyRuntimeSeconds.get(hour12), equalTo(600));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(3));
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
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(1799));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(1));
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
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(600));
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(1200));
        assertThat("12:00 hour runtime", hourlyRuntimeSeconds.get(hour12), equalTo(600));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(3));
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
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(3600));
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(0));
        assertThat("12:00 hour runtime", hourlyRuntimeSeconds.get(hour12), equalTo(3600));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(3));
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
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(600));
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(600));
        assertThat("12:00 hour runtime", hourlyRuntimeSeconds.get(hour12), equalTo(0));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(3));
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
        assertThat("10:00 hour runtime", hourlyRuntimeSeconds.get(hour10), equalTo(1200));
        assertThat("11:00 hour runtime", hourlyRuntimeSeconds.get(hour11), equalTo(600));
        assertThat("12:00 hour runtime", hourlyRuntimeSeconds.get(hour12), equalTo(1200));
        assertThat("hourly runtimes size", hourlyRuntimeSeconds.size(), equalTo(3));
    }
    
}
