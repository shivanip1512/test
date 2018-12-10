package com.cannontech.dr.honeywellWifi;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.honeywellWifi.azure.event.EquipmentStatus;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.RuntimeStatus;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class HoneywellWifiRuntimeCalcServiceTest {
    private static HoneywellWifiRuntimeCalcService honeywellWifiRuntimeCalcService;
    
    private static final int honeywell9000Id = 1;
    private static final int focusProId = 2;
    private static final int visionPro8000Id = 3;
    private static final int honeywellThermostatId = 4;
    
    private static LiteYukonPAObject honeywell9000;
    private static LiteYukonPAObject focusPro;
    private static LiteYukonPAObject visionPro8000;
    private static LiteYukonPAObject honeywellThermostat;
    private static List<YukonPao> allThermostats;
    
    private static final DateTime date1 = TimeUtil.getStartOfHour(DateTime.now());
    private static final DateTime date2 = date1.minus(Duration.standardDays(1));
    private static final DateTime date3 = date1.minus(Duration.standardDays(2));
    private static final DateTime date4 = date1.minus(Duration.standardDays(3));
    
    @Before
    public void initEach() {
        honeywellWifiRuntimeCalcService = new HoneywellWifiRuntimeCalcServiceImpl();
        honeywell9000 = new LiteYukonPAObject(honeywell9000Id, "honeywell9000", 
                                              PaoCategory.DEVICE, PaoClass.THERMOSTAT, PaoType.HONEYWELL_9000, 
                                              "description", "F");
        
        focusPro = new LiteYukonPAObject(focusProId, "focusPro", 
                                         PaoCategory.DEVICE, PaoClass.THERMOSTAT, PaoType.HONEYWELL_FOCUSPRO, 
                                         "description", "F");
        
        visionPro8000 = new LiteYukonPAObject(visionPro8000Id, "visionPro8000", 
                                              PaoCategory.DEVICE, PaoClass.THERMOSTAT, PaoType.HONEYWELL_VISIONPRO_8000, 
                                              "description", "F");
        
        honeywellThermostat = new LiteYukonPAObject(honeywellThermostatId, "honeywellThermostat", 
                                                    PaoCategory.DEVICE, PaoClass.THERMOSTAT, PaoType.HONEYWELL_THERMOSTAT, 
                                                   "description", "F");
        
        allThermostats = Lists.newArrayList(honeywell9000, focusPro, visionPro8000, honeywellThermostat);
    }
    
    @Test
    public void test_getAllThermostats() {
        
        PaoDao mockPaoDao = createNiceMock(PaoDao.class);
        
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.HONEYWELL_9000))
            .andReturn(Lists.newArrayList(honeywell9000));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.HONEYWELL_FOCUSPRO))
            .andReturn(Lists.newArrayList(focusPro));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.HONEYWELL_VISIONPRO_8000))
            .andReturn(Lists.newArrayList(visionPro8000));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.HONEYWELL_THERMOSTAT))
            .andReturn(Lists.newArrayList(honeywellThermostat));
        replay(mockPaoDao);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "paoDao", mockPaoDao);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.getAllThermostats();
        List<YukonPao> thermostats = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, "getAllThermostats", new Object[0]);
        
        assertThat("thermostats list contains honeywell 9000", thermostats, hasItem(honeywell9000));
        assertThat("thermostats list contains focus pro", thermostats, hasItem(focusPro));
        assertThat("thermostats list contains vision pro 8000", thermostats, hasItem(visionPro8000));
        assertThat("thermostats list contains honeywell thermostat", thermostats, hasItem(honeywellThermostat));
        assertThat("thermostats list size", thermostats.size(), equalTo(4));
    }
    
    @Test
    public void test_getLastRuntime() {
        
        Map<PaoIdentifier, PointValueQualityHolder> runtimeValues = new HashMap<>();

        PointValueQualityHolder honeywell9000LastRuntime = 
                buildPvqh(1, PointType.Analog, date1.toDate(), 10.0);
        PointValueQualityHolder focusProLastRuntime = 
                buildPvqh(2, PointType.Analog, date2.toDate(), 20.0);
        PointValueQualityHolder visionPro8000LastRuntime = 
                buildPvqh(3, PointType.Analog, date3.toDate(), 30.0);
        PointValueQualityHolder honeywellThermostatLastRuntime = 
                buildPvqh(4, PointType.Analog, date4.toDate(), 40.0);
        
        runtimeValues.put(honeywell9000.getPaoIdentifier(), honeywell9000LastRuntime);
        runtimeValues.put(focusPro.getPaoIdentifier(), focusProLastRuntime);
        runtimeValues.put(visionPro8000.getPaoIdentifier(), visionPro8000LastRuntime);
        runtimeValues.put(honeywellThermostat.getPaoIdentifier(), honeywellThermostatLastRuntime);
        
        RawPointHistoryDao mockRphDao = createNiceMock(RawPointHistoryDao.class);
        expect(mockRphDao.getSingleAttributeData(allThermostats, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG, false, null))
            .andReturn(runtimeValues);
        replay(mockRphDao);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "rphDao", mockRphDao);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.getLastRuntimes(allThermostats);
        Map<Integer, DateTime> lastRuntimes = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, 
                                                                               "getLastRuntimes", allThermostats);
        
        
        assertThat("honeywell9000 present in last runtimes", lastRuntimes.keySet(), hasItem(honeywell9000Id));
        assertThat("focusPro present in last runtimes", lastRuntimes.keySet(), hasItem(focusProId));
        assertThat("visionPro8000 present in last runtimes", lastRuntimes.keySet(), hasItem(visionPro8000Id));
        assertThat("honeywellThermostat present in last runtimes", lastRuntimes.keySet(), hasItem(honeywellThermostatId));
        
        assertThat("correct last runtime date for honeywell9000", lastRuntimes.get(honeywell9000Id), equalTo(date1));
        assertThat("correct last runtime date for focusPro", lastRuntimes.get(focusProId), equalTo(date2));
        assertThat("correct last runtime date for visionPro8000", lastRuntimes.get(visionPro8000Id), equalTo(date3));
        assertThat("correct last runtime date for honeywellThermostat", lastRuntimes.get(honeywellThermostatId), equalTo(date4));
        
        assertThat("number of entries in last runtimes", lastRuntimes.size(), equalTo(4));
    }
    
    @Test(expected=IllegalStateException.class)
    public void test_getEndOfRuntimeCalcRange_nullQueueAndNullLastProcessed() {
        
        HoneywellWifiDataListener mockDataListener = createNiceMock(HoneywellWifiDataListener.class);
        expect(mockDataListener.getLastEmptyQueueTime())
            .andReturn(null);
        expect(mockDataListener.getLastProcessedMessageTime())
            .andReturn(null);
        replay(mockDataListener);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "dataListener", mockDataListener);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.getEndOfRuntimeCalcRange();
        ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, "getEndOfRuntimeCalcRange", new Object[0]);
    }
    
    @Test
    public void test_getEndOfRuntimeCalcRange_nullQueue() {
        
        HoneywellWifiDataListener mockDataListener = createNiceMock(HoneywellWifiDataListener.class);
        expect(mockDataListener.getLastEmptyQueueTime())
            .andReturn(null);
        expect(mockDataListener.getLastProcessedMessageTime())
            .andReturn(date2);
        replay(mockDataListener);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "dataListener", mockDataListener);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.getEndOfRuntimeCalcRange();
        DateTime lastRuntimeStatusDate = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, 
                                                                          "getEndOfRuntimeCalcRange", new Object[0]);
        
        assertThat(lastRuntimeStatusDate, equalTo(date2));
    }
    
    @Test
    public void test_getEndOfRuntimeCalcRange_nullLastProcessed() {
        
        HoneywellWifiDataListener mockDataListener = createNiceMock(HoneywellWifiDataListener.class);
        expect(mockDataListener.getLastEmptyQueueTime())
            .andReturn(date2);
        expect(mockDataListener.getLastProcessedMessageTime())
            .andReturn(null);
        replay(mockDataListener);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "dataListener", mockDataListener);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.getEndOfRuntimeCalcRange();
        DateTime lastRuntimeStatusDate = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, 
                                                                          "getEndOfRuntimeCalcRange", new Object[0]);
        
        assertThat(lastRuntimeStatusDate, equalTo(date2));
    }
    
    @Test
    public void test_getEndOfRuntimeCalcRange_nullQueue_recentLastProcessed() {
        
        HoneywellWifiDataListener mockDataListener = createNiceMock(HoneywellWifiDataListener.class);
        expect(mockDataListener.getLastEmptyQueueTime())
            .andReturn(null);
        expect(mockDataListener.getLastProcessedMessageTime())
            .andReturn(DateTime.now());
        replay(mockDataListener);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "dataListener", mockDataListener);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.getEndOfRuntimeCalcRange();
        DateTime lastRuntimeStatusDate = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, 
                                                                          "getEndOfRuntimeCalcRange", new Object[0]);
        
        DateTime startOfPreviousHour = TimeUtil.getStartOfHour(DateTime.now().minus(Duration.standardHours(1)));
        assertThat(lastRuntimeStatusDate, equalTo(startOfPreviousHour));
    }
    
    @Test
    public void test_getEndOfRuntimeCalcRange_nullLastProcessed_recentQueue() {
        
        HoneywellWifiDataListener mockDataListener = createNiceMock(HoneywellWifiDataListener.class);
        expect(mockDataListener.getLastEmptyQueueTime())
            .andReturn(DateTime.now());
        expect(mockDataListener.getLastProcessedMessageTime())
            .andReturn(null);
        replay(mockDataListener);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "dataListener", mockDataListener);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.getEndOfRuntimeCalcRange();
        DateTime lastRuntimeStatusDate = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, 
                                                                          "getEndOfRuntimeCalcRange", new Object[0]);
        
        DateTime startOfPreviousHour = TimeUtil.getStartOfHour(DateTime.now().minus(Duration.standardHours(1)));
        assertThat(lastRuntimeStatusDate, equalTo(startOfPreviousHour));
    }
    
    @Test
    public void test_insertRuntimes_noValues() {
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = new HashMap<>();
        
        AsyncDynamicDataSource mockDispatch = createStrictMock(AsyncDynamicDataSource.class);
        replay(mockDispatch);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "asyncDynamicDataSource", mockDispatch);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.insertRuntimes(honeywell9000, hourlyRuntimeSeconds, null);
        ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, "insertRuntimes",
                                         new Object[] {honeywell9000, hourlyRuntimeSeconds, null});
        //Mock dispatch will throw an exception if any methods are called.
    }
    
    @Test
    public void test_insertRuntimes() {
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = new HashMap<>();
        hourlyRuntimeSeconds.put(date4, 600);
        hourlyRuntimeSeconds.put(date3, 0);
        hourlyRuntimeSeconds.put(date2, 1200);
        hourlyRuntimeSeconds.put(date1, 660);
        
        Capture<Iterable<PointData>> dispatchDataCapture = new Capture<>();
        AsyncDynamicDataSource mockDispatch = createStrictMock(AsyncDynamicDataSource.class);
        mockDispatch.putValues(EasyMock.capture(dispatchDataCapture));
        expectLastCall();
        replay(mockDispatch);
        
        PointDao mockPointDao = createNiceMock(PointDao.class);
        expect(mockPointDao.getLitePointIdByDeviceId_Offset_PointType(honeywell9000Id, 5, PointType.Analog))
            .andReturn(new LitePoint(1));
        replay(mockPointDao);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "asyncDynamicDataSource", mockDispatch);
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "pointDao", mockPointDao);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.insertRuntimes(honeywell9000, hourlyRuntimeSeconds, null)
        ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, "insertRuntimes",
                                         new Object[] {honeywell9000, hourlyRuntimeSeconds, null});
        
        PointData pointData1 = buildPointData(1, date4, 10);
        PointData pointData2 = buildPointData(1, date3, 0);
        PointData pointData3 = buildPointData(1, date2, 20);
        PointData pointData4 = buildPointData(1, date1, 11);
        
        Iterable<PointData> capturedData = dispatchDataCapture.getValue();
        assertThat("point data 1 insertion", capturedData, hasItem(pointData1));
        assertThat("point data 2 insertion", capturedData, hasItem(pointData2));
        assertThat("point data 3 insertion", capturedData, hasItem(pointData3));
        assertThat("point data 4 insertion", capturedData, hasItem(pointData4));
        assertThat("quantity of point data inserted", Iterators.size(capturedData.iterator()), equalTo(4));
    }
    
    @Test
    public void test_insertRuntimes_withFilter() {
        Map<DateTime, Integer> hourlyRuntimeSeconds = new HashMap<>();
        hourlyRuntimeSeconds.put(date4, 600);
        hourlyRuntimeSeconds.put(date3, 0);
        hourlyRuntimeSeconds.put(date2, 1200);
        hourlyRuntimeSeconds.put(date1, 660);
        
        Capture<Iterable<PointData>> dispatchDataCapture = new Capture<>();
        AsyncDynamicDataSource mockDispatch = createStrictMock(AsyncDynamicDataSource.class);
        mockDispatch.putValues(EasyMock.capture(dispatchDataCapture));
        expectLastCall();
        replay(mockDispatch);
        
        PointDao mockPointDao = createNiceMock(PointDao.class);
        expect(mockPointDao.getLitePointIdByDeviceId_Offset_PointType(honeywell9000Id, 5, PointType.Analog))
            .andReturn(new LitePoint(1));
        replay(mockPointDao);
        
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "asyncDynamicDataSource", mockDispatch);
        ReflectionTestUtils.setField(honeywellWifiRuntimeCalcService, "pointDao", mockPointDao);
        
        Predicate<Map.Entry<DateTime, Integer>> filter = entry -> entry.getKey().isAfter(date4);
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.insertRuntimes(honeywell9000, hourlyRuntimeSeconds, filter)
        ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, "insertRuntimes",
                                         new Object[] {honeywell9000, hourlyRuntimeSeconds, filter});
        
        PointData pointData2 = buildPointData(1, date3, 0);
        PointData pointData3 = buildPointData(1, date2, 20);
        PointData pointData4 = buildPointData(1, date1, 11);
        
        Iterable<PointData> capturedData = dispatchDataCapture.getValue();
        assertThat("point data 2 insertion", capturedData, hasItem(pointData2));
        assertThat("point data 3 insertion", capturedData, hasItem(pointData3));
        assertThat("point data 4 insertion", capturedData, hasItem(pointData4));
        assertThat("quantity of point data inserted", Iterators.size(capturedData.iterator()), equalTo(3));
    }
    
    @Test
    public void test_getRuntimeStatusFromPoint() {
        PointValueQualityHolder pointValue0 = buildPvqh(1, PointType.Status, date1.toDate(), 
                                                        EquipmentStatus.HEATING.getStateValue());
        PointValueQualityHolder pointValue1 = buildPvqh(1, PointType.Status, date2.toDate(), 
                                                        EquipmentStatus.COOLING.getStateValue());
        PointValueQualityHolder pointValue2 = buildPvqh(1, PointType.Status, date3.toDate(), 
                                                        EquipmentStatus.OFF.getStateValue());
        
        // Reflectively invoke private method 
        // honeywellWifiRuntimeCalcService.getRuntimeStatusFromPoint
        DatedRuntimeStatus status0 = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, 
                                                                      "getRuntimeStatusFromPoint", pointValue0);
        DatedRuntimeStatus status1 = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, 
                                                                      "getRuntimeStatusFromPoint", pointValue1);
        DatedRuntimeStatus status2 = ReflectionTestUtils.invokeMethod(honeywellWifiRuntimeCalcService, 
                                                                      "getRuntimeStatusFromPoint", pointValue2);
        
        assertThat("status 0 date", status0.getDate(), equalTo(date1));
        assertThat("status 0 heating == running", status0.getRuntimeStatus(), equalTo(RuntimeStatus.RUNNING));
        assertThat("status 1 date", status1.getDate(), equalTo(date2));
        assertThat("status 1 cooling == running", status1.getRuntimeStatus(), equalTo(RuntimeStatus.RUNNING));
        assertThat("status 2 date", status2.getDate(), equalTo(date3));
        assertThat("status 2 off == stopped", status2.getRuntimeStatus(), equalTo(RuntimeStatus.STOPPED));
    }
    
    @Test
    public void test_calculateRuntimes() {
        //TODO
    }
    
    private PointValueQualityHolder buildPvqh(int pointId, PointType type, Date timestamp, Double value) {
        return PointValueBuilder.create()
                                .withPointId(pointId)
                                .withPointQuality(PointQuality.Normal)
                                .withType(type)
                                .withTimeStamp(timestamp)
                                .withValue(value)
                                .build();
    }
    
    private PointData buildPointData(int pointId, DateTime date, double value) {
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setType(PointType.Analog.getPointTypeId()); //Analog
        pointData.setMillis(0);
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTime(date.toDate());
        pointData.setValue(value);
        pointData.setTagsPointMustArchive(true);
        return pointData;
    }
}
