package com.cannontech.dr.estimatedload.service.impl;

import junit.framework.Assert;

import org.easymock.EasyMockSupport;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class FormulaServiceTest extends EasyMockSupport {

    // Class under test
    private FormulaServiceImpl formulaServiceImpl = new FormulaServiceImpl();
    
    @Test
    public void testCalculateTimeOfDayValue() {
        // All intervals are 60 minutes now. Will be revisited once device firmware allows reporting other interval sizes.
        int timeOfDayIntervalInMinutes = 60;
        LocalTime time;
        Double value;
        
        // Test 12:00:00am - Output should be 1.0
        time = new LocalTime(0, 0, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(1.0, value, 0.0001);
        
        // Test 12:59:00am - Output should be 1.0
        time = new LocalTime(0, 59, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(1.0, value, 0.0001);
        
        // Test 11:00:00am - Output should be 12.0
        time = new LocalTime(11, 00, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(12.0, value, 0.0001);
        
        // Test 11:59:00am - Output should be 12.0
        time = new LocalTime(11, 59, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(12.0, value, 0.0001);
        
        // Test 12:00:00pm - Output should be 13.0
        time = new LocalTime(12, 00, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(13.0, value, 0.0001);
        
        // Test 12:59:00pm - Output should be 13.0
        time = new LocalTime(12, 59, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(13.0, value, 0.0001);
        
        // Test 4:00:00pm - Output should be 17.0
        time = new LocalTime(16, 00, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(17.0, value, 0.0001);
        
        // Test 4:59:00pm - Output should be 17.0
        time = new LocalTime(16, 59, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(17.0, value, 0.0001);
        
        // Test 11:00:00pm - Output should be 24.0
        time = new LocalTime(23, 00, 0);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(24.0, value, 0.0001);
        
        // Test 11:59:59pm - Output should be 24.0
        time = new LocalTime(23, 59, 59);
        value = ReflectionTestUtils.invokeMethod(formulaServiceImpl, "calculateTimeOfDayValue", 
                time, timeOfDayIntervalInMinutes);
        Assert.assertEquals(24.0, value, 0.0001);
    }
}
