package com.cannontech.dr.honeywellWifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.dr.honeywellWifi.azure.event.ApplicationAccessAddedEvent;
import com.cannontech.dr.honeywellWifi.azure.event.ApplicationAccessRemovedEvent;
import com.cannontech.dr.honeywellWifi.azure.event.ConnectionStatus;
import com.cannontech.dr.honeywellWifi.azure.event.ConnectionStatusEvent;
import com.cannontech.dr.honeywellWifi.azure.event.DemandResponseEvent;
import com.cannontech.dr.honeywellWifi.azure.event.EquipmentStatus;
import com.cannontech.dr.honeywellWifi.azure.event.EquipmentStatusEvent;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;
import com.cannontech.dr.honeywellWifi.azure.event.Interval;
import com.cannontech.dr.honeywellWifi.azure.event.UiDataBasicEvent;
import com.cannontech.dr.honeywellWifi.azure.event.UnknownEvent;
import com.microsoft.azure.servicebus.Message;

/**
 * This is the unit test for status events that are read by HoneywellWifiDataListener
 */

public class HoneywellWifiDataListenerEventTest {
    private static HoneywellWifiDataListener honeywellWifiDataListenerEventTest;
    
    @BeforeEach
    public void init() {
        honeywellWifiDataListenerEventTest = new HoneywellWifiDataListener();   
    }

