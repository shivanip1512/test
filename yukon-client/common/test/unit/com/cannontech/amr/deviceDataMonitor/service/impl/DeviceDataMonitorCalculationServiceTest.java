package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.google.common.collect.ImmutableSet;

public class DeviceDataMonitorCalculationServiceTest {
    private DeviceDataMonitorCalculationServiceImpl service;
    
    private static final Instant now = Instant.now();
    private static final String monitorName = "fakeMonitor";
    private static final int monitorId = 0;
    private static final Set<Integer> oldViolatingDeviceIds = ImmutableSet.of(1, 2, 3, 4);
    private static final Set<Integer> newViolatingDeviceIds = ImmutableSet.of(3, 4, 5, 6);
    
    @Before
    public void setup() {
        service = new DeviceDataMonitorCalculationServiceImpl();
    }
    
    @Test
    public void test_getEnteringViolationEvents() {
        
        Stream<SmartNotificationEvent> events = ReflectionTestUtils.invokeMethod(service, "getEnteringViolationEvents", 
                oldViolatingDeviceIds, newViolatingDeviceIds, monitorId, monitorName, now);
        List<SmartNotificationEvent> eventList = events.collect(Collectors.toList());
        
        // There should be 2 "entering violation" events: devices 5 and 6
        Assert.assertEquals("Incorrect number of 'entering violation' events.", 2, eventList.size());
        
        // Check that device 5 is present with appropriate parameters
        SmartNotificationEvent device4event = new SmartNotificationEvent(now);
        device4event.addParameters(DeviceDataMonitorEventAssembler.PAO_ID, 5);
        device4event.addParameters(DeviceDataMonitorEventAssembler.MONITOR_ID, monitorId);
        device4event.addParameters(DeviceDataMonitorEventAssembler.MONITOR_NAME, monitorName);
        device4event.addParameters(DeviceDataMonitorEventAssembler.STATE, DeviceDataMonitorEventAssembler.MonitorState.IN_VIOLATION);
        Assert.assertTrue("Event for device 5 is missing.", eventList.contains(device4event));
        
        // Check that device 6 is present with appropriate parameters
        SmartNotificationEvent device5event = new SmartNotificationEvent(now);
        device5event.addParameters(DeviceDataMonitorEventAssembler.PAO_ID, 6);
        device5event.addParameters(DeviceDataMonitorEventAssembler.MONITOR_ID, monitorId);
        device5event.addParameters(DeviceDataMonitorEventAssembler.MONITOR_NAME, monitorName);
        device5event.addParameters(DeviceDataMonitorEventAssembler.STATE, DeviceDataMonitorEventAssembler.MonitorState.IN_VIOLATION);
        Assert.assertTrue("Event for device 6 is missing.", eventList.contains(device5event));
        Assert.assertTrue("", eventList.contains(device5event));
    }
    
    @Test
    public void test_getExitingViolationEvents() {
        Stream<SmartNotificationEvent> events = ReflectionTestUtils.invokeMethod(service, "getExitingViolationEvents",
                oldViolatingDeviceIds, newViolatingDeviceIds, monitorId, monitorName, now);
        List<SmartNotificationEvent> eventList = events.collect(Collectors.toList());
        
        // There should be 2 "exiting violation" events: devices 1 and 2
        Assert.assertEquals("Incorrect number of 'exiting violation' events.", 2, eventList.size());
        
        // Check that device 1 is present with appropriate parameters
        SmartNotificationEvent device1event = new SmartNotificationEvent(now);
        device1event.addParameters(DeviceDataMonitorEventAssembler.PAO_ID, 1);
        device1event.addParameters(DeviceDataMonitorEventAssembler.MONITOR_ID, monitorId);
        device1event.addParameters(DeviceDataMonitorEventAssembler.MONITOR_NAME, monitorName);
        device1event.addParameters(DeviceDataMonitorEventAssembler.STATE, DeviceDataMonitorEventAssembler.MonitorState.OUT_OF_VIOLATION);
        Assert.assertTrue("Event for device 5 is missing.", eventList.contains(device1event));
        
        // Check that device 2 is present with appropriate parameters
        SmartNotificationEvent device2event = new SmartNotificationEvent(now);
        device2event.addParameters(DeviceDataMonitorEventAssembler.PAO_ID, 2);
        device2event.addParameters(DeviceDataMonitorEventAssembler.MONITOR_ID, monitorId);
        device2event.addParameters(DeviceDataMonitorEventAssembler.MONITOR_NAME, monitorName);
        device2event.addParameters(DeviceDataMonitorEventAssembler.STATE, DeviceDataMonitorEventAssembler.MonitorState.OUT_OF_VIOLATION);
        Assert.assertTrue("Event for device 2 is missing.", eventList.contains(device2event));
    }
}
