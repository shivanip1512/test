package com.cannontech.dr.rfn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.rfn.model.RfnLcrTlvPointDataType;
import com.cannontech.dr.rfn.service.impl.RfnLcrTlvDataMappingServiceImpl;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

public class RfnLcrTlvDataMappingServiceTest {
    private RfnLcrTlvDataMappingServiceImpl service = new RfnLcrTlvDataMappingServiceImpl();
    
    @Test
    public void test_isValidTimeOfReading_withValidDate() {
        DateTime validDate = new DateTime(2001, 1, 1, 0, 0, 1); //1 second after the start of the year
        byte[] validDateSecondsBytes = getBytes(validDate.getMillis() / 1000);
        
        ListMultimap<FieldType, byte[]> validData = LinkedListMultimap.create();
        validData.put(FieldType.UTC, validDateSecondsBytes);
        
        boolean isValid = service.isValidTimeOfReading(validData);
        assertTrue("Date after start of 2001 marked as invalid.", isValid);
    }
    
    @Test
    public void test_isValidTimeOfReading_withInvalidDate() {
        DateTime invalidDate = new DateTime(2001, 1, 1, 0, 0, 0); //the exact start of the year
        byte[] invalidDateSecondsBytes = getBytes(invalidDate.getMillis() / 1000);
        
        ListMultimap<FieldType, byte[]> invalidData = LinkedListMultimap.create();
        invalidData.put(FieldType.UTC, invalidDateSecondsBytes);
        
        boolean isValid = service.isValidTimeOfReading(invalidData);
        assertFalse("Date at start of 2001 marked as valid.", isValid);
    }
    