    @Test
    public void test_uiDataBasicEvent_BasicParsing() {
        final Instant created = Instant.now();
        final Double displayedTemp = 78.0;
        final Double heatSetpoint = 62.0;
        final Double coolSetpoint = 80.0;
        final TemperatureUnit displayedUnits = TemperatureUnit.FAHRENHEIT;
        final String statusHeat = "Hold";
        final Double heatLowerSetpointLimit = 40.0;
        final Double heatUpperSetpointLimit = 90.0;
        final Double coolLowerSetpointLimit = 50.0;
        final Double coolUpperSetpointLimit = 99.0;
        final Double scheduledHeatSetpoint = 62.0;
        final Double scheduledCoolSetpoint = 82.0;
        final Boolean switchEmergencyHeatAllowed = false;
        final String systemSwitchPosition = "Cool";
        final Double deadband = 0.0;
        final String displayedTempStatus = "Measured";
        final Integer deviceId = 35219;
        final String macId = "00D02D831835";
        Message message = new Message();
        
        String json = "{\r\n" + 
                "  \"identifier\": \"775b9f04-0d40-4d10-a4f4-bd5ab19ef99b\",\r\n" + 
                "  \"type\": \"UIDataBasicEvent\",\r\n" + 
                "  \"json\": \"{\\r\\n  \\\"created\\\": \\\"" + created.toString() + "\\\",\\r\\n  \\\"displayedTemp\\\": 78.0,\\r\\n  \\\"heatSetpoint\\\": 62.0,"
                        + "\\r\\n  \\\"coolSetpoint\\\": 80.0,\\r\\n  \\\"displayedUnits\\\": \\\"F\\\",\\r\\n  \\\"statusHeat\\\": \\\"Hold\\\","
                        + "\\r\\n  \\\"heatLowerSetpointLimit\\\": 40.0,\\r\\n  \\\"heatUpperSetpointLimit\\\": 90.0,\\r\\n  \\\"coolLowerSetpointLimit\\\": 50.0,"
                        + "\\r\\n  \\\"coolUpperSetpointLimit\\\": 99.0,\\r\\n  \\\"scheduledHeatSetpoint\\\": 62.0,\\r\\n  \\\"scheduledCoolSetpoint\\\": 82.0,"
                        + "\\r\\n  \\\"switchAutoAllowed\\\": false,\\r\\n  \\\"switchCoolAllowed\\\": true,\\r\\n  \\\"switchOffAllowed\\\": true,"
                        + "\\r\\n  \\\"switchHeatAllowed\\\": true,\\r\\n  \\\"switchEmergencyHeatAllowed\\\": false,\\r\\n  \\\"systemSwitchPosition\\\": \\\"Cool\\\","
                        + "\\r\\n  \\\"deadband\\\": 0.0,\\r\\n  \\\"displayedTempStatus\\\": \\\"Measured\\\",\\r\\n  \\\"deviceId\\\": 35219,\\r\\n  \\\"macId\\\": \\\"00D02D831835\\\"\\r\\n}\",\r\n" + 
                "  \"date\": \"2019-05-01T07:12:27\"\r\n" + 
                "}";
        UiDataBasicEvent actualEvent = (UiDataBasicEvent) ReflectionTestUtils.invokeMethod(honeywellWifiDataListenerEventTest, "getData", json, message);
        UiDataBasicEvent expectedEvent = new UiDataBasicEvent(created, displayedTemp, heatSetpoint, coolSetpoint, displayedUnits, statusHeat, heatLowerSetpointLimit, 
                                                              heatUpperSetpointLimit, coolLowerSetpointLimit, coolUpperSetpointLimit, scheduledHeatSetpoint, scheduledCoolSetpoint, 
                                                              switchEmergencyHeatAllowed, systemSwitchPosition, deadband, displayedTempStatus, deviceId, macId);
        
        assertEquals(expectedEvent.getCreatedDate(), actualEvent.getCreatedDate(), "Create Date Mismatch");
        assertEquals(expectedEvent.getDisplayedTemp(), actualEvent.getDisplayedTemp(), "Displayed Temperature Mismatch");
        assertEquals(expectedEvent.getHeatSetpoint(), actualEvent.getHeatSetpoint(), "Heat Set Point Mismatch");
        assertEquals(expectedEvent.getCoolSetpoint(), actualEvent.getCoolSetpoint(), "Cool Set Point Mismatch");
        assertEquals(expectedEvent.getDisplayedUnits(), actualEvent.getDisplayedUnits(), "Displayed Units Mismatch");
        assertEquals(expectedEvent.getStatusHeat(), actualEvent.getStatusHeat(), "Status Heat Mismatch");
        assertEquals(expectedEvent.getHeatLowerSetpointLimit(), actualEvent.getHeatLowerSetpointLimit(), "Heat Lower Set Point Limit Mismatch");
        assertEquals(expectedEvent.getHeatUpperSetpointLimit(), actualEvent.getHeatUpperSetpointLimit(), "Heat Upper Set Point Limit Mismatch");
        assertEquals(expectedEvent.getCoolLowerSetpointLimit(), actualEvent.getCoolLowerSetpointLimit(), "Cool Lower Set Point Limit Mismatch");
        assertEquals(expectedEvent.getCoolUpperSetpointLimit(), actualEvent.getCoolUpperSetpointLimit(), "Cool Upper Set Point Limit Mismatch");
        assertEquals(expectedEvent.getScheduledHeatSetpoint(), actualEvent.getScheduledHeatSetpoint(), "Scheduled Heat Set Point Mismatch");
        assertEquals(expectedEvent.getScheduledCoolSetpoint(), actualEvent.getScheduledCoolSetpoint(), "Scheduled Cool Set Point Mismatch");
        assertEquals(expectedEvent.getSwitchEmergencyHeatAllowed(), actualEvent.getSwitchEmergencyHeatAllowed(), "Switch Emergency Heat Allowed Mismatch");
        assertEquals(expectedEvent.getSystemSwitchPosition(), actualEvent.getSystemSwitchPosition(), "System Switch Position Mismatch");
        assertEquals(expectedEvent.getDeadband(), actualEvent.getDeadband(), "Deadband Mismatch");
        assertEquals(expectedEvent.getDisplayedTempStatus(), actualEvent.getDisplayedTempStatus(), "Displayed Temp Status Mismatch");
        assertEquals(expectedEvent.getDeviceId(), actualEvent.getDeviceId(), "Device Id Mismatch");
        assertEquals(expectedEvent.getMacId(), actualEvent.getMacId(), "Device Mac Id Mismatch");
    }
    
