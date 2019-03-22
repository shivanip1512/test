package com.cannontech.dr.itron.service.impl;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.itron.ItronDataEventType;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.RuntimeCalcServiceImpl;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ItronRuntimeCalcServiceImplTest {
    private ItronRuntimeCalcServiceImpl itronRuntimeCalcService = new ItronRuntimeCalcServiceImpl();
    
    private static final int lcr6600sId = 17;
    private static final int lcr6601sId = 79;
    
    private LiteYukonPAObject lcr6600s;
    private LiteYukonPAObject lcr6601s;
    private List<YukonPao> allDevices;
    
    private LitePoint lcr6600s_radioLinkQuality;
    private LitePoint lcr6600s_relay1_relayState;
    private LitePoint lcr6600s_relay1_runtimeLog_15m;
    private LitePoint lcr6600s_relay1_runtimeLog_60m;
    private LitePoint lcr6600s_relay1_shedStatus;
    private LitePoint lcr6600s_relay1_shedtimeLog_5m;
    private LitePoint lcr6600s_relay1_shedtimeLog_15m;
    private List<LitePoint> lcr6600s_points;
    
    private static final DateTime date1 = DateTime.parse("2019-03-20T11:13:27");
    private static final DateTime date2 = date1.plus(Duration.standardHours(8));
    
    @Before
    public void initEach() {
        lcr6600s = new LiteYukonPAObject(lcr6600sId, "LCR-6600S", PaoCategory.DEVICE, PaoClass.ITRON, PaoType.LCR6600S, "description", "F");
        lcr6601s = new LiteYukonPAObject(lcr6601sId, "LCR-6601S", PaoCategory.DEVICE, PaoClass.ITRON, PaoType.LCR6601S, "description", "F");

        allDevices = ImmutableList.of(lcr6600s, lcr6601s);

        lcr6600s_radioLinkQuality = 
                new LitePoint(178, "Radio Link Quality", PointType.Analog.getPointTypeId(), lcr6600sId, 56, -1);
        lcr6600s_relay1_relayState = 
                new LitePoint(179, "Relay 1 Relay State", PointType.Status.getPointTypeId(), lcr6600sId, 3, -1);
        lcr6600s_relay1_runtimeLog_15m = 
                new LitePoint(180, "Relay 1 Run Time Data Log 15 Minutes", PointType.Analog.getPointTypeId(), lcr6600sId, 12, -1);
        lcr6600s_relay1_runtimeLog_60m = 
                new LitePoint(181, "Relay 1 Run Time Data Log 60 Minutes", PointType.Analog.getPointTypeId(), lcr6600sId, 10, -1);
        lcr6600s_relay1_shedStatus = 
                new LitePoint(182, "Relay 1 Shed Status", PointType.Status.getPointTypeId(), lcr6600sId, 4, -1);
        lcr6600s_relay1_shedtimeLog_5m = 
                new LitePoint(183, "Relay 1 Shed Time Data Log 5 Minutes", PointType.Analog.getPointTypeId(), lcr6600sId, 15, -1);
        lcr6600s_relay1_shedtimeLog_15m = 
                new LitePoint(184, "Relay 1 Shed Time Data Log 15 Minutes", PointType.Analog.getPointTypeId(), lcr6600sId, 16, -1);
        
        lcr6600s_points = ImmutableList.of(lcr6600s_radioLinkQuality,
                                           lcr6600s_relay1_relayState,
                                           lcr6600s_relay1_runtimeLog_15m,
                                           lcr6600s_relay1_runtimeLog_60m,
                                           lcr6600s_relay1_shedStatus,
                                           lcr6600s_relay1_shedtimeLog_5m,
                                           lcr6600s_relay1_shedtimeLog_15m);
    }
    
    @Test
    public void test_getAllDevices() {
        
        PaoDao mockPaoDao = createNiceMock(PaoDao.class);
        
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.LCR6600S))
            .andReturn(Lists.newArrayList(lcr6600s));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.LCR6601S))
            .andReturn(Lists.newArrayList(lcr6601s));
        replay(mockPaoDao);
        
        ReflectionTestUtils.setField(itronRuntimeCalcService, "paoDao", mockPaoDao);
        
        List<YukonPao> devices = ReflectionTestUtils.invokeMethod(itronRuntimeCalcService, "getAllDevices");
        
        assertThat("device list contains lcr6600s", devices, hasItem(lcr6600s));
        assertThat("device list contains lcr6601s", devices, hasItem(lcr6601s));
        assertThat("device list size", devices.size(), equalTo(2));
    }
    
    @Test
    public void test_getRuntimeStatusFromPoint() {
        PointValueQualityHolder pointValue0 = buildPvqh(lcr6600s_relay1_relayState, date1, 
                                                        (double)ItronDataEventType.LOAD_ON.getValue());
        PointValueQualityHolder pointValue1 = buildPvqh(lcr6600s_relay1_relayState, date2, 
                                                        (double)ItronDataEventType.LOAD_OFF.getValue());
        
        // Reflectively invoke private method 
        // ItronRuntimeCalcService.getRuntimeStatusFromPoint
        DatedRuntimeStatus status0 = ReflectionTestUtils.invokeMethod(itronRuntimeCalcService, 
                                                                      "getRuntimeStatusFromPoint", pointValue0);
        DatedRuntimeStatus status1 = ReflectionTestUtils.invokeMethod(itronRuntimeCalcService, 
                                                                      "getRuntimeStatusFromPoint", pointValue1);
        
        assertThat("status 0 date", status0.getDate(), equalTo(date1));
        assertThat("status 0 runtime == active", status0.isActive(), equalTo(true));
        assertThat("status 1 date", status1.getDate(), equalTo(date2));
        assertThat("status 1 runtime == inactive", status1.isActive(), equalTo(false));
    }
    
    @Test
    public void test_addBoundaryValues_valuesWithinRange() {
        
        ItronRuntimeCalcServiceImpl itronRuntimeCalcService = EasyMock.createMockBuilder(ItronRuntimeCalcServiceImpl.class)
                .addMockedMethod("getPrecedingArchivedValue", PointValueHolder.class)
                .createMock();
        
        DateTime rangeStart = DateTime.parse("2019-03-20T11:13:27.000Z");
        DateTime rangeEnd = rangeStart.plusMinutes(76);

        var value1 = buildPvqh(lcr6600s_relay1_relayState, rangeStart.plusMinutes(22), 6.0);
        var value2 = buildPvqh(lcr6600s_relay1_relayState, rangeStart.plusMinutes(44), 171.0);
        
        Iterable<PointValueHolder> relayStatuses = List.of(value1, value2);
        
        Range<Instant> logRange = Range.inclusive(rangeStart.toInstant(), rangeEnd.toInstant());
        
        expect(itronRuntimeCalcService.getPrecedingArchivedValue(value1))
            .andReturn(List.of(buildPvqh(lcr6600s_relay1_relayState, rangeStart.minusMinutes(6), 9.0)));
        replay(itronRuntimeCalcService);

        Iterable<PointValueHolder> results = itronRuntimeCalcService.addBoundaryValues(relayStatuses, logRange); 
        
        assertThat("boundary values added", Iterables.size(results), equalTo(4));
        
        PointValueHolder first = Iterables.getFirst(results, null);
        
        assertThat("preceding boundary value", first.getValue(), equalTo(9.0));
        assertThat("preceding boundary timestamp", first.getPointDataTimeStamp().getTime(), equalTo(rangeStart.minusMinutes(6).getMillis()));
        assertThat("preceding boundary id", first.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));

        PointValueHolder last = Iterables.getLast(results, null);
        
        assertThat("trailing boundary value", last.getValue(), equalTo(171.0));
        assertThat("trailing boundary timestamp", last.getPointDataTimeStamp().getTime(), equalTo(rangeEnd.getMillis()));
        assertThat("trailing boundary id", last.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));
    }
    
    @Test
    public void test_addBoundaryValues_recentValueOutsideRange() {
        
        ItronRuntimeCalcServiceImpl itronRuntimeCalcService = EasyMock.createMockBuilder(ItronRuntimeCalcServiceImpl.class)
                .addMockedMethod("getPrecedingArchivedValue", PointValueHolder.class)
                .createMock();
        
        DateTime rangeStart = DateTime.parse("2019-03-20T11:13:27.000Z");
        DateTime rangeEnd = rangeStart.plusMinutes(76);

        var value1 = buildPvqh(lcr6600s_relay1_relayState, rangeStart.minusMinutes(7), 6.0);
        
        Iterable<PointValueHolder> relayStatuses = List.of(value1);
        
        Range<Instant> logRange = Range.inclusive(rangeStart.toInstant(), rangeEnd.toInstant());
        
        //  expect no calls
        replay(itronRuntimeCalcService);
        
        Iterable<PointValueHolder> results = itronRuntimeCalcService.addBoundaryValues(relayStatuses, logRange); 
        
        assertThat("boundary values added", Iterables.size(results), equalTo(2));
        
        PointValueHolder last = Iterables.getLast(results, null);
        
        assertThat("trailing boundary value", last.getValue(), equalTo(6.0));
        assertThat("trailing boundary timestamp", last.getPointDataTimeStamp().getTime(), equalTo(rangeEnd.getMillis()));
        assertThat("trailing boundary id", last.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));
    }
    
    @Test
    public void test_addBoundaryValues_valueAtRangeEnd() {
        
        ItronRuntimeCalcServiceImpl itronRuntimeCalcService = EasyMock.createMockBuilder(ItronRuntimeCalcServiceImpl.class)
                .addMockedMethod("getPrecedingArchivedValue", PointValueHolder.class)
                .createMock();
        
        DateTime rangeStart = DateTime.parse("2019-03-20T11:13:27.000Z");
        DateTime rangeEnd = rangeStart.plusMinutes(76);

        var value1 = buildPvqh(lcr6600s_relay1_relayState, rangeStart.plusMinutes(22), 6.0);
        var value2 = buildPvqh(lcr6600s_relay1_relayState, rangeEnd, 171.0);
        
        Iterable<PointValueHolder> relayStatuses = List.of(value1, value2);
        
        Range<Instant> logRange = Range.inclusive(rangeStart.toInstant(), rangeEnd.toInstant());
        
        expect(itronRuntimeCalcService.getPrecedingArchivedValue(value1))
            .andReturn(List.of(buildPvqh(lcr6600s_relay1_relayState, rangeStart.minusMinutes(6), 9.0)));
        replay(itronRuntimeCalcService);
    
        Iterable<PointValueHolder> results = itronRuntimeCalcService.addBoundaryValues(relayStatuses, logRange); 
        
        assertThat("boundary values added", Iterables.size(results), equalTo(3));
        
        PointValueHolder first = Iterables.getFirst(results, null);
        
        assertThat("preceding boundary value", first.getValue(), equalTo(9.0));
        assertThat("preceding boundary timestamp", first.getPointDataTimeStamp().getTime(), equalTo(rangeStart.minusMinutes(6).getMillis()));
        assertThat("preceding boundary id", first.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));

        PointValueHolder last = Iterables.getLast(results, null);
        
        assertThat("trailing boundary value", last.getValue(), equalTo(171.0));
        assertThat("trailing boundary timestamp", last.getPointDataTimeStamp().getTime(), equalTo(rangeEnd.getMillis()));
        assertThat("trailing boundary id", last.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));
    }
    
    @Test
    public void test_addBoundaryValues_valueAtRangeStart() {
        
        ItronRuntimeCalcServiceImpl itronRuntimeCalcService = EasyMock.createMockBuilder(ItronRuntimeCalcServiceImpl.class)
                .addMockedMethod("getPrecedingArchivedValue", PointValueHolder.class)
                .createMock();
        
        DateTime rangeStart = DateTime.parse("2019-03-20T11:13:27.000Z");
        DateTime rangeEnd = rangeStart.plusMinutes(76);

        var value1 = buildPvqh(lcr6600s_relay1_relayState, rangeStart, 6.0);
        var value2 = buildPvqh(lcr6600s_relay1_relayState, rangeStart.plusMinutes(44), 171.0);
        
        Iterable<PointValueHolder> relayStatuses = List.of(value1, value2);
        
        Range<Instant> logRange = Range.inclusive(rangeStart.toInstant(), rangeEnd.toInstant());
        
        expect(itronRuntimeCalcService.getPrecedingArchivedValue(value1))
            .andReturn(List.of(buildPvqh(lcr6600s_relay1_relayState, rangeStart.minusMinutes(6), 9.0)));
        replay(itronRuntimeCalcService);

        Iterable<PointValueHolder> results = itronRuntimeCalcService.addBoundaryValues(relayStatuses, logRange); 
        
        assertThat("boundary values added", Iterables.size(results), equalTo(3));
        
        PointValueHolder first = Iterables.getFirst(results, null);
        
        assertThat("preceding boundary value", first.getValue(), equalTo(6.0));
        assertThat("preceding boundary timestamp", first.getPointDataTimeStamp().getTime(), equalTo(rangeStart.getMillis()));
        assertThat("preceding boundary id", first.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));

        PointValueHolder last = Iterables.getLast(results, null);
        
        assertThat("trailing boundary value", last.getValue(), equalTo(171.0));
        assertThat("trailing boundary timestamp", last.getPointDataTimeStamp().getTime(), equalTo(rangeEnd.getMillis()));
        assertThat("trailing boundary id", last.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));
    }
    
    @Test
    public void test_addBoundaryValues_noRangeStart() {
        
        ItronRuntimeCalcServiceImpl itronRuntimeCalcService = EasyMock.createMockBuilder(ItronRuntimeCalcServiceImpl.class)
                .addMockedMethod("getPrecedingArchivedValue", PointValueHolder.class)
                .createMock();
        
        DateTime rangeStart = DateTime.parse("2019-03-20T11:13:27.000Z");
        DateTime rangeEnd = rangeStart.plusMinutes(76);

        var value1 = buildPvqh(lcr6600s_relay1_relayState, rangeStart.plusMinutes(22), 6.0);
        var value2 = buildPvqh(lcr6600s_relay1_relayState, rangeStart.plusMinutes(44), 171.0);
        
        Iterable<PointValueHolder> relayStatuses = List.of(value1, value2);
        
        Range<Instant> logRange = Range.inclusive(null, rangeEnd.toInstant());
        
        //  expect no calls
        replay(itronRuntimeCalcService);

        Iterable<PointValueHolder> results = itronRuntimeCalcService.addBoundaryValues(relayStatuses, logRange); 
        
        assertThat("boundary values added", Iterables.size(results), equalTo(3));
        
        PointValueHolder first = Iterables.getFirst(results, null);
        
        assertThat("first value", first.getValue(), equalTo(6.0));
        assertThat("first timestamp", first.getPointDataTimeStamp().getTime(), equalTo(rangeStart.plusMinutes(22).getMillis()));
        assertThat("first id", first.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));

        PointValueHolder last = Iterables.getLast(results, null);
        
        assertThat("trailing boundary value", last.getValue(), equalTo(171.0));
        assertThat("trailing boundary timestamp", last.getPointDataTimeStamp().getTime(), equalTo(rangeEnd.getMillis()));
        assertThat("trailing boundary id", last.getId(), equalTo(lcr6600s_relay1_relayState.getPointID()));
    }
    
    @Test
    public void test_calculateRuntimes() {

        ReflectionTestUtils.setField(itronRuntimeCalcService, "runtimeCalcService", new RuntimeCalcServiceImpl());

        var mockPaoDao = createNiceMock(PaoDao.class);
        
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.LCR6600S))
            .andReturn(Lists.newArrayList(lcr6600s));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.LCR6601S))
            .andReturn(Collections.emptyList());
        replay(mockPaoDao);
        
        ReflectionTestUtils.setField(itronRuntimeCalcService, "paoDao", mockPaoDao);
        
        var mockAttributeService = createStrictMock(AttributeService.class);

        expect(mockAttributeService.findPaoMultiPointIdentifiersForAttributes(List.of(lcr6600s), ItronRelayDataLogs.getDataLogAttributes()))
            .andReturn(List.of(new PaoMultiPointIdentifier(lcr6600s, List.of(new PointIdentifier(lcr6600s_relay1_runtimeLog_15m.getPointTypeEnum(),
                                                                                                 lcr6600s_relay1_runtimeLog_15m.getPointOffset())))));
        expect(mockAttributeService.findPaoMultiPointIdentifiersForAttributes(List.of(lcr6600s), ItronRelayDataLogs.getRelayStatusAttributes()))
            .andReturn(List.of(new PaoMultiPointIdentifier(lcr6600s, List.of(new PointIdentifier(lcr6600s_relay1_relayState.getPointTypeEnum(),
                                                                                                 lcr6600s_relay1_relayState.getPointOffset())))));
        replay(mockAttributeService);
        
        ReflectionTestUtils.setField(itronRuntimeCalcService, "attributeService", mockAttributeService);

        //  Per device:
        //  getRecentData()
        var mockPointDao = createNiceMock(PointDao.class);
        expect(mockPointDao.getLitePointsByPaObjectId(lcr6600s.getPaoIdentifier().getPaoId()))
            .andReturn(lcr6600s_points);
        replay(mockPointDao);

        ReflectionTestUtils.setField(itronRuntimeCalcService, "pointDao", mockPointDao);
        
        Set<Integer> lcr6600s_pointIds = Sets.newHashSet(Lists.transform(lcr6600s_points, LitePoint::getPointID));
        
        DateTime radioLinkQuality = DateTime.parse("2019-03-20T12:06:17.922Z");
        DateTime recentRelayState = DateTime.parse("2019-03-20T11:13:27.000Z");
        DateTime recentDataLog = recentRelayState.minus(Duration.standardHours(3));
        DateTime interimRelayState = recentRelayState.minus(Duration.standardHours(2));
        DateTime previousRelayState = recentDataLog.minus(Duration.standardMinutes(17));
        
        var mockAsyncDynamicDataSource = createStrictMock(AsyncDynamicDataSource.class);
        EasyMock.<Set<? extends PointValueQualityHolder>>expect(mockAsyncDynamicDataSource.getPointDataOnce(lcr6600s_pointIds))
            .andReturn(Sets.newHashSet(buildPvqh(lcr6600s_radioLinkQuality, radioLinkQuality, 100.0),
                                       buildPvqh(lcr6600s_relay1_relayState, recentRelayState, 0.0),
                                       buildPvqh(lcr6600s_relay1_runtimeLog_15m, recentDataLog, 1.0)));
        
        mockAsyncDynamicDataSource.putValues(eq(List.of(
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T08:15:00.000Z"), 0.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T08:30:00.000Z"), 0.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T08:45:00.000Z"), 0.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T09:00:00.000Z"), 0.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T09:15:00.000Z"), 1.55),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T09:30:00.000Z"), 15.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T09:45:00.000Z"), 15.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T10:00:00.000Z"), 15.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T10:15:00.000Z"), 15.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T10:30:00.000Z"), 15.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T10:45:00.000Z"), 15.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T11:00:00.000Z"), 15.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T11:15:00.000Z"), 13.45),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T11:30:00.000Z"), 0.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T11:45:00.000Z"), 0.0),
                buildPointData(lcr6600s_relay1_runtimeLog_15m, DateTime.parse("2019-03-20T12:00:00.000Z"), 0.0))));
        
        
        replay(mockAsyncDynamicDataSource);
        
        ReflectionTestUtils.setField(itronRuntimeCalcService, "asyncDynamicDataSource", mockAsyncDynamicDataSource);

        //  per relay status
        var mockRawPointHistoryDao = createStrictMock(RawPointHistoryDao.class);
        expect(mockRawPointHistoryDao.getPointData(Sets.newHashSet(lcr6600s_relay1_relayState.getPointID()),
                                                   Range.inclusive(recentDataLog.toInstant(), TimeUtil.getStartOfHour(radioLinkQuality).toInstant()), 
                                                   false,
                                                   Order.FORWARD))
            .andReturn(List.of(buildPvqh(lcr6600s_relay1_relayState, interimRelayState, 3.0),
                               buildPvqh(lcr6600s_relay1_relayState, recentRelayState, 0.0)));
        expect(mockRawPointHistoryDao.getLimitedPointData(lcr6600s_relay1_relayState.getPointID(),
                                                          new Range<>(null, true, interimRelayState.toInstant(), false), 
                                                          false,
                                                          Order.REVERSE,
                                                          1))
            .andReturn(List.of(buildPvqh(lcr6600s_relay1_relayState, previousRelayState, 0.0)));
        replay(mockRawPointHistoryDao);

        ReflectionTestUtils.setField(itronRuntimeCalcService, "rphDao", mockRawPointHistoryDao);

        var mockPaoDefinitionDao = createStrictMock(PaoDefinitionDao.class);
        expect(mockPaoDefinitionDao.findAttributeLookup(eq(lcr6600s.getPaoType()), anyObject(BuiltInAttribute.class)))
            .andReturn(Optional.empty());
        expect(mockPaoDefinitionDao.findAttributeLookup(lcr6600s.getPaoType(), BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG_15_MIN))
            .andReturn(Optional.of(new AttributeDefinition(BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG_15_MIN, 
                                                           new PointTemplate("Relay 1 Run Time Log 15 Minutes",
                                                                             PointType.Analog,
                                                                             12, 1, UnitOfMeasure.MINUTES.getId(), -1, 0),
                                                           null)));
        expect(mockPaoDefinitionDao.findAttributeLookup(eq(lcr6600s.getPaoType()), anyObject(BuiltInAttribute.class)))
            .andReturn(Optional.empty())
            .times(2);
        expect(mockPaoDefinitionDao.findAttributeLookup(lcr6600s.getPaoType(), BuiltInAttribute.RELAY_1_RELAY_STATE))
            .andReturn(Optional.of(new AttributeDefinition(BuiltInAttribute.RELAY_1_RELAY_STATE, 
                                                           new PointTemplate("Relay 1 Relay State",
                                                                             PointType.Status,
                                                                             3, 1, -1, -1, 0),
                                                           null)));
        expect(mockPaoDefinitionDao.findAttributeLookup(eq(lcr6600s.getPaoType()), anyObject(BuiltInAttribute.class)))
            .andReturn(Optional.empty())
            .times(15);
        expect(mockPaoDefinitionDao.findAttributeLookup(eq(lcr6600s.getPaoType()), anyObject(BuiltInAttribute.class)))
            .andReturn(Optional.empty())
            .times(20);

        replay(mockPaoDefinitionDao);

        ReflectionTestUtils.setField(itronRuntimeCalcService, "paoDefinitionDao", mockPaoDefinitionDao);

        var newTimes = new AssetAvailabilityPointDataTimes(lcr6600sId);
        newTimes.setRelayRuntime(1, date1.toInstant());
        
        var mockDynamicLcrCommunicationsDao = createNiceMock(DynamicLcrCommunicationsDao.class);
        /* expect */ mockDynamicLcrCommunicationsDao.insertData(newTimes);
        
        replay(mockDynamicLcrCommunicationsDao);

        ReflectionTestUtils.setField(itronRuntimeCalcService, "dynamicLcrCommunicationsDao", mockDynamicLcrCommunicationsDao);
        
        itronRuntimeCalcService.calculateDataLogs();
    }
    
    @Test
    public void test_calculateRuntimes_uninitialized() {

        ReflectionTestUtils.setField(itronRuntimeCalcService, "runtimeCalcService", new RuntimeCalcServiceImpl());

        var mockPaoDao = createNiceMock(PaoDao.class);
        
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.LCR6600S))
            .andReturn(Lists.newArrayList(lcr6600s));
        expect(mockPaoDao.getLiteYukonPAObjectByType(PaoType.LCR6601S))
            .andReturn(Collections.emptyList());
        replay(mockPaoDao);
        
        ReflectionTestUtils.setField(itronRuntimeCalcService, "paoDao", mockPaoDao);
        
        var mockAttributeService = createStrictMock(AttributeService.class);

        expect(mockAttributeService.findPaoMultiPointIdentifiersForAttributes(List.of(lcr6600s), ItronRelayDataLogs.getDataLogAttributes()))
            .andReturn(List.of(new PaoMultiPointIdentifier(lcr6600s, List.of(new PointIdentifier(lcr6600s_relay1_runtimeLog_15m.getPointTypeEnum(),
                                                                                                 lcr6600s_relay1_runtimeLog_15m.getPointOffset()),
                                                                             new PointIdentifier(lcr6600s_relay1_shedtimeLog_5m.getPointTypeEnum(),
                                                                                                 lcr6600s_relay1_shedtimeLog_5m.getPointOffset())
                                                                             ))));
        expect(mockAttributeService.findPaoMultiPointIdentifiersForAttributes(List.of(lcr6600s), ItronRelayDataLogs.getRelayStatusAttributes()))
            .andReturn(List.of(new PaoMultiPointIdentifier(lcr6600s, List.of(new PointIdentifier(lcr6600s_relay1_relayState.getPointTypeEnum(),
                                                                                                 lcr6600s_relay1_relayState.getPointOffset()),
                                                                             new PointIdentifier(lcr6600s_relay1_shedStatus.getPointTypeEnum(),
                                                                                                 lcr6600s_relay1_shedStatus.getPointOffset())))
                               ));
        replay(mockAttributeService);
        
        ReflectionTestUtils.setField(itronRuntimeCalcService, "attributeService", mockAttributeService);

        //  Per device:
        //  getRecentData()
        var mockPointDao = createNiceMock(PointDao.class);
        expect(mockPointDao.getLitePointsByPaObjectId(lcr6600s.getPaoIdentifier().getPaoId()))
            .andReturn(lcr6600s_points);
        replay(mockPointDao);

        ReflectionTestUtils.setField(itronRuntimeCalcService, "pointDao", mockPointDao);
        
        Set<Integer> lcr6600s_pointIds = Sets.newHashSet(Lists.transform(lcr6600s_points, LitePoint::getPointID));
        
        DateTime recentRelayState = DateTime.parse("2019-03-20T11:13:27.000Z");
        DateTime uninitialized = DateTime.parse("2010-01-01T18:00:00.000Z");
        
        var mockAsyncDynamicDataSource = createStrictMock(AsyncDynamicDataSource.class);
        EasyMock.<Set<? extends PointValueQualityHolder>>expect(mockAsyncDynamicDataSource.getPointDataOnce(lcr6600s_pointIds))
            .andReturn(Sets.newHashSet(buildPvqh(lcr6600s_relay1_relayState, recentRelayState, 0.0),
                                       buildPvqh(lcr6600s_relay1_shedStatus, uninitialized, 0.0, PointQuality.Uninitialized),
                                       buildPvqh(lcr6600s_relay1_runtimeLog_15m, uninitialized, 0.0, PointQuality.Uninitialized),
                                       buildPvqh(lcr6600s_relay1_shedtimeLog_5m, uninitialized, 0.0, PointQuality.Uninitialized)));
        
        mockAsyncDynamicDataSource.putValues(eq(Collections.emptyList()));
        
        
        replay(mockAsyncDynamicDataSource);
        
        ReflectionTestUtils.setField(itronRuntimeCalcService, "asyncDynamicDataSource", mockAsyncDynamicDataSource);

        //  per relay status
        var mockRawPointHistoryDao = createStrictMock(RawPointHistoryDao.class);
        expect(mockRawPointHistoryDao.getPointData(Sets.newHashSet(lcr6600s_relay1_relayState.getPointID(),
                                                                   lcr6600s_relay1_shedStatus.getPointID()),
                                                   Range.inclusive(null, TimeUtil.getStartOfHour(recentRelayState).toInstant()), 
                                                   false,
                                                   Order.FORWARD))
            .andReturn(Collections.emptyList());

        replay(mockRawPointHistoryDao);

        ReflectionTestUtils.setField(itronRuntimeCalcService, "rphDao", mockRawPointHistoryDao);

        var mockPaoDefinitionDao = createStrictMock(PaoDefinitionDao.class);
        expect(mockPaoDefinitionDao.findAttributeLookup(eq(lcr6600s.getPaoType()), anyObject(BuiltInAttribute.class)))
            .andReturn(Optional.empty());
        expect(mockPaoDefinitionDao.findAttributeLookup(lcr6600s.getPaoType(), BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG_15_MIN))
            .andReturn(Optional.of(new AttributeDefinition(BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG_15_MIN, 
                                                           new PointTemplate("Relay 1 Run Time Log 15 Minutes",
                                                                             PointType.Analog,
                                                                             12, 1, UnitOfMeasure.MINUTES.getId(), -1, 0),
                                                           null)));
        expect(mockPaoDefinitionDao.findAttributeLookup(eq(lcr6600s.getPaoType()), anyObject(BuiltInAttribute.class)))
            .andReturn(Optional.empty())
            .times(2);
        expect(mockPaoDefinitionDao.findAttributeLookup(lcr6600s.getPaoType(), BuiltInAttribute.RELAY_1_RELAY_STATE))
            .andReturn(Optional.of(new AttributeDefinition(BuiltInAttribute.RELAY_1_RELAY_STATE, 
                                                           new PointTemplate("Relay 1 Relay State",
                                                                             PointType.Status,
                                                                             3, 1, -1, -1, 0),
                                                           null)));
        expect(mockPaoDefinitionDao.findAttributeLookup(eq(lcr6600s.getPaoType()), anyObject(BuiltInAttribute.class)))
            .andReturn(Optional.empty())
            .times(15);
        expect(mockPaoDefinitionDao.findAttributeLookup(eq(lcr6600s.getPaoType()), anyObject(BuiltInAttribute.class)))
            .andReturn(Optional.empty())
            .times(20);

        replay(mockPaoDefinitionDao);

        ReflectionTestUtils.setField(itronRuntimeCalcService, "paoDefinitionDao", mockPaoDefinitionDao);

        var newTimes = new AssetAvailabilityPointDataTimes(lcr6600sId);
        newTimes.setRelayRuntime(1, date1.toInstant());
        
        var mockDynamicLcrCommunicationsDao = createNiceMock(DynamicLcrCommunicationsDao.class);
        /* expect */ mockDynamicLcrCommunicationsDao.insertData(newTimes);
        
        replay(mockDynamicLcrCommunicationsDao);

        ReflectionTestUtils.setField(itronRuntimeCalcService, "dynamicLcrCommunicationsDao", mockDynamicLcrCommunicationsDao);
        
        itronRuntimeCalcService.calculateDataLogs();
    }
    
    private PointData buildPointData(LitePoint point, DateTime timestamp, Double value) {
        var pd = new PointData();
        pd.setId(point.getPointID());
        pd.setType(point.getPointType());
        pd.setTime(timestamp.toDate());
        pd.setValue(value);
        pd.setPointQuality(PointQuality.Normal);
        pd.setTagsPointMustArchive(true);
        return pd;
    }
    
    private PointValueQualityHolder buildPvqh(LitePoint point, DateTime timestamp, Double value) {
        return buildPvqh(point, timestamp, value, PointQuality.Normal);
    }

    private PointValueQualityHolder buildPvqh(LitePoint point, DateTime timestamp, Double value, PointQuality quality) {
        return PointValueBuilder.create()
                                .withPointId(point.getPointID())
                                .withPointQuality(quality)
                                .withType(point.getPointTypeEnum())
                                .withTimeStamp(timestamp.toDate())
                                .withValue(value)
                                .build();
    }
}
