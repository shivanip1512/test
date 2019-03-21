package com.cannontech.dr.itron.service;

import static org.junit.Assert.fail;

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
import com.cannontech.dr.itron.ItronDataEventType;
import com.cannontech.dr.itron.service.impl.ItronDeviceDataParser;
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
        String[] rowData = new String[10];
        rowData[0] = "1";
        rowData[1] = "EVENT_CAT_NIC_EVENT";
        rowData[3] = "2018-04-25T10:27:53.117-0700";
        rowData[5] = "11:22:33:44:55:66:66:77";
        rowData[9] = "type: 0, log event ID: 32782 (0x800E) - Vendor-specific or Unknown, payload:  data(0000000000)";
        
        LitePoint lp1 = new LitePoint(1, "pao1", 1, 1, 1, 15);
        LiteYukonPAObject lpo = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        AttributeService attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, BuiltInAttribute.CONTROL_STATUS)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        AsyncDynamicDataSource dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        PointData pvqh = new PointData();
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        Multimap<PaoIdentifier, PointData> results = parser.generatePointData(rowData);
        Collection<PointData> data = results.get(lpo.getPaoIdentifier());
        Assert.assertEquals(1, data.size());
        PointData pointData = (PointData) results.get(lpo.getPaoIdentifier()).toArray()[0];
        Assert.assertEquals(1, pointData.getValue(), 0.1);

    }
    
    @Test
    public void validateRowParsingForRelayNumberFinding() {
        String[] rowData = new String[10];
        rowData[0] = "1";
        rowData[1] = "EVENT_CAT_NIC_EVENT";
        rowData[3] = "2018-04-25T10:27:53.117-0700";
        rowData[5] = "11:22:33:44:55:66:66:77";
        rowData[9] = "type: 0, log event ID: 32792 (0x8018) - Vendor-specific or Unknown, payload:  data(0300000000)";
        
        LitePoint lp1 = new LitePoint(1, "pao1", 1, 1, 1, 15);
        LiteYukonPAObject lpo = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        AttributeService attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, BuiltInAttribute.RELAY_3_SHED_STATUS)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        AsyncDynamicDataSource dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        PointData pvqh = new PointData();
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        Multimap<PaoIdentifier, PointData> results = parser.generatePointData(rowData);
        Collection<PointData> data = results.get(lpo.getPaoIdentifier());
        Assert.assertEquals(1, data.size());
        PointData pointData = (PointData) results.get(lpo.getPaoIdentifier()).toArray()[0];
        Assert.assertEquals(1, pointData.getValue(), 0.1);

    }
    
    @Test
    public void validateRowParsingForPayloadValuedEvent() {
        String[] rowData = new String[10];
        rowData[0] = "1";
        rowData[1] = "EVENT_CAT_NIC_EVENT";
        rowData[3] = "2018-04-25T10:27:53.117-0700";
        rowData[5] = "11:22:33:44:55:66:66:77";
        rowData[9] = "type: 0, log event ID: 32898 (0x8082) - Vendor-specific or Unknown, payload:  data(2213E24400)";
        
        LitePoint lp1 = new LitePoint(1, "pao1", 1, 1, 1, 15);
        LiteYukonPAObject lpo = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        AttributeService attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, BuiltInAttribute.TIME_SYNC)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        AsyncDynamicDataSource dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        PointData pvqh = new PointData();
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        Multimap<PaoIdentifier, PointData> results = parser.generatePointData(rowData);
        Collection<PointData> data = results.get(lpo.getPaoIdentifier());
        Assert.assertEquals(1, data.size());
        PointData pointData = (PointData) results.get(lpo.getPaoIdentifier()).toArray()[0];
        double value = pointData.getValue();
        Assert.assertEquals(571728452, value, .1);
    }
    
    @Test
    public void validateRowParsingForIncrementalEvent() {
        String[] rowData = new String[10];
        rowData[0] = "1";
        rowData[1] = "EVENT_CAT_NIC_EVENT";
        rowData[3] = "2018-04-25T10:27:53.117-0700";
        rowData[5] = "11:22:33:44:55:66:66:77";
        rowData[9] = "type: 0, log event ID: 32768 (0x8000) - Vendor-specific or Unknown, payload:  data(0000000000)";
        
        LitePoint lp1 = new LitePoint(1, "pao1", 1, 1, 1, 15);
        LiteYukonPAObject lpo = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        AttributeService attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, BuiltInAttribute.BLINK_COUNT)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        AsyncDynamicDataSource dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        PointData pvqh = new PointData();
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        Multimap<PaoIdentifier, PointData> results = parser.generatePointData(rowData);
        Collection<PointData> data = results.get(lpo.getPaoIdentifier());
        Assert.assertEquals(1, data.size());
        PointData pointData = (PointData) results.get(lpo.getPaoIdentifier()).toArray()[0];
        Assert.assertEquals(1, pointData.getValue(), 0.1);

    }
    
    @Test
    public void validateRowParsingForTwoPartVoltageMin() {
        String[] rowData = new String[10];
        rowData[0] = "1";
        rowData[1] = "EVENT_CAT_NIC_EVENT";
        rowData[3] = "2018-04-25T10:27:53.117-0700";
        rowData[5] = "11:22:33:44:55:66:66:77";
        rowData[9] = "type: 0, log event ID: 32924 (0x809C) - Vendor-specific or Unknown, payload:  data(0002000000)";
        
        LitePoint lp1 = new LitePoint(1, "pao1", 1, 1, 1, 15);
        LiteYukonPAObject lpo = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        AttributeService attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, BuiltInAttribute.MINIMUM_VOLTAGE)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        AsyncDynamicDataSource dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        PointData pvqh = new PointData();
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        Multimap<PaoIdentifier, PointData> results = parser.generatePointData(rowData);
        Collection<PointData> data = results.get(lpo.getPaoIdentifier());
        Assert.assertEquals(0, data.size());
        
        String[] rowData2 = new String[10];
        rowData2[0] = "1";
        rowData2[1] = "EVENT_CAT_NIC_EVENT";
        rowData2[3] = "2018-04-25T10:27:53.117-0700";
        rowData2[5] = "11:22:33:44:55:66:66:77";
        rowData2[9] = "type: 0, log event ID: 32926 (0x809E) - Vendor-specific or Unknown, payload:  data(2213E24400)";
        
        deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, BuiltInAttribute.MINIMUM_VOLTAGE)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        results = parser.generatePointData(rowData2);
        data = results.get(lpo.getPaoIdentifier());
        Assert.assertEquals(1, data.size());
        PointData pointData = (PointData) results.get(lpo.getPaoIdentifier()).toArray()[0];
        Assert.assertEquals(2, pointData.getValue(), 0.1);
    }

    @Test
    public void validateRowParsingForTwoPartVoltageMax() {
        String[] rowData = new String[10];
        rowData[0] = "1";
        rowData[1] = "EVENT_CAT_NIC_EVENT";
        rowData[3] = "2018-04-25T10:27:53.117-0700";
        rowData[5] = "11:22:33:44:55:66:66:77";
        rowData[9] = "type: 0, log event ID: 32923 (0x809B) - Vendor-specific or Unknown, payload:  data(0002000000)";
        
        LitePoint lp1 = new LitePoint(1, "pao1", 1, 1, 1, 15);
        LiteYukonPAObject lpo = new LiteYukonPAObject(1, "pao1", 
                                                      PaoCategory.DEVICE, 
                                                      PaoClass.ITRON, 
                                                      PaoType.LCR6600S, 
                                                      "", "");
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        IDatabaseCache serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        AttributeService attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, BuiltInAttribute.MAXIMUM_VOLTAGE)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        AsyncDynamicDataSource dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        PointData pvqh = new PointData();
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        Multimap<PaoIdentifier, PointData> results = parser.generatePointData(rowData);
        Collection<PointData> data = results.get(lpo.getPaoIdentifier());
        Assert.assertEquals(0, data.size());
        
        String[] rowData2 = new String[10];
        rowData2[0] = "1";
        rowData2[1] = "EVENT_CAT_NIC_EVENT";
        rowData2[3] = "2018-04-25T10:27:53.117-0700";
        rowData2[5] = "11:22:33:44:55:66:66:77";
        rowData2[9] = "type: 0, log event ID: 32927 (0x809F) - Vendor-specific or Unknown, payload:  data(2213E24400)";
        
        deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.expect(deviceDao.getDeviceIdFromMacAddress("11:22:33:44:55:66:66:77")).andReturn(1);
        EasyMock.replay(deviceDao);
        ReflectionTestUtils.setField(parser, "deviceDao", deviceDao);

        serverDatabaseCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(serverDatabaseCache.getAllPaosMap()).andReturn(Map.of(1, lpo));
        EasyMock.replay(serverDatabaseCache);
        ReflectionTestUtils.setField(parser, "serverDatabaseCache", serverDatabaseCache);

        attributeService = EasyMock.createStrictMock(AttributeService.class);
        EasyMock.expect(attributeService.createAndFindPointForAttribute(lpo, BuiltInAttribute.MAXIMUM_VOLTAGE)).andReturn(lp1);
        EasyMock.replay(attributeService);
        ReflectionTestUtils.setField(parser, "attributeService", attributeService);
        
        dataSource = EasyMock.createStrictMock(AsyncDynamicDataSource.class);
        EasyMock.expect(dataSource.getPointValue(1)).andReturn(pvqh);
        EasyMock.replay(dataSource);
        ReflectionTestUtils.setField(parser, "dataSource", dataSource);
        
        results = parser.generatePointData(rowData2);
        data = results.get(lpo.getPaoIdentifier());
        Assert.assertEquals(1, data.size());
        PointData pointData = (PointData) results.get(lpo.getPaoIdentifier()).toArray()[0];
        Assert.assertEquals(2, pointData.getValue(), 0.1);
    }
}
