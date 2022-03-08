package com.cannontech.services.calculated;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.easymock.IAnswer;
import org.joda.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.cannontech.common.util.xml.SimpleXPathTemplate;
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

    @BeforeEach
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
        assertEquals(4, messagesToSend.size());
    }

    @Test
    public void before_DSTRollBack_InOverlapTime__1_00() {

        Instant instant = new Instant(1478412000000l); // Overlap --> true, DST -> true, time->Sun Nov 06 01:00:00 CDT 2016
                                                     
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void before_DSTRollBack_InOverlapTime_1_01() {

        Instant instant = new Instant(1478412061476l); // Overlap --> true, DST -> true, time->Sun Nov 06 01:01:01 CDT 2016
                                                       
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void before_DSTRollBack_InOverlapTime_1_59_59() {
        Instant instant = new Instant(1478415599999l); // Overlap --> true, DST ->true, time -> Sun Nov 06 01:59:59 CDT 2016
                                                      
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void after_DSTRollBack_InOverlapTime_1_00() {
        Instant instant = new Instant(1478415600000l); // Overlap --> true, DST ->false, time -> Sun Nov 06 01:00:00 CST 2016 (CDT to CST)
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(0, messagesToSend.size());
    }
    

    @Test
    public void after_DSTRollBack_InOverlapTime_1_01() {
        Instant instant = new Instant(1478415662950l); // Overlap --> true, DST -> false, time->Sun Nov 06 01:01:02 CST 2016
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(0, messagesToSend.size());
    }

    @Test
    public void after_DSTRollBack_InOverlapTime_1_59_59() {
        Instant instant = new Instant(1478419199999l); // Overlap --> true, DST -> false, time->Sun Nov 06 01:59:59 CST 2016
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void after_OverlapTime_2_00() {
        Instant instant = new Instant(1478419200000l); // Overlap --> false, DST -> false, time->Sun Nov 06 02:00:00 CST 2016
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(0, messagesToSend.size());
    }
    
    @Test
    public void after_OverlapTime_2_01() {
        Instant instant = new Instant(1478419268633l); // Overlap --> false, DST -> false, time->Sun Nov 06 02:01:08 CST 2016
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(4, messagesToSend.size());
    }
    
    @Test
    public void spring_dst_1_00() {
        Instant instant = new Instant(1489302000000l); // Overlap --> false, DST -> false, time->Sun March 12 01:00:00 CST 2017
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(4, messagesToSend.size());
    }
    
    @Test
    public void spring_dst_1_59() {
        Instant instant = new Instant(1489305599999l); // Overlap --> false, DST -> false, time->Sun March 12 01:59:59 CST 2017
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(4, messagesToSend.size());
    }
    
    @Test
    public void spring_dst_3_00() {
        Instant instant = new Instant(1489305600000l); // Overlap --> false, DST -> false, time->Sun March 12 03:00:00 CDT 2017 (forward 1 hour)
        buildRequestParameters(instant);
        calculator.calculate(recentReadings, calculationData, messagesToSend);
        assertEquals(4, messagesToSend.size());
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

    private void buildRfMeterRequestParameters(Instant instant, double pointValue, PaoPointIdentifier paoPointId) {

        final PointValueQualityHolder pointValueQualityHolder =
            PointValueBuilder.create().withPointId(12841773).withType(PointType.Analog).withPointQuality(
                PointQuality.Normal).withValue(pointValue).withTimeStamp(instant.toDate()).build();
        calculationData =
            CalculationData.of(PaoPointValue.of(paoPointId, pointValueQualityHolder), 86400);

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
    
    /**
     * Helper method to build mocks and calculate for the specified dates/values, then assert that the correct number 
     * of messages is generated.
     */
    public void checkNumberOfMessages(int numMessages, PaoPointIdentifier paoPointId, 
            LinkedHashMap<Instant, Integer> calculationDateValues) {
        
        for (var dateValue : calculationDateValues.entrySet()) {
            buildRfMeterRequestParameters(dateValue.getKey(), dateValue.getValue(), paoPointId);
            calculator.calculate(recentReadings, calculationData, messagesToSend);
        }

        assertEquals(numMessages, messagesToSend.size());
    }
    
    @Test
    public void validWaterReadings000() {
        
        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 0); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 0); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 0); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(12, RFW_PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void validElectricReadings000() {
        
        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 0); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 0); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 0); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(12, PAO_POINT_IDENTIFIER, calculationDateValues);
    }

    @Test
    public void invalidWaterReadings100() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 1); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 0); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 0); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(8, RFW_PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void invalidElectricReadings100() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 1); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 0); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 0); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(8, PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void invalidWaterReadings010() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 0); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 1); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 0); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(8, RFW_PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void invalidElectricReadings010() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 0); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 1); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 0); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(8, PAO_POINT_IDENTIFIER, calculationDateValues);
    }

    @Test
    public void invalidWaterReadings110() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 1); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 1); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 0); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(8, RFW_PAO_POINT_IDENTIFIER, calculationDateValues);
    }

    @Test
    public void invalidElectricReadings110() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 1); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 1); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 0); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(8, PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void validWaterReadings001() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 0); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 0); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 1); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(12, RFW_PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void validElectricReadings001() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 0); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 0); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 1); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(12, PAO_POINT_IDENTIFIER, calculationDateValues);
    }

    @Test
    public void invalidWaterReadings101() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 1); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 0); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 1); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(8, RFW_PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void invalidElectricReadings101() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 1); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 0); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 1); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(8, PAO_POINT_IDENTIFIER, calculationDateValues);
    }

    @Test
    public void validWaterReadings011() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 0); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 1); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 1); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(12, RFW_PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void validElectricReadings011() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 0); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 1); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 1); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(12, PAO_POINT_IDENTIFIER, calculationDateValues);
    }

    @Test
    public void validWaterReadings111() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 1); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 1); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 1); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(12, RFW_PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void validElectricReadings111() {

        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472102587787l), 1); // Date -2016-08-25T05:23:07.787Z
        calculationDateValues.put(new Instant(1472188987787l), 1); // Date -2016-08-26T05:23:07.787Z
        calculationDateValues.put(new Instant(1472275387787l), 1); // Date -2016-08-27T05:23:07.787Z
        
        checkNumberOfMessages(12, PAO_POINT_IDENTIFIER, calculationDateValues);
    }

    @Test public void testNegativekWhReadings() {
        calculator.setBasedOn(BuiltInAttribute.NET_KWH);
        calculator.setPerInterval(BuiltInAttribute.NET_KWH_PER_INTERVAL);
        calculator.setLoadProfile(BuiltInAttribute.NET_KW_LOAD_PROFILE);
        
        LinkedHashMap<Instant, Integer> calculationDateValues = new LinkedHashMap<>();
        calculationDateValues.put(new Instant(1472126400000l), 0); // Date -2016-08-25T12:00:00.000Z
        calculationDateValues.put(new Instant(1472126700000l), 1); // Date -2016-08-25T12:05:00.000Z
        calculationDateValues.put(new Instant(1472127000000l), 0); // Date -2016-08-25T12:10:00.000Z
        
        checkNumberOfMessages(12, PAO_POINT_IDENTIFIER, calculationDateValues);
    }
    
    @Test
    public void validCalculableAttributeUpToDate() throws IOException {
        InputStream in = this.getClass().getResourceAsStream("/com/cannontech/services/rfn/rfnMeteringContext.xml");

        String xmlString = IOUtils.toString(in, StandardCharsets.UTF_8.name());

        Properties namespaces = new Properties();
        namespaces.put("b", "http://www.springframework.org/schema/beans");
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(xmlString);
        template.setNamespaces(namespaces);

        List<String> applicationContextAttributes = template.evaluateAsStringList("//b:property[@name='basedOn']/@value");
        Set<String> calculableAttributes = BuiltInAttribute.getCalculableAttributes()
                                                           .stream()
                                                           .map(attr -> attr.name())
                                                           .collect(Collectors.toSet());
        List<String> attributesNotCalculable = applicationContextAttributes.stream()
                                              .filter(attr -> !calculableAttributes.contains(attr))
                                              .collect(Collectors.toList());
        assertTrue(attributesNotCalculable.isEmpty());
    }
}
