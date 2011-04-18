package com.cannontech.common.device.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.cannontech.device.range.IntegerRange;
import com.cannontech.device.range.InvalidRangeException;

public class PlcAddressRangeServiceTest{
    
    @Test
    public void testInvoke() throws Exception {
        
        // Test for correct string parsing
        assertParsing(TestRangeString.SINGLE_ADDRESS.toString(), "1000");
        assertParsing(TestRangeString.SINGLE_INTERVAL.toString(), "1000 - 2000");
        assertParsing(TestRangeString.MULTI_INTERVAL.toString(), "1000 - 2000, 3000 - 4000, 5000 - 6000");
        assertParsing(TestRangeString.MULTI_ADDRESSES.toString(), "1000, 2000, 3000");
        assertParsing(TestRangeString.MIXED.toString(), "1000 - 2000, 3000, 4000 - 5000, 6000");
        assertParsing(TestRangeString.SPACES.toString(), "1000 - 2000, 3000, 4000 - 5000");
        
        // One single address is allowed
        assertAddressCheck(TestRangeString.SINGLE_ADDRESS, 1000, true);
        assertAddressCheck(TestRangeString.SINGLE_ADDRESS, 1001, false);
        assertAddressCheck(TestRangeString.SINGLE_ADDRESS, 999, false);
      
        // Single Interval
        assertAddressCheck(TestRangeString.SINGLE_INTERVAL, 1500, true);
        assertAddressCheck(TestRangeString.SINGLE_INTERVAL, 1000, true);
        assertAddressCheck(TestRangeString.SINGLE_INTERVAL, 2000, true);
        assertAddressCheck(TestRangeString.SINGLE_INTERVAL, 2001, false);
        assertAddressCheck(TestRangeString.SINGLE_INTERVAL, 999, false);
        
        // Multi Intervals
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 1500, true);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 1000, true);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 2000, true);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 3000, true);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 4000, true);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 5000, true);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 6000, true);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 6001, false);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 999, false);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 2001, false);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 2999, false);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 4001, false);
        assertAddressCheck(TestRangeString.MULTI_INTERVAL, 4999, false);
        
        // Mixed
        assertAddressCheck(TestRangeString.MIXED, 1000, true);
        assertAddressCheck(TestRangeString.MIXED, 1500, true);
        assertAddressCheck(TestRangeString.MIXED, 2000, true);
        assertAddressCheck(TestRangeString.MIXED, 3000, true);
        assertAddressCheck(TestRangeString.MIXED, 4000, true);
        assertAddressCheck(TestRangeString.MIXED, 4500, true);
        assertAddressCheck(TestRangeString.MIXED, 5000, true);
        assertAddressCheck(TestRangeString.MIXED, 6000, true);
        assertAddressCheck(TestRangeString.MIXED, 999, false);
        assertAddressCheck(TestRangeString.MIXED, 2001, false);
        assertAddressCheck(TestRangeString.MIXED, 2999, false);
        assertAddressCheck(TestRangeString.MIXED, 3001, false);
        assertAddressCheck(TestRangeString.MIXED, 3999, false);
        assertAddressCheck(TestRangeString.MIXED, 5001, false);
        assertAddressCheck(TestRangeString.MIXED, 5999, false);
        assertAddressCheck(TestRangeString.MIXED, 6001, false);
        
        //Invalid range string formats
        try {
            assertAddressCheck(TestRangeString.INVALID_FORMAT1, 5000, false);
            fail();
        } catch (InvalidRangeException e) { }
        
        try {
            assertAddressCheck(TestRangeString.INVALID_FORMAT2, 5000, true);
            fail();
        } catch (InvalidRangeException e) { }
        
    }
    
    private void assertParsing(String inputString, String expectedRangeString) {
        IntegerRange range = new IntegerRange(inputString);
        assertEquals(expectedRangeString, range.toString());
    }
    
    private void assertAddressCheck(TestRangeString testString, int testAddress, boolean isValid) {
        String rangeString = testString.toString();
        IntegerRange range = new IntegerRange(rangeString);
        boolean result = range.isWithinRange(testAddress);
        assertEquals(result, isValid);
    }
    
    
    private enum TestRangeString {
        SINGLE_ADDRESS("1000"),
        SINGLE_INTERVAL("1000-2000"),
        MULTI_INTERVAL("1000-2000,3000-4000,5000-6000"),
        MULTI_ADDRESSES("1000,2000,3000"),
        MIXED("1000-2000,3000,4000-5000,6000"),
        SPACES(" 1000 - 2000,   3000 , 4000-  5000   "),
        INVALID_FORMAT1("1000 2000, 3000"),
        INVALID_FORMAT2("1000 - 2000 - 3000, 4000 - 5000");
        
        private String rangeString;
        
        private TestRangeString(String rangeString) {
            this.rangeString = rangeString;
        }
        
        public String toString() {
            return rangeString;
        }
    }
    
}
