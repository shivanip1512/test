package com.cannontech.dr.itron.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Multimap;

public class ItronDeviceDataParserTest {
    
    ItronDeviceDataParser parser;
    
    @Before
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
    public void validateRowParsingForEventStarted() {
        String[] rowData = rowData("type: 1, log event ID: 14 (0x0e) - Event Started, payload: Event ID (90793) data(000162A900)");

        RecentEventParticipationService recentEventParticipationService = EasyMock.createMock(RecentEventParticipationService.class);
        recentEventParticipationService.updateDeviceControlEvent(EasyMock.anyInt(), EasyMock.anyInt(), EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall();
        EasyMock.replay(recentEventParticipationService);
        ReflectionTestUtils.setField(parser, "recentEventParticipationService", recentEventParticipationService);
        
        Collection<PointData> data = parseRow(BuiltInAttribute.CONTROL_STATUS, rowData);
        assertOnlyEntryEquals(data, 1);

    }
    
    @Test
    public void validateRowParsingForRelayNumberFinding() {
        String[] rowData = rowData("type: 1, log event ID: 24 (0x18) - Relay Open, payload: Physical Relay (3)  data(0300000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.RELAY_3_SHED_STATUS, rowData);
        assertOnlyEntryEquals(data, 1);
    }
    
    @Test
    public void validateRowParsingForPayloadValuedEvent() {
        String[] rowData = rowData("type: 0, log event ID: 32925 (0x809D) - Vendor-specific or Unknown, payload:  data(00F0000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.AVERAGE_VOLTAGE, rowData);
        Assert.assertEquals(1, data.size());
        PointData pointData = (PointData) data.toArray()[0];
        double value = pointData.getValue();
        Assert.assertEquals(240, value, .1);
    }
    
    @Test
    public void validateRowParsingForIncrementalEvent() {
        String[] rowData = rowData("type: 0, log event ID: 32768 (0x8000) - Vendor-specific or Unknown, payload:  data(0000000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.BLINK_COUNT, rowData);
        assertOnlyEntryEquals(data, 1);
    }
    
    @Test
    public void validateRowParsingForTwoPartVoltageMin() {
        String[] rowData = rowData("type: 0, log event ID: 32924 (0x809C) - Vendor-specific or Unknown, payload:  data(0002000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.MINIMUM_VOLTAGE, rowData);
        Assert.assertEquals(0, data.size());
        
        String[] rowData2 = rowData("type: 0, log event ID: 32926 (0x809E) - Vendor-specific or Unknown, payload:  data(2213E24400)");
        data = parseRow(BuiltInAttribute.MINIMUM_VOLTAGE, rowData2);
        assertOnlyEntryEquals(data, 2);
    }

    @Test
    public void validateRowParsingForTwoPartVoltageMax() {
        String[] rowData = rowData("type: 0, log event ID: 32923 (0x809B) - Vendor-specific or Unknown, payload:  data(0002000000)");
        Collection<PointData> data = parseRow(BuiltInAttribute.MAXIMUM_VOLTAGE, rowData);
        Assert.assertEquals(0, data.size());
        
        String[] rowData2 = rowData("type: 0, log event ID: 32927 (0x809F) - Vendor-specific or Unknown, payload:  data(2213E24400)");
        data = parseRow(BuiltInAttribute.MAXIMUM_VOLTAGE, rowData2);
        assertOnlyEntryEquals(data, 2);
    }
    
    @Test
    public void validateRowParsingEmptyPayload() {
        String[] rowData = rowData("type: 3, log event ID: 622133494, payload: ");
        try {
            Collection<PointData> data = parseRow(BuiltInAttribute.MAXIMUM_VOLTAGE, rowData); //attribute doesn't matter here
            Assert.assertEquals("Incorrect data size for empty payload.", data.size(), 0);
        } catch (Exception e) {
            Assert.fail("Exception thrown parsing row with empty payload: " + e.getMessage());
        }
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
    
    private void assertOnlyEntryEquals(Collection<PointData> data, int expectedEntryValue) {
        Assert.assertEquals(1, data.size());
        PointData pointData = (PointData) data.toArray()[0];
        Assert.assertEquals(expectedEntryValue, pointData.getValue(), 0.1);
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
