package com.cannontech.dr.rfn.service;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.dr.rfn.model.RfnLcrTlvPointDataType;
import com.cannontech.dr.rfn.service.impl.RfnLcrTlvDataMappingServiceImpl;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public class RfnLcrTlvDataMappingServiceTest {
    private RfnLcrTlvDataMappingServiceImpl service = new RfnLcrTlvDataMappingServiceImpl();;
    
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
