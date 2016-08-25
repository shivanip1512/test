package com.cannontech.services.calculated;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.easymock.IAnswer;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointValue;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

/**
 * In calculate method, YukonUserContext’s getTimeZone is used and it will return the default system
 * timezone (JVM timezone) so for the testing purpose, I changed the system timezone to CDT /CST and tested
 * all scenario related to it.
 * 
 */
public class PerIntervalAndLoadProfileCalculatorTest {
    private final static PaoIdentifier RFW_PAO_IDENTIFIER = new PaoIdentifier(1, PaoType.RFWMETER);
    List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
    private final static PaoIdentifier PAO_IDENTIFIER = new PaoIdentifier(1, PaoType.RFN410CL);
    private final static PointIdentifier POINT_IDENTIFIER = new PointIdentifier(PointType.PulseAccumulator, 1);
    private final static PaoPointIdentifier PAO_POINT_IDENTIFIER = new PaoPointIdentifier(PAO_IDENTIFIER,
        POINT_IDENTIFIER);
    private final static PaoPointIdentifier RFW_PAO_POINT_IDENTIFIER = new PaoPointIdentifier(RFW_PAO_IDENTIFIER,
        POINT_IDENTIFIER);
    private PerIntervalAndLoadProfileCalculator calculator;
    private CalculationData calculationData;
    private Cache<CacheKey, CacheValue> recentReadings;
    private AttributeService attributeService;
    private RawPointHistoryDao rphDao;

    @Before
    public void setUp() throws Exception {
        calculator = new PerIntervalAndLoadProfileCalculator();

        calculator.setLoadProfile(BuiltInAttribute.DELIVERED_KW_LOAD_PROFILE);
        calculator.setPerInterval(BuiltInAttribute.DELIVERED_KWH_PER_INTERVAL);
        recentReadings = CacheBuilder.newBuilder().expireAfterWrite(72, TimeUnit.HOURS).build();

        attributeService = createNiceMock(AttributeService.class);
        attributeService.getPointForAttribute(anyObject(YukonPao.class), anyObject(Attribute.class));
        expectLastCall().andAnswer(new IAnswer<LitePoint>() {
            @Override
            public LitePoint answer() throws Throwable {
                return new LitePoint(200);

            }
        }).anyTimes();
        ReflectionTestUtils.setField(calculator, "attributeService", attributeService);
        replay(attributeService);
    }

    @Test
    public void before_OverlapTime_00_59_59() {
        Instant instant = new Instant(1478411999999l); // Overlap --> false, DST --> true , time-> Sun Nov 06 00:59:59 CDT 2016
                                                 
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(4, messagesToSend.size());
    }

