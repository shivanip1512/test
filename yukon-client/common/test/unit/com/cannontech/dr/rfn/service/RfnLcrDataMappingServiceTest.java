package com.cannontech.dr.rfn.service;

import static com.cannontech.dr.rfn.model.RfnLcrPointDataMap.RELAY_1_LOAD_SIZE;
import static com.cannontech.dr.rfn.model.RfnLcrPointDataMap.RELAY_1_REMAINING_CONTROL;

import java.util.List;

import junit.framework.Assert;
import org.joda.time.Instant;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.MockAttributeServiceImpl;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.rfn.service.impl.RfnLcrExiDataMappingServiceImpl;
import com.cannontech.message.dispatch.message.PointData;

public class RfnLcrDataMappingServiceTest {

    private AttributeService attributeService = new MockAttributeServiceImpl();
    private RfnLcrExiDataMappingServiceImpl dataMappingServiceImpl = new RfnLcrExiDataMappingServiceImpl();
    {
        ReflectionTestUtils.setField(dataMappingServiceImpl, "attributeService", attributeService);

    }
    
    private static final String realReading = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DRReport utc=\"1375219337\" " +// This is important
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<UniqueID>1077</UniqueID><HostDeviceID revision=\"1280\">1077</HostDeviceID><OtherDeviceIDS/>" +
                "<ExtendedAddresssing><SPID>2000</SPID><Geo>1</Geo><Feeder>1</Feeder><Zip>1</Zip><UDA>65000</UDA><Required>128</Required></ExtendedAddresssing>" +
                "<Info><Flags>0</Flags><ReportingInterval>0</ReportingInterval><RecordingInterval>60</RecordingInterval><TotalLUFEvents>0</TotalLUFEvents>" +
                "<TotalLUVEvents>0</TotalLUVEvents><BlinkCount>4</BlinkCount></Info>" +
                "<Relays><Relay id=\"0\"><Flags>0</Flags><Program>11</Program><Splinter>1</Splinter><RemainingControlTime>12965</RemainingControlTime><KwRating>132</KwRating><AmpType>65</AmpType>" +
                "<IntervalData startTime=\"1375218000\">" + // This is important
                "<Interval>7740</Interval><Interval>19516</Interval><Interval>19457</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19461</Interval><Interval>19516</Interval><Interval>19516</Interval><Interval>19516</Interval><Interval>19516</Interval></IntervalData></Relay></Relays><ControlEvents><ControlEvent><Flags>0</Flags><ID>828367597</ID><Type>0</Type><RandomDelayTime>0</RandomDelayTime><Start>0</Start><Stop>14400</Stop></ControlEvent></ControlEvents><LUVEvents/><LUFEvents/></DRReport>";
    
    private static final String timeModifiedReading = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DRReport utc=\"1375219337\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<UniqueID>1077</UniqueID><HostDeviceID revision=\"1280\">1077</HostDeviceID><OtherDeviceIDS/>" +
            "<ExtendedAddresssing><SPID>2000</SPID><Geo>1</Geo><Feeder>1</Feeder><Zip>1</Zip><UDA>65000</UDA><Required>128</Required></ExtendedAddresssing>" +
            "<Info><Flags>0</Flags><ReportingInterval>0</ReportingInterval><RecordingInterval>60</RecordingInterval><TotalLUFEvents>0</TotalLUFEvents>" +
            "<TotalLUVEvents>0</TotalLUVEvents><BlinkCount>4</BlinkCount></Info>" +
            "<Relays><Relay id=\"0\"><Flags>0</Flags><Program>11</Program><Splinter>1</Splinter><RemainingControlTime>12965</RemainingControlTime><KwRating>132</KwRating><AmpType>65</AmpType>" +
            "<IntervalData startTime=\"1375215737\">" + // Exactly 1 hour
            "<Interval>7740</Interval><Interval>19516</Interval><Interval>19457</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19456</Interval><Interval>19461</Interval><Interval>19516</Interval><Interval>19516</Interval><Interval>19516</Interval><Interval>19516</Interval></IntervalData></Relay></Relays><ControlEvents><ControlEvent><Flags>0</Flags><ID>828367597</ID><Type>0</Type><RandomDelayTime>0</RandomDelayTime><Start>0</Start><Stop>14400</Stop></ControlEvent></ControlEvents><LUVEvents/><LUFEvents/></DRReport>";
    
    /* This test is to test the fix to YUK-12413. The method being tested should return no data if there is less than
     * one interval of time represented in the message.
     * 
     * The XML here is taken from an actual device in the error case.
     * Note that the second check; the "success" version in done in a very bad way and is here only to verify the 
     * execution path is in fact different if the time window is 1h or greater.
     */
    @Test
    public void testMapPointIntervalData() {
        SimpleXPathTemplate data = new SimpleXPathTemplate();
        data.setContext(realReading);
        
        Long timeInSec = data.evaluateAsLong("/DRReport/@utc");
        Instant instantOfReading = new Instant(timeInSec * 1000);
        
        PaoIdentifier identifier = new PaoIdentifier(1, PaoType.LCR6200_RFN);
        
        AssetAvailabilityPointDataTimes assetAvailabilityTimes = new AssetAvailabilityPointDataTimes(identifier.getPaoId());
        assetAvailabilityTimes.setLastCommunicationTime(instantOfReading);

        RfnDevice device = new RfnDevice("serialNumber", identifier, new RfnIdentifier("serialNumber", "sensorManufacturer", "sensorModel") );
        List<PointData> mapIntervalData = ReflectionTestUtils.invokeMethod(dataMappingServiceImpl, "mapIntervalData", data, device, assetAvailabilityTimes);
        
        Assert.assertEquals(mapIntervalData.size(), 0);
        
        // Change only the start time so this version has at least 1 valid value. Otherwise identical to above.
        data.setContext(timeModifiedReading);

        try {
            mapIntervalData = ReflectionTestUtils.invokeMethod(dataMappingServiceImpl, "mapIntervalData", data, device);
            Assert.fail();
        } catch(Exception e) { /* This throws as it generates 1 valid value and I did not do the work to handle the point being generated.*/ }

    }
    
    @Test
    public void testMultiplierResult() {
        SimpleXPathTemplate data = new SimpleXPathTemplate();
        data.setContext(realReading);
        
        // Test that .001 multiplier is working; <KwRating>132</KwRating>
        Assert.assertEquals((Double)ReflectionTestUtils.invokeMethod(dataMappingServiceImpl,"evaluateArchiveReadValue", data, RELAY_1_LOAD_SIZE), .132, .000001);
        
        // Test that having no multiplier is working; <RemainingControlTime>12965</RemainingControlTime>
        Assert.assertEquals((Double)ReflectionTestUtils.invokeMethod(dataMappingServiceImpl,"evaluateArchiveReadValue", data, RELAY_1_REMAINING_CONTROL), 12965, .000001);
    }

}
