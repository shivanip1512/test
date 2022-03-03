package com.cannontech.common.smartNotification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class SmartNotificationSubscriptionServiceTest {

    private List<DeviceDataMonitor> deviceDataMonitorList = Lists.newArrayList();
    private DeviceDataMonitor deviceDataMonitor = null;

    @BeforeEach
    public void setUp() throws Exception {
        SmartNotificationSubscription smartNotificationSubscriptionIW = new SmartNotificationSubscription();
        smartNotificationSubscriptionIW.setType(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
        deviceDataMonitor = new DeviceDataMonitor();
        deviceDataMonitor.setName("DDM1");
        deviceDataMonitor.setId(32);
        deviceDataMonitorList.add(deviceDataMonitor);
    }

    @AfterEach
    public void tearDown() throws Exception {
        deviceDataMonitorList.clear();
    }

    @Test
    public final void test_getSubscription_Type_And_Name_For_DeviceDataMonitor_validMonitor() {
        SmartNotificationSubscription smartNotificationSubscriptionDDM = new SmartNotificationSubscription();
        smartNotificationSubscriptionDDM.setType(SmartNotificationEventType.DEVICE_DATA_MONITOR);
        smartNotificationSubscriptionDDM.setParameters(
            ImmutableMap.<String, Object> builder().put(DeviceDataMonitorEventAssembler.MONITOR_ID, 32).build());
        assertEquals(SmartNotificationSubscriptionService.getSubscriptionTypeAndName(smartNotificationSubscriptionDDM,
            deviceDataMonitorList), "DEVICE_DATA_MONITORDDM1");
    }

    @Test
    public final void test_getSubscription_Type_And_Name_For_InfrastructureWarning_validIW() {
        SmartNotificationSubscription smartNotificationSubscriptionIW = new SmartNotificationSubscription();
        smartNotificationSubscriptionIW.setType(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
        assertEquals(SmartNotificationSubscriptionService.getSubscriptionTypeAndName(smartNotificationSubscriptionIW,
            deviceDataMonitorList), "INFRASTRUCTURE_WARNING");
    }

    @Test
    public final void test_getSubscription_Type_And_Name_For_DeviceDataMonitor_inValidMonitor() {
        SmartNotificationSubscription smartNotificationSubscriptionDDM = new SmartNotificationSubscription();
        smartNotificationSubscriptionDDM.setType(SmartNotificationEventType.DEVICE_DATA_MONITOR);
        smartNotificationSubscriptionDDM.setParameters(
            ImmutableMap.<String, Object> builder().put(DeviceDataMonitorEventAssembler.MONITOR_ID, 45).build());
        assertNotEquals(SmartNotificationSubscriptionService.getSubscriptionTypeAndName(
            smartNotificationSubscriptionDDM, deviceDataMonitorList), "DEVICE_DATA_MONITORDDM1");
    }

    @Test
    public final void test_getSubscription_Type_And_Name_For_InfrastructureWarning_inValidIW() {
        SmartNotificationSubscription smartNotificationSubscriptionIW = new SmartNotificationSubscription();
        smartNotificationSubscriptionIW.setType(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
        assertNotEquals(SmartNotificationSubscriptionService.getSubscriptionTypeAndName(smartNotificationSubscriptionIW,
            deviceDataMonitorList), "INFRASTRUCTURE_WARNINGDUMMY");
    }

    @Test
    public final void test_getFrequencyDailyFrequency_valid() {
        SmartNotificationSubscription smartNotificationSubscriptionDailyFrequency = new SmartNotificationSubscription();
        smartNotificationSubscriptionDailyFrequency.setFrequency(SmartNotificationFrequency.DAILY_DIGEST);;
        smartNotificationSubscriptionDailyFrequency.setParameters(
            ImmutableMap.<String, Object> builder().put("sendTime", "02:00").build());
        assertEquals(SmartNotificationSubscriptionService.getFrequency(smartNotificationSubscriptionDailyFrequency), "DAILY_DIGESTC");
    }

    @Test
    public final void test_getFrequencyDailyFrequency_Invalid() {
        SmartNotificationSubscription smartNotificationSubscriptionDailyFrequency = new SmartNotificationSubscription();
        smartNotificationSubscriptionDailyFrequency.setFrequency(SmartNotificationFrequency.DAILY_DIGEST);;
        smartNotificationSubscriptionDailyFrequency.setParameters(
            ImmutableMap.<String, Object> builder().put("sendTime", "02:00").build());
        assertNotEquals(SmartNotificationSubscriptionService.getFrequency(smartNotificationSubscriptionDailyFrequency), "IMMEDIATE");
    }

    @Test
    public final void test_getFrequencyImmediate_valid() {
        SmartNotificationSubscription smartNotificationSubscriptionDailyFrequency = new SmartNotificationSubscription();
        smartNotificationSubscriptionDailyFrequency.setFrequency(SmartNotificationFrequency.IMMEDIATE);;
        assertEquals(SmartNotificationSubscriptionService.getFrequency(smartNotificationSubscriptionDailyFrequency), "IMMEDIATE");
    }

    @Test
    public final void test_getFrequencyImmediate_Invalid() {
        SmartNotificationSubscription smartNotificationSubscriptionDailyFrequency = new SmartNotificationSubscription();
        smartNotificationSubscriptionDailyFrequency.setFrequency(SmartNotificationFrequency.IMMEDIATE);;
        assertNotEquals(SmartNotificationSubscriptionService.getFrequency(smartNotificationSubscriptionDailyFrequency), "DAILY_DIGEST");
    }
}