    @Test
    public void test_demandResponseEvent_BasicParsing() {
        final Integer demandResponseId = 1202;
        final EventPhase phase = EventPhase.NOT_STARTED;
        final Boolean optedOut = false;
        final Double heatSetpointLimit = null;
        final Double coolSetpointLimit = null;
        final Interval interval = new Interval(1, null, null, null, null, 0.0, new Duration(3569000));
        final List<Interval> intervals = new ArrayList<Interval>();
        intervals.add(0, interval);
        final Instant startTime = Instant.now();
        final Integer deviceId = 35219;
        final String macId = "00D02D831835";
        Message message = new Message();
        
        String json = "{\r\n" + 
                "  \"identifier\": \"46b965b7-f077-40cd-b37a-98d4beb18257\",\r\n" + 
                "  \"type\": \"DemandResponseEvent\",\r\n" + 
                "  \"json\": \"{\\r\\n  \\\"demandResponseId\\\": 1202,\\r\\n  \\\"phase\\\": \\\"NotStarted\\\",\\r\\n  \\\"optedOut\\\": false,"
                        + "\\r\\n  \\\"intervals\\\": [\\r\\n    {\\r\\n      \\\"sequenceNumber\\\": 1,\\r\\n      \\\"load\\\": 0.0,"
                        + "\\r\\n      \\\"durationSeconds\\\": 3569.0\\r\\n    }\\r\\n  ],\\r\\n  \\\"startTime\\\": \\\"" + startTime.toString() + "\\\","
                        + "\\r\\n  \\\"deviceId\\\": 35219,\\r\\n  \\\"macId\\\": \\\"00D02D831835\\\"\\r\\n}\",\r\n" + 
                "  \"date\": \"2017-07-10T16:00:09\"\r\n" + 
                "}";
        
        DemandResponseEvent actualEvent = (DemandResponseEvent) ReflectionTestUtils.invokeMethod(honeywellWifiDataListenerEventTest, "getData", json, message);
        DemandResponseEvent expectedEvent = new DemandResponseEvent(demandResponseId, phase, optedOut, heatSetpointLimit, coolSetpointLimit, intervals, startTime, deviceId, macId);
        
        assertEquals(expectedEvent.getDemandResponseId(), actualEvent.getDemandResponseId(), "Demand Response Id Mismatch");
        assertEquals(expectedEvent.getPhase(), actualEvent.getPhase(), "Phase Mismatch");
        assertEquals( expectedEvent.getOptedOut(), actualEvent.getOptedOut(), "Opted Out Mismatch");
        assertEquals(expectedEvent.getHeatSetpointLimit(), actualEvent.getHeatSetpointLimit(), "Heat Set Point Limit Mismatch");
        assertEquals(expectedEvent.getCoolSetpointLimit(), actualEvent.getCoolSetpointLimit(), "Cool Set Point Limit Mismatch");
        assertEquals(expectedEvent.getIntervals(), actualEvent.getIntervals(), "Intervals Mismatch");
        assertEquals(expectedEvent.getStartTime(), actualEvent.getStartTime(), "Start Time Mismatch");
        assertEquals(expectedEvent.getDeviceId(), actualEvent.getDeviceId(), "Device Id Mismatch");
        assertEquals(expectedEvent.getMacId(), actualEvent.getMacId(), "Device Mac Id Mismatch");
    }
    