    @Test
    public void before_DSTRollBack_InOverlapTime__1_00() {

        Instant instant = new Instant(1478412000000l); // Overlap --> true, DST -> true, time->Sun Nov 06 01:00:00 CDT 2016
                                                     
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void before_DSTRollBack_InOverlapTime_1_01() {

        Instant instant = new Instant(1478412061476l); // Overlap --> true, DST -> true, time->Sun Nov 06 01:01:01 CDT 2016
                                                       
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void before_DSTRollBack_InOverlapTime_1_59_59() {
        Instant instant = new Instant(1478415599999l); // Overlap --> true, DST ->true, time -> Sun Nov 06 01:59:59 CDT 2016
                                                      
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void after_DSTRollBack_InOverlapTime_1_00() {
        Instant instant = new Instant(1478415600000l); // Overlap --> true, DST ->false, time -> Sun Nov 06 01:00:00 CST 2016 (CDT to CST)
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(0, messagesToSend.size());
    }
    

    @Test
    public void after_DSTRollBack_InOverlapTime_1_01() {
        Instant instant = new Instant(1478415662950l); // Overlap --> true, DST -> false, time->Sun Nov 06 01:01:02 CST 2016
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(0, messagesToSend.size());
    }

    @Test
    public void after_DSTRollBack_InOverlapTime_1_59_59() {
        Instant instant = new Instant(1478419199999l); // Overlap --> true, DST -> false, time->Sun Nov 06 01:59:59 CST 2016
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void after_OverlapTime_2_00() {
        Instant instant = new Instant(1478419200000l); // Overlap --> false, DST -> false, time->Sun Nov 06 02:00:00 CST 2016
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void after_OverlapTime_2_01() {
        Instant instant = new Instant(1478419268633l); // Overlap --> false, DST -> false, time->Sun Nov 06 02:01:08 CST 2016
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(4, messagesToSend.size());
    }
    
    @Test
    public void spring_dst_1_00() {
        Instant instant = new Instant(1489302000000l); // Overlap --> false, DST -> false, time->Sun March 12 01:00:00 CST 2017
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(4, messagesToSend.size());
    }
    
    @Test
    public void spring_dst_1_59() {
        Instant instant = new Instant(1489305599999l); // Overlap --> false, DST -> false, time->Sun March 12 01:59:59 CST 2017
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(4, messagesToSend.size());
    }
    
    @Test
    public void spring_dst_3_00() {
        Instant instant = new Instant(1489305600000l); // Overlap --> false, DST -> false, time->Sun March 12 03:00:00 CDT 2017 (forward 1 hour)
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        Assert.assertEquals(4, messagesToSend.size());
    }
    
    private void buildRequestParameters(Instant instant) {

        final PointValueQualityHolder pointValueQualityHolder =
            PointValueBuilder.create().withPointId(200).withType(PointType.PulseAccumulator).withPointQuality(
                PointQuality.Manual).withValue(600).withTimeStamp(instant.toDate()).build();
        calculationData = CalculationData.of(PaoPointValue.of(PAO_POINT_IDENTIFIER, pointValueQualityHolder), 3600);

        CacheKey key =
            CacheKey.of(pointValueQualityHolder.getId(), pointValueQualityHolder.getPointDataTimeStamp().getTime());
        CacheValue value =
            CacheValue.of(pointValueQualityHolder.getValue(), calculationData.getInterval(), false, false);
        recentReadings.put(key, value);

        rphDao = createNiceMock(RawPointHistoryDao.class);
        rphDao.getSpecificValue(anyInt(), anyLong());
        expectLastCall().andAnswer(new IAnswer<PointValueQualityHolder>() {
            @Override
            public PointValueQualityHolder answer() throws Throwable {

                return pointValueQualityHolder;
            }
        }).anyTimes();

        ReflectionTestUtils.setField(calculator, "rphDao", rphDao);

        replay(rphDao);
    }

    private void buildRfwMeterRequestParameters(Instant instant, double pointValue) {

        final PointValueQualityHolder pointValueQualityHolder =
            PointValueBuilder.create().withPointId(12841773).withType(PointType.Analog).withPointQuality(
                PointQuality.Normal).withValue(pointValue).withTimeStamp(instant.toDate()).build();
        calculationData =
            CalculationData.of(PaoPointValue.of(RFW_PAO_POINT_IDENTIFIER, pointValueQualityHolder), 86400);

        CacheKey key =
            CacheKey.of(pointValueQualityHolder.getId(), pointValueQualityHolder.getPointDataTimeStamp().getTime());
        CacheValue value =
            CacheValue.of(pointValueQualityHolder.getValue(), calculationData.getInterval(), false, false);
        recentReadings.put(key, value);

        rphDao = createNiceMock(RawPointHistoryDao.class);
        rphDao.getSpecificValue(anyInt(), anyLong());
        expectLastCall().andAnswer(new IAnswer<PointValueQualityHolder>() {
            @Override
            public PointValueQualityHolder answer() throws Throwable {

                return pointValueQualityHolder;
            }
        }).anyTimes();

        ReflectionTestUtils.setField(calculator, "rphDao", rphDao);

        replay(rphDao);
    }

    @Test
    public void Valid_Readings_0_0_0() {

        Instant instant = new Instant(1472102587787l); // Date -2016-08-25T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472188987787l); // Date -2016-08-26T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472275387787l); // Date -2016-08-27T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        Assert.assertEquals(12, messagesToSend.size());
    }

    @Test
    public void Invalid_Readings_1_0_0() {

        Instant instant = new Instant(1472102587787l); // Date -2016-08-25T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472188987787l); // Date -2016-08-26T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472275387787l); // Date -2016-08-27T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        Assert.assertEquals(8, messagesToSend.size());
    }

    @Test
    public void Invalid_Readings_0_1_0() { // tested

        Instant instant = new Instant(1472102587787l); // Date -2016-08-25T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472188987787l); // Date -2016-08-26T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472275387787l); // Date -2016-08-27T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        System.out.println(messagesToSend.size());
        Assert.assertEquals(8, messagesToSend.size());
    }

    @Test
    public void Invalid_Readings_1_1_0() {

        Instant instant = new Instant(1472102587787l); // Date -2016-08-25T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472188987787l); // Date -2016-08-26T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472275387787l); // Date -2016-08-27T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        Assert.assertEquals(8, messagesToSend.size());
    }

    @Test
    public void Valid_Readings_0_0_1() {

        Instant instant = new Instant(1472102587787l); // Date -2016-08-25T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472188987787l); // Date -2016-08-26T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472275387787l); // Date -2016-08-27T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        Assert.assertEquals(12, messagesToSend.size());
    }

    @Test
    public void Invalid_Readings_1_0_1() {

        Instant instant = new Instant(1472102587787l); // Date -2016-08-25T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472188987787l); // Date -2016-08-26T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472275387787l); // Date -2016-08-27T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        Assert.assertEquals(8, messagesToSend.size());
    }

    @Test
    public void Invalid_Readings_0_1_1() {

        Instant instant = new Instant(1472102587787l); // Date -2016-08-25T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 0);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472188987787l); // Date -2016-08-26T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472275387787l); // Date -2016-08-27T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        Assert.assertEquals(12, messagesToSend.size());
    }

    @Test
    public void Valid_Readings_1_1_1() {

        Instant instant = new Instant(1472102587787l); // Date -2016-08-25T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472188987787l); // Date -2016-08-26T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        instant = new Instant(1472275387787l); // Date -2016-08-27T05:23:07.787Z
        buildRfwMeterRequestParameters(instant, 1);
        calculator.calculate(recentReadings, calculationData, messagesToSend);

        Assert.assertEquals(12, messagesToSend.size());
    }

}
