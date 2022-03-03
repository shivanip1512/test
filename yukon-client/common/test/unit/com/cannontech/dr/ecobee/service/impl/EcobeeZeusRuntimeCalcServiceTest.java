package com.cannontech.dr.ecobee.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.cannontech.dr.ecobee.service.EcobeeZeusRuntimeCalcService;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.RuntimeCalcServiceHelper;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class EcobeeZeusRuntimeCalcServiceTest {

    private static EcobeeZeusRuntimeCalcService ecobeeZeusRuntimeCalcService;
    private static RuntimeCalcServiceHelper runtimeCalcServiceHelper;
    
    private static final int ecobeeSmartSiId = 1;
    private static final int ecobee3Id = 2;
    private static final int ecobee3LiteId = 3;
    private static final int ecobeeSmartId = 4;
    
    private static LiteYukonPAObject ecobeeSmartSi;
    private static LiteYukonPAObject ecobee3;
    private static LiteYukonPAObject ecobee3Lite;
    private static LiteYukonPAObject ecobeeSmart;
    private static List<YukonPao> allThermostats;
    
    private static final DateTime date1 = TimeUtil.getStartOfHour(DateTime.now());
    private static final DateTime date2 = date1.minus(Duration.standardDays(1));
    private static final DateTime date3 = date1.minus(Duration.standardDays(2));
    private static final DateTime date4 = date1.minus(Duration.standardDays(3));
    
    @BeforeEach
    public void initEach() {
        ecobeeZeusRuntimeCalcService = new EcobeeZeusRuntimeCalcServiceImpl();
        runtimeCalcServiceHelper = new RuntimeCalcServiceHelper();
        ecobeeSmartSi = new LiteYukonPAObject(ecobeeSmartSiId, "ecobeeSmartSi", PaoCategory.DEVICE, PaoClass.THERMOSTAT, PaoType.ECOBEE_SMART_SI, "description", "F");

        ecobee3 = new LiteYukonPAObject(ecobee3Id, "ecobee3", PaoCategory.DEVICE, PaoClass.THERMOSTAT, PaoType.ECOBEE_3, "description", "F");

        ecobee3Lite = new LiteYukonPAObject(ecobee3LiteId, "ecobee3Lite", PaoCategory.DEVICE, PaoClass.THERMOSTAT, PaoType.ECOBEE_3_LITE, "description", "F");

        ecobeeSmart = new LiteYukonPAObject(ecobeeSmartId, "ecobeeSmart", PaoCategory.DEVICE, PaoClass.THERMOSTAT, PaoType.ECOBEE_SMART, "description", "F");

        allThermostats = Lists.newArrayList(ecobeeSmartSi, ecobee3, ecobee3Lite, ecobeeSmart);
    }
    
    @Test
    public void test_getAllThermostats() {

        PaoDao mockPaoDao = createNiceMock(PaoDao.class);

        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.ECOBEE_SMART_SI)).andReturn(Lists.newArrayList(ecobeeSmartSi));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.ECOBEE_3)).andReturn(Lists.newArrayList(ecobee3));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.ECOBEE_3_LITE)).andReturn(Lists.newArrayList(ecobee3Lite));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.ECOBEE_SMART)).andReturn(Lists.newArrayList(ecobeeSmart));
        replay(mockPaoDao);

        ReflectionTestUtils.setField(ecobeeZeusRuntimeCalcService, "paoDao", mockPaoDao);

        // Reflectively invoke private method
        List<YukonPao> thermostats = ReflectionTestUtils.invokeMethod(ecobeeZeusRuntimeCalcService, "getAllThermostats", new Object[0]);

        assertThat("thermostats list contains ecobeeSmart Si", thermostats, hasItems(ecobeeSmartSi));
        assertThat("thermostats list contains ecobee3", thermostats, hasItems(ecobee3));
        assertThat("thermostats list contains ecobee3Lite", thermostats, hasItems(ecobee3Lite));
        assertThat("thermostats list contains ecobeeSmart", thermostats, hasItems(ecobeeSmart));
        assertThat("thermostats list size", thermostats.size(), equalTo(4));
    }
    
    @Test
    public void test_getLastRuntime() {
        
        Map<PaoIdentifier, PointValueQualityHolder> runtimeValues = new HashMap<>();

        PointValueQualityHolder ecobeeSmartSiLastRuntime = buildPvqh(1, PointType.Analog, date1.toDate(), 10.0);
        PointValueQualityHolder ecobee3LastRuntime = buildPvqh(2, PointType.Analog, date2.toDate(), 20.0);
        PointValueQualityHolder ecobee3LiteLastRuntime = buildPvqh(3, PointType.Analog, date3.toDate(), 30.0);
        PointValueQualityHolder ecobeeSmartLastRuntime = buildPvqh(4, PointType.Analog, date4.toDate(), 40.0);

        runtimeValues.put(ecobeeSmartSi.getPaoIdentifier(), ecobeeSmartSiLastRuntime);
        runtimeValues.put(ecobee3.getPaoIdentifier(), ecobee3LastRuntime);
        runtimeValues.put(ecobee3Lite.getPaoIdentifier(), ecobee3LiteLastRuntime);
        runtimeValues.put(ecobeeSmart.getPaoIdentifier(), ecobeeSmartLastRuntime);
        
        RawPointHistoryDao mockRphDao = createNiceMock(RawPointHistoryDao.class);
        expect(mockRphDao.getSingleAttributeData(allThermostats, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG, false, null))
            .andReturn(runtimeValues);
        replay(mockRphDao);
        
        ReflectionTestUtils.setField(ecobeeZeusRuntimeCalcService, "rphDao", mockRphDao);
        ReflectionTestUtils.setField(runtimeCalcServiceHelper, "rphDao", mockRphDao);
        // Reflectively invoke private method 
        Map<Integer, DateTime> lastRuntimes = ReflectionTestUtils.invokeMethod(runtimeCalcServiceHelper, 
                                                                               "getLastRuntimes", allThermostats);
        
        assertThat("ecobeeSmartSi present in last runtimes", lastRuntimes.keySet(), hasItems(ecobeeSmartSiId));
        assertThat("ecobee3 present in last runtimes", lastRuntimes.keySet(), hasItems(ecobee3Id));
        assertThat("ecobee3Lite present in last runtimes", lastRuntimes.keySet(), hasItems(ecobee3LiteId));
        assertThat("ecobeeSmart present in last runtimes", lastRuntimes.keySet(), hasItems(ecobeeSmartId));
        
        assertThat("correct last runtime date for ecobeeSmartSi", lastRuntimes.get(ecobeeSmartSiId), equalTo(date1));
        assertThat("correct last runtime date for ecobee3", lastRuntimes.get(ecobee3Id), equalTo(date2));
        assertThat("correct last runtime date for ecobee3Lite", lastRuntimes.get(ecobee3LiteId), equalTo(date3));
        assertThat("correct last runtime date for ecobeeSmart", lastRuntimes.get(ecobeeSmartId), equalTo(date4));
        
        assertThat("number of entries in last runtimes", lastRuntimes.size(), equalTo(4));
    }

    @Test
    public void test_insertRuntimes_noValues() {
        
        Map<DateTime, Integer> hourlyRuntimeSeconds = new HashMap<>();
        
        AsyncDynamicDataSource mockDispatch = createStrictMock(AsyncDynamicDataSource.class);
        replay(mockDispatch);
        
        ReflectionTestUtils.setField(ecobeeZeusRuntimeCalcService, "asyncDynamicDataSource", mockDispatch);
        
        // Reflectively invoke private method 
        ReflectionTestUtils.invokeMethod(runtimeCalcServiceHelper, "insertRuntimes",
                                         new Object[] {ecobeeSmartSi, hourlyRuntimeSeconds, null});
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
        expect(mockPointDao.getLitePointIdByDeviceId_Offset_PointType(ecobeeSmartSiId, 5, PointType.Analog))
            .andReturn(new LitePoint(1));
        replay(mockPointDao);
        
        ReflectionTestUtils.setField(ecobeeZeusRuntimeCalcService, "asyncDynamicDataSource", mockDispatch);
        ReflectionTestUtils.setField(runtimeCalcServiceHelper, "asyncDynamicDataSource", mockDispatch);
        ReflectionTestUtils.setField(runtimeCalcServiceHelper, "pointDao", mockPointDao);
        
        // Reflectively invoke private method 
        ReflectionTestUtils.invokeMethod(runtimeCalcServiceHelper, "insertRuntimes",
                                         new Object[] {ecobeeSmartSi, hourlyRuntimeSeconds, null});
        
        PointData pointData1 = buildPointData(1, date4, 10);
        PointData pointData2 = buildPointData(1, date3, 0);
        PointData pointData3 = buildPointData(1, date2, 20);
        PointData pointData4 = buildPointData(1, date1, 11);
        
        Iterable<PointData> capturedData = dispatchDataCapture.getValue();
        assertThat("point data 1 insertion", capturedData, hasItems(pointData1));
        assertThat("point data 2 insertion", capturedData, hasItems(pointData2));
        assertThat("point data 3 insertion", capturedData, hasItems(pointData3));
        assertThat("point data 4 insertion", capturedData, hasItems(pointData4));
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
        expect(mockPointDao.getLitePointIdByDeviceId_Offset_PointType(ecobeeSmartSiId, 5, PointType.Analog))
            .andReturn(new LitePoint(1));
        replay(mockPointDao);
        
        ReflectionTestUtils.setField(ecobeeZeusRuntimeCalcService, "asyncDynamicDataSource", mockDispatch);
        ReflectionTestUtils.setField(runtimeCalcServiceHelper, "asyncDynamicDataSource", mockDispatch);
        ReflectionTestUtils.setField(runtimeCalcServiceHelper, "pointDao", mockPointDao);
        
        Predicate<Map.Entry<DateTime, Integer>> filter = entry -> entry.getKey().isAfter(date4);
        
        ReflectionTestUtils.invokeMethod(runtimeCalcServiceHelper, "insertRuntimes",
                                         new Object[] {ecobeeSmartSi, hourlyRuntimeSeconds, filter});
        
        PointData pointData2 = buildPointData(1, date3, 0);
        PointData pointData3 = buildPointData(1, date2, 20);
        PointData pointData4 = buildPointData(1, date1, 11);
        
        Iterable<PointData> capturedData = dispatchDataCapture.getValue();
        assertThat("point data 2 insertion", capturedData, hasItems(pointData2));
        assertThat("point data 3 insertion", capturedData, hasItems(pointData3));
        assertThat("point data 4 insertion", capturedData, hasItems(pointData4));
        assertThat("quantity of point data inserted", Iterators.size(capturedData.iterator()), equalTo(3));
    }
    
    @Test
    public void test_getRuntimeStatusFromPoint() {
        PointValueQualityHolder pointValue0 = buildPvqh(1, PointType.Status, date1.toDate(), 0.0);
        PointValueQualityHolder pointValue1 = buildPvqh(1, PointType.Status, date2.toDate(), 1.0);
        PointValueQualityHolder pointValue2 = buildPvqh(1, PointType.Status, date3.toDate(), 2.0);
        
        // Reflectively invoke private method 
        // EcobeeZeusRuntimeCalcService.getRuntimeStatusFromPoint
        DatedRuntimeStatus status0 = ReflectionTestUtils.invokeMethod(ecobeeZeusRuntimeCalcService, 
                                                                      "getRuntimeStatusFromPoint", pointValue0);
        DatedRuntimeStatus status1 = ReflectionTestUtils.invokeMethod(ecobeeZeusRuntimeCalcService, 
                                                                      "getRuntimeStatusFromPoint", pointValue1);
        DatedRuntimeStatus status2 = ReflectionTestUtils.invokeMethod(ecobeeZeusRuntimeCalcService, 
                                                                      "getRuntimeStatusFromPoint", pointValue2);
        
        assertThat("status 0 date", status0.getDate(), equalTo(date1));
        assertThat("status 0 heating == running", status0.isActive(), equalTo(true));
        assertThat("status 1 date", status1.getDate(), equalTo(date2));
        assertThat("status 1 cooling == running", status1.isActive(), equalTo(true));
        assertThat("status 2 date", status2.getDate(), equalTo(date3));
        assertThat("status 2 off == stopped", status2.isActive(), equalTo(false));
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