    @Test
    public void test_evaluateArchiveReadValue_ordinaryPoints() {
        // These points are just plain values, no processing
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.BLINK_COUNT, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.BLINK_COUNT, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_TRIGGER, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_TRIGGER, 1200);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_RESTORE, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_RESTORE, 1100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_TRIGGER_TIME, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_TRIGGER_TIME, 1000);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_RESTORE_TIME, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_RESTORE_TIME, 1000);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_TRIGGER, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_TRIGGER, 20);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_RESTORE, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_RESTORE, 17);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_TRIGGER_TIME, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_TRIGGER_TIME, 1000);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_RESTORE_TIME, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_RESTORE_TIME, 1000);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_START_RANDOM_TIME, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_START_RANDOM_TIME, 1000);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_END_RANDOM_TIME, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_END_RANDOM_TIME, 1000);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_START_RANDOM_TIME, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_START_RANDOM_TIME, 1000);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_END_RANDOM_TIME, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_END_RANDOM_TIME, 1000);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_MIN_EVENT_DURATION, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_MIN_EVENT_DURATION, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_MIN_EVENT_DURATION, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_MIN_EVENT_DURATION, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_MAX_EVENT_DURATION, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_MAX_EVENT_DURATION, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.MINIMUM_EVENT_SEPARATION, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.MINIMUM_EVENT_SEPARATION, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.TOTAL_LUF_EVENT, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.TOTAL_LUF_EVENT, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.TOTAL_LUV_EVENT, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.TOTAL_LUV_EVENT, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_EVENT_COUNT, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOV_EVENT_COUNT, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_EVENT_COUNT, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.LOF_EVENT_COUNT, 100);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.SERVICE_STATUS, 0);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.SERVICE_STATUS, 4);
        evaluateArchiveReadValue(RfnLcrTlvPointDataType.SERVICE_STATUS, 8);
    }
    
    @Test
    public void test_evaluateArchiveReadValue_pqrEnabled() {
        ListMultimap<FieldType, byte[]> data1 = ArrayListMultimap.create();
        data1.put(FieldType.POWER_QUALITY_RESPONSE_ENABLED, getBytes(0));
        Object result1 = ReflectionTestUtils.invokeMethod(service, "evaluateArchiveReadValue", data1, RfnLcrTlvPointDataType.POWER_QUALITY_RESPONSE_ENABLED);
        assertTrue("evaluateArchiveReadValue did not return a double", result1 instanceof Double);
        assertEquals(new Double(0), result1);
        
        ListMultimap<FieldType, byte[]> data2 = ArrayListMultimap.create();
        data2.put(FieldType.POWER_QUALITY_RESPONSE_ENABLED, getBytes(1));
        Object result2 = ReflectionTestUtils.invokeMethod(service, "evaluateArchiveReadValue", data2, RfnLcrTlvPointDataType.POWER_QUALITY_RESPONSE_ENABLED);
        assertTrue("evaluateArchiveReadValue did not return a double", result2 instanceof Double);
        assertEquals(new Double(1), result2);
        
        ListMultimap<FieldType, byte[]> data3 = ArrayListMultimap.create();
        data3.put(FieldType.POWER_QUALITY_RESPONSE_ENABLED, getBytes(2));
        Object result3 = ReflectionTestUtils.invokeMethod(service, "evaluateArchiveReadValue", data3, RfnLcrTlvPointDataType.POWER_QUALITY_RESPONSE_ENABLED);
        assertTrue("evaluateArchiveReadValue did not return a double", result3 instanceof Double);
        assertEquals(new Double(1), result3);
    }
    
    //TODO: test control status, relay remaining control fields
    
    private void evaluateArchiveReadValue(RfnLcrTlvPointDataType dataType, int value) {
        ListMultimap<FieldType, byte[]> data = ArrayListMultimap.create();
        data.put(dataType.getFieldType(), getBytes(value));
        Object result = ReflectionTestUtils.invokeMethod(service, "evaluateArchiveReadValue", data, dataType);
        assertTrue("evaluateArchiveReadValue did not return a double", result instanceof Double);
        assertEquals(new Double(value), result);
    }
    
    /* This tests all of the incoming addressing with and without an existing address.
     * 
     * 1. We change every one of the "geo" address levels from a fixed 9 to a new value and verify that value
     * 2. We add a new relay, and change only part of an existing relay (change splinter and not program)
     * 3. Test with no existing addressing with and without new relays - YUK-19945
     * 4. If the reported timestamp is older, nothing should change.
     */
    @Test 
    public void test_generateUpdatedAddressingFromMessage() {
        // Fixed "now" for the test with milliseconds removed for comparison with device that doesn't support ms
        Instant now;
        {
            Instant instant = Instant.now();
            now = new Instant(instant.getMillis()-instant.getMillis()%1000);
        }
        
        final int devicePaoId = 1234;
        
        ListMultimap<FieldType, byte[]> data  = ArrayListMultimap.create();
        data.put(FieldType.SPID, getBytes(1));
        data.put(FieldType.GEO_ADDRESS, getBytes(2));
        data.put(FieldType.SUBSTATION_ADDRESS, getBytes(3));
        data.put(FieldType.FEEDER_ADDRESS, getBytes(4));
        data.put(FieldType.ZIP_ADDRESS, getBytes(5));
        data.put(FieldType.UDA_ADDRESS, getBytes(6));
        data.put(FieldType.REQUIRED_ADDRESS, getBytes(7));
        data.put(FieldType.UTC, getBytes(now.getMillis()/1000)); // Seconds
        
        HashSet<ExpressComReportedAddressRelay> relays = Sets.newHashSet();
        ExpressComReportedAddressRelay newRelay = new ExpressComReportedAddressRelay(1);
        newRelay.setProgram(1);
        relays.add(newRelay);
        newRelay = new ExpressComReportedAddressRelay(2);
        newRelay.setProgram(2);
        relays.add(newRelay);
        newRelay = new ExpressComReportedAddressRelay(3);
        newRelay.setSplinter(3);
        relays.add(newRelay);
        
        ExpressComReportedAddress currentAddress = new ExpressComReportedAddress();
        currentAddress.setChangeId(2);
        currentAddress.setDeviceId(devicePaoId);
        currentAddress.setFeeder(9);
        currentAddress.setGeo(9);
        currentAddress.setRelays(relays);
        currentAddress.setRequired(9);
        currentAddress.setSpid(9);
        currentAddress.setSubstation(9);
        currentAddress.setTimestamp(now.minus(3000));
        currentAddress.setUda(9);
        currentAddress.setZip(9);
        
        {
            // Tests "new" addressing with no currentAddress and no relays
            Object result = ReflectionTestUtils.invokeMethod(service, "generateUpdatedAddressingFromMessage", data, devicePaoId, null);
            ExpressComReportedAddress resultAddress = (ExpressComReportedAddress)result;
            assertEquals(resultAddress.getDeviceId(), devicePaoId);
            assertEquals(resultAddress.getSpid(), 1);
            assertEquals(resultAddress.getGeo(), 2);
            assertEquals(resultAddress.getSubstation(), 3);
            assertEquals(resultAddress.getFeeder(), 4);
            assertEquals(resultAddress.getZip(), 5);
            assertEquals(resultAddress.getUda(), 6);
            assertEquals(resultAddress.getRequired(), 7);
            assertEquals(resultAddress.getRelays().size(), 0);
            assertEquals(resultAddress.getTimestamp(), now);
        }
        
        {
            // Tests "new" addressing with currentAddress and no reported relays
            Object result = ReflectionTestUtils.invokeMethod(service, "generateUpdatedAddressingFromMessage", data, devicePaoId, currentAddress);
            ExpressComReportedAddress resultAddress = (ExpressComReportedAddress)result;
            assertEquals(resultAddress.getDeviceId(), devicePaoId);
            assertEquals(resultAddress.getSpid(), 1);
            assertEquals(resultAddress.getGeo(), 2);
            assertEquals(resultAddress.getSubstation(), 3);
            assertEquals(resultAddress.getFeeder(), 4);
            assertEquals(resultAddress.getZip(), 5);
            assertEquals(resultAddress.getUda(), 6);
            assertEquals(resultAddress.getRequired(), 7);
            assertEquals(resultAddress.getRelays().size(), 3);
            assertEquals(resultAddress.getTimestamp(), now);
        }
        
        // Adding relays to the incoming data now for the rest of the tests
        // Relay addresses are 2 bytes, first byte is relay, second is address
        data.put(FieldType.RELAY_N_SPLINTER_ADDRESS, getBytes((short)0x0101)); // relay 1 splinter 1 
        data.put(FieldType.RELAY_N_SPLINTER_ADDRESS, getBytes((short)0x0202)); // relay 2 splinter 2 program 3
        data.put(FieldType.RELAY_N_PROGRAM_ADDRESS, getBytes((short)0x0203)); // relay 2 splinter 2 program 3
        data.put(FieldType.RELAY_N_PROGRAM_ADDRESS, getBytes((short)0x0303)); // relay 3 program 3
        data.put(FieldType.RELAY_N_PROGRAM_ADDRESS, getBytes((short)0x0404)); // relay 4 program 4
        
        {
            // Tests "new" addressing with relays and no currentAddress (yuk-19945)
            Object result = ReflectionTestUtils.invokeMethod(service, "generateUpdatedAddressingFromMessage", data, devicePaoId, null);
            ExpressComReportedAddress resultAddress = (ExpressComReportedAddress)result;
            System.out.println(resultAddress.getTimestamp());
            assertEquals(resultAddress.getDeviceId(), devicePaoId);
            assertEquals(resultAddress.getSpid(), 1);
            assertEquals(resultAddress.getGeo(), 2);
            assertEquals(resultAddress.getSubstation(), 3);
            assertEquals(resultAddress.getFeeder(), 4);
            assertEquals(resultAddress.getZip(), 5);
            assertEquals(resultAddress.getUda(), 6);
            assertEquals(resultAddress.getRequired(), 7);
            assertEquals(resultAddress.getRelays().size(), 4);
            assertEquals(resultAddress.getTimestamp(), now);
        }

        
        {
            Object result = ReflectionTestUtils.invokeMethod(service, "generateUpdatedAddressingFromMessage", data, devicePaoId, currentAddress);
            ExpressComReportedAddress resultAddress = (ExpressComReportedAddress)result;
            assertEquals(resultAddress.getDeviceId(), devicePaoId);
            assertEquals(resultAddress.getSpid(), 1);
            assertEquals(resultAddress.getGeo(), 2);
            assertEquals(resultAddress.getSubstation(), 3);
            assertEquals(resultAddress.getFeeder(), 4);
            assertEquals(resultAddress.getZip(), 5);
            assertEquals(resultAddress.getUda(), 6);
            assertEquals(resultAddress.getRequired(), 7);
            assertEquals(resultAddress.getRelays().size(), 4); // We added a relay so the count is 4 now
            assertEquals(resultAddress.getTimestamp(), now);
            
            // We had relay 1 program 1, we reported relay 1 splinter 1, so we should now record relay1 p1 s1
            ExpressComReportedAddressRelay testRelay = new ExpressComReportedAddressRelay(1);
            testRelay.setProgram(1);
            assertFalse(resultAddress.getRelays().contains(testRelay));
            testRelay.setSplinter(1);
            assertTrue(resultAddress.getRelays().contains(testRelay));
            
            // We switched from current of R2P2 to R2P3S2
            testRelay.setRelayNumber(2);
            testRelay.setProgram(3);
            testRelay.setSplinter(2);
            assertTrue(resultAddress.getRelays().contains(testRelay));
            
            // We switched from current of R3S3 to R3P3S3
            testRelay.setRelayNumber(3);
            testRelay.setProgram(3);
            testRelay.setSplinter(3);
            assertTrue(resultAddress.getRelays().contains(testRelay));
            
            // We added relay 4
            testRelay.setRelayNumber(4);
            testRelay.setProgram(4);
            testRelay.setSplinter(0);
            assertTrue(resultAddress.getRelays().contains(testRelay));
        }
        
        
        currentAddress.setTimestamp(now.plus(6000));
        
        {
            // moved currentAddress to the future, so we should get null
            Object result = ReflectionTestUtils.invokeMethod(service, "generateUpdatedAddressingFromMessage", data, devicePaoId, currentAddress);
            assertEquals(result, null);
        }
    }
    
    private byte[] getBytes(short value) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(value);
        return buffer.array();
    }
    
    private byte[] getBytes(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(value);
        return buffer.array();
    }
    
    private byte[] getBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(value);
        return buffer.array();
    }
}
