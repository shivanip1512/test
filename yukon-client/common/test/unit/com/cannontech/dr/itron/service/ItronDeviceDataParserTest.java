package com.cannontech.dr.itron.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.easymock.EasyMock;
import org.joda.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.itron.ItronDataEventType;
import com.cannontech.dr.itron.service.impl.ItronDeviceDataParser;
import com.cannontech.dr.itron.service.impl.ItronLoadControlEventStatus;
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Multimap;

public class ItronDeviceDataParserTest {
    
    ItronDeviceDataParser parser;
    
    @BeforeEach
    public void init() {
        parser = new ItronDeviceDataParser();
    }
    
    @Test
    public void validateItronDataEventType() {
        Arrays.stream(ItronDataEventType.values())
            .collect(Collectors.groupingBy(ItronDataEventType::getEventIdHex))
            .entrySet().stream()
            .filter(e -> e.getValue().size() > 1)
            .map(e -> String.format("0x%08X", e.getKey()))
            .reduce((s1, s2) -> s1 + ", " + s2)
            .ifPresent(duplicates -> fail("duplicate hex value found: " + duplicates));
    }
    
    @Test
    public void validateRowParsingForRelayNumberFinding() {
        String[] rowData = rowData("type: 1, log event ID: 24 (0x18) - Relay Open, payload: Physical Relay (0) data(0100000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.RELAY_1_SHED_STATUS, rowData);
        assertOnlyEntryEquals(data, 1);
        
        String[] rowData2 = rowData("type: 1, log event ID: 24 (0x18) - Relay Open, payload: Physical Relay (1) data(0200000000)");
        Collection<PointData> data2 = parseRow(BuiltInAttribute.RELAY_2_SHED_STATUS, rowData2);
        assertOnlyEntryEquals(data2, 1);
        
        String[] rowData3 = rowData("type: 1, log event ID: 24 (0x18) - Relay Open, payload: Physical Relay (2) data(0400000000)");
        Collection<PointData> data3 = parseRow(BuiltInAttribute.RELAY_3_SHED_STATUS, rowData3);
        assertOnlyEntryEquals(data3, 1);
        
        String[] rowData4 = rowData("type: 1, log event ID: 24 (0x18) - Relay Open, payload: Physical Relay (3) data(0800000000)");
        Collection<PointData> data4 = parseRow(BuiltInAttribute.RELAY_4_SHED_STATUS, rowData4);
        assertOnlyEntryEquals(data4, 1);
    }
    
    @Test
    public void validateRowParsingForPayloadValuedEventWithMultiplier() {
        String[] rowData = rowData("type: 0, log event ID: 32925 (0x809D) - Vendor-specific or Unknown, payload:  data(0960000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.AVERAGE_VOLTAGE, rowData);
        assertEquals(1, data.size());
        PointData pointData = (PointData) data.toArray()[0];
        double value = pointData.getValue();
        assertEquals(240.0, value, .1);
    }

    @Test
    public void validateRowParsingForEventStarted() {
        String[] rowData = rowData("type: 0, log event ID: 14 (0x0e) - Event Started, payload: Event ID (130093) data(0001FC2D00)");
        Collection<PointData> data = parseRow(BuiltInAttribute.EVENT_STARTED, rowData);
        assertEquals(1, data.size());
        PointData pointData = (PointData) data.toArray()[0];
        double value = pointData.getValue();
        assertEquals(130093, value, .1);
    }

    @Test
    public void validateRowParsingForEventStopped() {
        String[] rowData = rowData("type: 0, log event ID: 15 (0x0F) - Event Stopped, payload: Event ID (130094) data(0001FC2E00)");
        Collection<PointData> data = parseRow(BuiltInAttribute.EVENT_STOPPED, rowData);
        assertEquals(1, data.size());
        PointData pointData = (PointData) data.toArray()[0];
        double value = pointData.getValue();
        assertEquals(130094, value, .1);
    }

    @Test
    public void validateRowParsingForEventCancelled() {
        String[] rowData = rowData("type: 1, log event ID: 16 (0x10) - Event Cancelled, payload: Event ID (645117902) data(2673B7CE00)");
        Collection<PointData> data = parseRow(BuiltInAttribute.EVENT_CANCELLED, rowData);
        assertEquals(1, data.size());
        PointData pointData = (PointData) data.toArray()[0];
        double value = pointData.getValue();
        assertEquals(645117902, value, .1);
    }

    @Test
    public void validateRowParsingForIncrementalEvent() {
        String[] rowData = rowData("type: 1, log event ID: 0 (0x00) - Power Loss, payload:  data(0000000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.BLINK_COUNT, rowData);
        assertOnlyEntryEquals(data, 1);
    }
    
    @Test
    public void validateRowParsingForTwoPartVoltageMin() {
        //0x960 = 2400 mV
        String[] rowData = rowData("type: 0, log event ID: 32924 (0x809C) - Vendor-specific or Unknown, payload:  data(0960000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.MINIMUM_VOLTAGE, rowData);
        assertEquals(0, data.size());
        
        String[] rowData2 = rowData("type: 0, log event ID: 32926 (0x809E) - Vendor-specific or Unknown, payload:  data(2712638700)");
        data = parseRow(BuiltInAttribute.MINIMUM_VOLTAGE, rowData2);
        assertOnlyEntryEquals(data, 240);
        assertOnlyEntryTimestamp(data, new Date(1602201351000L)); // This date is taken from TSSL-6175
    }

    @Test
    public void validateRowParsingForTwoPartVoltageMax() {
        //0x4B5 = 1205 mV
        String[] rowData = rowData("type: 0, log event ID: 32923 (0x809B) - Vendor-specific or Unknown, payload:  data(04B5000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.MAXIMUM_VOLTAGE, rowData);
        assertEquals(0, data.size());
        
        String[] rowData2 = rowData("type: 0, log event ID: 32927 (0x809F) - Vendor-specific or Unknown, payload:  data(2213E24400)");
        data = parseRow(BuiltInAttribute.MAXIMUM_VOLTAGE, rowData2);
        assertOnlyEntryEquals(data, 120.5);
    }
    
    @Test
    public void validateRowParsingEmptyPayload() {
        String[] rowData = rowData("type: 3, log event ID: 622133494, payload: ");
        try {
            Collection<PointData> data = parseRow(BuiltInAttribute.MAXIMUM_VOLTAGE, rowData); //attribute doesn't matter here
            assertEquals( data.size(), 0, "Incorrect data size for empty payload.");
        } catch (Exception e) {
            fail("Exception thrown parsing row with empty payload: " + e.getMessage());
        }
    }
    
    @Test
    public void validateRowParsingMissingPayload() {
        String[] rowData = rowData("duty cycle: 100, cycle period: 15, cycles number: 28, event control: 1, monitor event: 1, event state: 1");
        try {
            Collection<PointData> data = parseRow(BuiltInAttribute.MAXIMUM_VOLTAGE, rowData); //attribute doesn't matter here
            assertEquals(data.size(), 0, "Incorrect data size for missing payload.");
        } catch (Exception e) {
            fail("Exception thrown parsing row with empty payload: " + e.getMessage());
        }
    }
    
    @Test
    public void validateRowParsingForLoadControlEventReceived() {
        String[] rowData = loadControlRowData("2018-04-25T10:27:53.117-0700", "Load Control Event status update - Event: 344, HAN Device: 00:0c:c1:00:01:00:03:c1, ESI: 00:13:50:05:00:92:86:41, Status: Event Received. ");
        setupMocksForLoadControl(344, ItronLoadControlEventStatus.EVENT_RECEIVED, new Instant("2018-04-25T10:27:53.117-0700"));
        parser.generatePointData(rowData);
    }
    
    @Test
    public void validateRowParsingForLoadControlEventStarted() {
        String[] rowData = loadControlRowData("2018-04-25T10:27:53.117-0700", "Load Control Event status update - Event: 344, HAN Device: 00:0c:c1:00:01:00:03:c1, ESI: 00:13:50:05:00:92:86:41, Status: Event started. ");
        setupMocksForLoadControl(344, ItronLoadControlEventStatus.EVENT_STARTED, new Instant("2018-04-25T10:27:53.117-0700"));
        parser.generatePointData(rowData);
    }
    
    @Test
    public void validateRowParsingForLoadControlEventCancelled() {
        String[] rowData = loadControlRowData("2018-04-25T10:27:53.117-0700", "Load Control Event status update - Event: 344, HAN Device: 00:0c:c1:00:01:00:03:c1, ESI: 00:13:50:05:00:92:86:41, Status: Event cancelled. ");
        setupMocksForLoadControl(344, ItronLoadControlEventStatus.EVENT_CANCELLED, new Instant("2018-04-25T10:27:53.117-0700"));
        parser.generatePointData(rowData);
    }
    
    @Test
    public void validateRowParsingForLoadControlEventCompleted() {
        String[] rowData = loadControlRowData("2018-04-25T10:27:53.117-0700", "Load Control Event status update - Event: 344, HAN Device: 00:0c:c1:00:01:00:03:c1, ESI: 00:13:50:05:00:92:86:41, Status: Event completed. ");
        setupMocksForLoadControl(344, ItronLoadControlEventStatus.EVENT_COMPLETED, new Instant("2018-04-25T10:27:53.117-0700"));
        parser.generatePointData(rowData);
    }
    
    @Test
    // Events in this form should be ignored: 
    //   Error sending Load Control event: 341 to ESI: 00:13:50:05:00:92:86:37. Error: Event rejected
    //   Error sending Load Control event: 346 to ESI: 00:13:50:05:00:92:86:41. Error: ZB_NMGR_STATUS_FAILURE
    public void validateRowParsingForLoadControlIgnoredError() {
        String[] rowData = loadControlRowData("2018-04-25T10:27:53.117-0700", "Error sending Load Control event: 341 to ESI: 00:13:50:05:00:92:86:37. Error: Event rejected. ");
        setupMocksForLoadControlUnsupported();
        parser.generatePointData(rowData);
    }
    
    @Test
    // Events in this form should be ignored: 
    //   Load Control Event status update - Event: 341, HAN Device: 00:0c:c1:00:01:00:03:d1, ESI: 00:13:50:05:00:92:86:37, Status: Event rejected.
    //   Load Control Event status update - Event: 346, HAN Device: 00:0c:c1:00:01:00:03:c1, ESI: 00:13:50:05:00:92:86:41, Status: Event state Unknown.
    public void validateRowParsingForLoadControlIgnoredState() {
        String[] rowData = loadControlRowData("2018-04-25T10:27:53.117-0700", "Event: 341, HAN Device: 00:0c:c1:00:01:00:03:d1, ESI: 00:13:50:05:00:92:86:37, Status: Event rejected. ");
        setupMocksForLoadControlUnsupported();
        parser.generatePointData(rowData);
    }
    
    private String[] rowData(String text) {
        String[] rowData = new String[10];
        rowData[0] = "1";
        rowData[1] = "EVENT_CAT_NIC_EVENT";
        rowData[3] = "2018-04-25T10:27:53.117-0700";
        rowData[5] = "11:22:33:44:55:66:66:77";
        rowData[9] = text;
        return rowData;
    }
    
    /**
     * Build up a row of load control data as an array of Strings, similar to this:
     * [32707,EVENT_CAT_PROGRAM_EVENTS,INFORMATION,2020-01-08T14:23:50.000-0600,2020-01-08T14:23:55.715-0600,00:0c:c1:00:01:00:03:c8,,,,Load Control Event status update - Event: 367, HAN Device: 00:0c:c1:00:01:00:03:c8, ESI: 00:13:50:05:00:92:84:42, Status: Event cancelled. ]
     */
    private String[] loadControlRowData(String timestamp, String text) {
        String[] rowData = new String[10];
        rowData[0] = "1";                            // Data ID
        rowData[1] = "EVENT_CAT_PROGRAM_EVENTS";     // Event Category
        rowData[2] = "INFORMATION";                  // ("Information" or "Notice")
        rowData[3] = timestamp;                      // Event generation time
        rowData[4] = timestamp;                      // Event reported time
        rowData[5] = "11:22:33:44:55:66:66:77";      // ESI Mac Address
        //rowData 6, 7, 8 are always empty
        rowData[9] = text;
        return rowData;
    }
    
    private Collection<PointData> parseRow(BuiltInAttribute attribute, String[] rowData) {
        LiteYukonPAObject lpo = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        
        setupMocks(lpo, attribute);
        
        Multimap<PaoIdentifier, PointData> results = parser.generatePointData(rowData);
        return results.get(lpo.getPaoIdentifier());
    }
    
    private void assertOnlyEntryEquals(Collection<PointData> data, double expectedEntryValue) {
        assertEquals(1, data.size());
        PointData pointData = (PointData) data.toArray()[0];
        assertEquals(expectedEntryValue, pointData.getValue(), 0.1);
    }

    private void assertOnlyEntryTimestamp(Collection<PointData> data, Date expectedTimestamp) {
        assertEquals(1, data.size());
        PointData pointData = (PointData) data.toArray()[0];
        assertEquals(expectedTimestamp, pointData.getPointDataTimeStamp());
    }

    private void setupMocksForLoadControlUnsupported() {
        LiteYukonPAObject pao = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);
        
        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, pao));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);
    }
    
    private void setupMocksForLoadControl(Integer eventId, ItronLoadControlEventStatus eventStatus, Instant timestamp) {
        LiteYukonPAObject pao = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);
        
        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, pao));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);
        
        RecentEventParticipationService recentEventParticipationService = EasyMock.createStrictMock(RecentEventParticipationService.class);
        recentEventParticipationService.updateDeviceControlEvent(eventId, 1, eventStatus, timestamp);
        EasyMock.expectLastCall();
        EasyMock.replay(recentEventParticipationService);
        ReflectionTestUtils.setField(parser, "recentEventParticipationService", recentEventParticipationService);
    }
    
    private void setupMocks(LiteYukonPAObject lpo, BuiltInAttribute attribute) {
        LitePoint lp1 = new LitePoint(1, "pao1", 1, 1, 1, 15);

        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        AttributeService attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, attribute)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        AsyncDynamicDataSource dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        PointData pvqh = new PointData();
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao = EasyMock.createStrictMock(DynamicLcrCommunicationsDao.class);
        dynamicLcrCommunicationsDao.insertData(EasyMock.anyObject());
        EasyMock.expectLastCall();
        EasyMock.replay(dynamicLcrCommunicationsDao);
        ReflectionTestUtils.setField(parser, "dynamicLcrCommunicationsDao", dynamicLcrCommunicationsDao);
        
    }
}