    @Test
    public void test_equipmentStatusEvent_BasicParsing() {
        final EquipmentStatus equipmentStatus = EquipmentStatus.OFF;
        final EquipmentStatus previouseEquipmentStatus = EquipmentStatus.OFF;
        final String fanStatus = "On";
        final String previousFanStatus = "Off";
        final Integer deviceId = 35208;
        final String macId = "00D02D6317ED";
        Message message = new Message();
        
        String json ="{\r\n" + 
                "  \"identifier\": \"8fce9913-0fe6-41f2-b0f9-4d1284c74cec\",\r\n" + 
                "  \"type\": \"EquipmentStatusEvent\",\r\n" + 
                "  \"json\": \"{\\r\\n  \\\"equipmentStatus\\\": \\\"Off\\\",\\r\\n  \\\"fanStatus\\\": \\\"On\\\",\\r\\n  \\\"previousEquipmentStatus\\\": \\\"Off\\\","
                        + "\\r\\n  \\\"previousFanStatus\\\": \\\"Off\\\",\\r\\n  \\\"deviceId\\\": 35208,\\r\\n  \\\"macId\\\": \\\"00D02D6317ED\\\"\\r\\n}\",\r\n" + 
                "  \"date\": \"2017-07-10T22:59:59\"\r\n" + 
                "}";
        
        EquipmentStatusEvent actualEvent = (EquipmentStatusEvent) ReflectionTestUtils.invokeMethod(honeywellWifiDataListenerEventTest, "getData", json, message);
        EquipmentStatusEvent expectedEvent = new EquipmentStatusEvent(equipmentStatus, previouseEquipmentStatus, fanStatus, previousFanStatus, deviceId, macId);
        
        assertEquals(expectedEvent.getEquipmentStatus(), actualEvent.getEquipmentStatus(), "Equipment Status Mismatch");
        assertEquals(expectedEvent.getPreviousEquipmentStatus(), actualEvent.getPreviousEquipmentStatus(), "Previous Equipment Status Mismatch");
        assertEquals(expectedEvent.getFanStatus(), actualEvent.getFanStatus(), "Fan Status Mismatch");
        assertEquals(expectedEvent.getPreviousFanStatus(), actualEvent.getPreviousFanStatus(), "Previous Fan Status Mismatch");
        assertEquals(expectedEvent.getDeviceId(), actualEvent.getDeviceId(), "Device Id Mismatch");
    }
    
    @Test
    public void test_connectionStatusEvent_BasicParsing() {
        final ConnectionStatus connectionStatus = ConnectionStatus.CONNECTION_LOST;
        final Integer deviceId = 35208;
        final String macId = "00D02D6317ED";
        Message message = new Message();
        
        String json = "{\r\n" + 
                "  \"identifier\": \"fe68abb7-d994-41fb-b6d0-53e00e2bd941\",\r\n" + 
                "  \"type\": \"ConnectionStatusEvent\",\r\n" + 
                "  \"json\": \"{\\r\\n  \\\"connectionStatus\\\": \\\"ConnectionLost\\\",\\r\\n  \\\"deviceId\\\": 35208,\\r\\n  \\\"macId\\\": \\\"00D02D6317ED\\\"\\r\\n}\",\r\n" + 
                "  \"date\": \"2017-07-10T05:01:40.343809\"\r\n" + 
                "}";
        
        ConnectionStatusEvent actualEvent = (ConnectionStatusEvent) ReflectionTestUtils.invokeMethod(honeywellWifiDataListenerEventTest, "getData", json, message);
        ConnectionStatusEvent expectedEvent = new ConnectionStatusEvent(connectionStatus,deviceId, macId);
        
        assertEquals(expectedEvent.getConnectionStatus(), actualEvent.getConnectionStatus(), "Connection Status Mismatch");
        assertEquals(expectedEvent.getDeviceId(), actualEvent.getDeviceId(), "Device Id Mismatch");
        assertEquals(expectedEvent.getMacId(), actualEvent.getMacId(), "Device Mac Id Mismatch");
    }
    
