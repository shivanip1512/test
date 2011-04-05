package com.cannontech.common.device.service;


import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoAdapter;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.device.range.IntegerRange;
import com.cannontech.device.range.InvalidRangeException;
import com.cannontech.device.range.impl.PlcAddressRangeServiceImpl;

public class PlcAddressRangeServiceTest extends TestCase{

    private PlcAddressRangeServiceImpl plcAddressRangeServiceImpl;
    
    
    @Before
    public void setUp() throws Exception {
        plcAddressRangeServiceImpl = new PlcAddressRangeServiceImpl();
        plcAddressRangeServiceImpl.setPaoDefinitionDao(new MockPaoDefinitionDao());
    }
    
    @Test
    public void testInvoke() throws Exception {
        
        // No range provided --> All addresses 0 - MAXINTEGER should be valid
        assertAddressCheck(TestPaoType.NO_RANGE, 0, true);
        assertAddressCheck(TestPaoType.NO_RANGE, Integer.MAX_VALUE, true);
        assertAddressCheck(TestPaoType.NO_RANGE, 5000, true);
        assertAddressCheck(TestPaoType.NO_RANGE, -1, false);
        
        // One single address is allowed
        assertAddressCheck(TestPaoType.SINGLE_ADDRESS, 1000, true);
        assertAddressCheck(TestPaoType.SINGLE_ADDRESS, 1001, false);
        assertAddressCheck(TestPaoType.SINGLE_ADDRESS, 999, false);
        assertParsing(TestPaoType.SINGLE_ADDRESS, "1000");
      
        // Single Interval
        assertAddressCheck(TestPaoType.SINGLE_INTERVAL, 1500, true);
        assertAddressCheck(TestPaoType.SINGLE_INTERVAL, 1000, true);
        assertAddressCheck(TestPaoType.SINGLE_INTERVAL, 2000, true);
        assertAddressCheck(TestPaoType.SINGLE_INTERVAL, 2001, false);
        assertAddressCheck(TestPaoType.SINGLE_INTERVAL, 999, false);
        assertParsing(TestPaoType.SINGLE_INTERVAL, "1000 - 2000");
        
        // Multi Intervals
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 1500, true);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 1000, true);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 2000, true);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 3000, true);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 4000, true);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 5000, true);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 6000, true);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 6001, false);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 999, false);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 2001, false);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 2999, false);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 4001, false);
        assertAddressCheck(TestPaoType.MULTI_INTERVAL, 4999, false);
        assertParsing(TestPaoType.MULTI_INTERVAL, "1000 - 2000, 3000 - 4000, 5000 - 6000");
        
        // Mixed
        assertAddressCheck(TestPaoType.MIXED, 1000, true);
        assertAddressCheck(TestPaoType.MIXED, 1500, true);
        assertAddressCheck(TestPaoType.MIXED, 2000, true);
        assertAddressCheck(TestPaoType.MIXED, 3000, true);
        assertAddressCheck(TestPaoType.MIXED, 4000, true);
        assertAddressCheck(TestPaoType.MIXED, 4500, true);
        assertAddressCheck(TestPaoType.MIXED, 5000, true);
        assertAddressCheck(TestPaoType.MIXED, 6000, true);
        assertAddressCheck(TestPaoType.MIXED, 999, false);
        assertAddressCheck(TestPaoType.MIXED, 2001, false);
        assertAddressCheck(TestPaoType.MIXED, 2999, false);
        assertAddressCheck(TestPaoType.MIXED, 3001, false);
        assertAddressCheck(TestPaoType.MIXED, 3999, false);
        assertAddressCheck(TestPaoType.MIXED, 5001, false);
        assertAddressCheck(TestPaoType.MIXED, 5999, false);
        assertAddressCheck(TestPaoType.MIXED, 6001, false);
        assertParsing(TestPaoType.MIXED, "1000 - 2000, 3000, 4000 - 5000, 6000");
        
        //Additional spaces in address range string
        assertParsing(TestPaoType.SPACES, "1000 - 2000, 3000, 4000 - 5000");
        
        //Invalid range string formats
        try {
            assertAddressCheck(TestPaoType.INVALID_FORMAT1, 5000, false);
            fail();
        } catch (InvalidRangeException e) { }
        
        try {
            assertAddressCheck(TestPaoType.INVALID_FORMAT2, 5000, true);
            fail();
        } catch (InvalidRangeException e) { }
        
    }
    
    private void assertParsing(TestPaoType testPaoType, String expectedRangeString) {
        IntegerRange range = plcAddressRangeServiceImpl.getAddressRangeForDevice(testPaoType.paoType);
        assertEquals(expectedRangeString, range.toString());
    }
    
    private void assertAddressCheck(TestPaoType testPaoType, int testAddress, boolean isValid) {
        boolean result = plcAddressRangeServiceImpl.isValidAddress(testPaoType.paoType, testAddress);
        assertEquals(result, isValid);
    }
    
    
    private enum TestPaoType {
        NO_RANGE(PaoType.ALPHA_A1, null),
        SINGLE_ADDRESS(PaoType.ALPHA_A3, "1000"),
        SINGLE_INTERVAL(PaoType.ALPHA_PPLUS, "1000-2000"),
        MULTI_INTERVAL(PaoType.CAP_CONTROL_AREA, "1000-2000,3000-4000,5000-6000"),
        MULTI_ADDRESSES(PaoType.CAP_CONTROL_FEEDER, "1000,2000,3000"),
        MIXED(PaoType.CAP_CONTROL_SPECIAL_AREA, "1000-2000,3000,4000-5000,6000"),
        SPACES(PaoType.CAP_CONTROL_SUBBUS, " 1000 - 2000,   3000 , 4000-  5000   "),
        INVALID_FORMAT1(PaoType.CAP_CONTROL_SUBSTATION, "1000 2000, 3000"),
        INVALID_FORMAT2(PaoType.CAPBANK, "1000 - 2000 - 3000, 4000 - 5000");
        
        private String rangeString;
        private PaoType paoType;
        
        private TestPaoType(PaoType paoType, String rangeString) {
            this.rangeString = rangeString;
            this.paoType = paoType;
        }
        
        public static TestPaoType getTestPaoType(PaoType paoType) {
            for(TestPaoType testType : TestPaoType.values()) {
                if(testType.paoType == paoType) {
                    return testType;
                }
            }
            
            throw new IllegalArgumentException("Pao Type not defined in test enum");
        }
    }
    
    private class MockPaoDefinitionDao extends PaoDefinitionDaoAdapter{
        
        @Override
        public boolean isTagSupported(PaoType paoType, PaoTag feature) {
            TestPaoType testType = TestPaoType.getTestPaoType(paoType);
            return testType.rangeString == null ? false : true;
        }
        
        public String getValueForTagString(PaoType paoType, PaoTag tag) {
            TestPaoType testType = TestPaoType.getTestPaoType(paoType);
            return testType.rangeString;
        }
    }
}