    @Test
    public void test_applicationAccessAddedEvent_BasicParsing() {
        final int userId = 7697;
        final int locationId = 27594;
        final String macId = "00D02D786734";
        final int appId = 677;
        final String applicationName = "Clear Result APCo";
        final boolean isConfirmed = false;
        Message message = new Message();
        
        String json = "{\r\n" + 
                "  \"identifier\": \"d1f6170e-5add-48cf-ba50-bdc7fcd4a3c1\",\r\n" + 
                "  \"type\": \"ApplicationAccessAddedEvent\",\r\n" + 
                "  \"json\": \"{\\r\\n  \\\"userId\\\": 7697,\\r\\n  \\\"locationId\\\": 27594,\\r\\n  \\\"macId\\\": \\\"00D02D786734\\\",\\r\\n  \\\"appId\\\": 677,"
                        + "\\r\\n  \\\"applicationName\\\": \\\"Clear Result APCo\\\",\\r\\n  \\\"isConfirmed\\\": false\\r\\n}\",\r\n" + 
                "  \"date\": \"2019-05-01T07:02:42.7473678\"\r\n" + 
                "}";
        
        ApplicationAccessAddedEvent actualEvent = (ApplicationAccessAddedEvent) ReflectionTestUtils.invokeMethod(honeywellWifiDataListenerEventTest, "getData", json, message);
        ApplicationAccessAddedEvent expectedEvent = new ApplicationAccessAddedEvent(userId, locationId, macId, appId, applicationName, isConfirmed);
        
        assertEquals(expectedEvent.getUserId(), actualEvent.getUserId(), "User Id Mismatch");
        assertEquals(expectedEvent.getLocationId(), actualEvent.getLocationId(), "Location Id Mismatch");
        assertEquals(expectedEvent.getMacId(), actualEvent.getMacId(), "Device Mac Id Mismatch");
        assertEquals(expectedEvent.getAppId(), actualEvent.getAppId(), "App Id Mismatch");
        assertEquals(expectedEvent.getApplicationName(), actualEvent.getApplicationName(), "Application Name Mismatch");
        assertEquals(expectedEvent.getIsConfirmed(), actualEvent.getIsConfirmed(), "Confirmation Mismatch");
    }
    
    @Test
    public void test_applicationAccessRemovedEvent_BasicParsing() {
        final Integer userId = 17219;
        final Integer locationId = 27330;
        final String macId = "00D02D52330F";
        final Integer appId = 369;
        final String applicationName = "Eaton 2";
        Message message = new Message();
        
        String json = "{\r\n" + 
                "  \"identifier\": \"94d65432-b994-493e-985f-8357205914eb\",\r\n" + 
                "  \"type\": \"ApplicationAccessRemovedEvent\",\r\n" + 
                "  \"json\": \"{\\r\\n  \\\"userId\\\": 17219,\\r\\n  \\\"locationId\\\": 27330,\\r\\n  \\\"macId\\\": \\\"00D02D52330F\\\",\\r\\n  \\\"appId\\\": 369,"
                        + "\\r\\n  \\\"applicationName\\\": \\\"Eaton 2\\\"\\r\\n}\",\r\n" + 
                "  \"date\": \"2019-05-01T17:50:43.974247\"\r\n" + 
                "}";
      
      ApplicationAccessRemovedEvent actualEvent = (ApplicationAccessRemovedEvent) ReflectionTestUtils.invokeMethod(honeywellWifiDataListenerEventTest, "getData", json, message);
      ApplicationAccessRemovedEvent expectedEvent = new ApplicationAccessRemovedEvent(userId, locationId, macId, appId, applicationName);

      assertEquals(expectedEvent.getUserId(), actualEvent.getUserId(), "User Id Mismatch");
      assertEquals(expectedEvent.getLocationId(), actualEvent.getLocationId(), "Location Id Mismatch");
      assertEquals(expectedEvent.getMacId(), actualEvent.getMacId(), "Device Mac Id Mismatch");
      assertEquals(expectedEvent.getAppId(), actualEvent.getAppId(), "App Id Mismatch");
      assertEquals(expectedEvent.getApplicationName(), actualEvent.getApplicationName(), "Application Name Mismatch");
    }
     
    @Test
    public void test_unknownEvent_BasicParsing() {
        Message message = new Message();
        
        String json = "{\r\n" + 
                "  \"identifier\": \"fe68abb7-d994-41fb-b6d0-53e00e2bd941\",\r\n" + 
                "  \"type\": \"RandomEvent\",\r\n" + 
                "  \"json\": \"{\\r\\n  \\\"unknownStatus\\\": \\\"ConnectionLost\\\",\\r\\n  \\\"unknownDeviceId\\\": 35208,\\r\\n  \\\"unknownMacId\\\": \\\"00D02D6317ED\\\"\\r\\n}\",\r\n" + 
                "  \"date\": \"2017-07-10T05:01:40.343809\"\r\n" + 
                "}";

        HoneywellWifiData actualEvent = ReflectionTestUtils.invokeMethod(honeywellWifiDataListenerEventTest, "getData", json, message);

        assertTrue(actualEvent instanceof UnknownEvent, "Event Not Instance of UnknownEvent");
    }
}
