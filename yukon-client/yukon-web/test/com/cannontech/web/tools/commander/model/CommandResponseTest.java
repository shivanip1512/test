package com.cannontech.web.tools.commander.model;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.message.porter.message.Return;

public class CommandResponseTest {
    
    private static final int RESPONSE_ID = 198;
    private static final int DEVICE_ID = 99;
    private static final int SUCCESS = DeviceError.SUCCESS.getCode();
    private static final int FUNCTION_CODE_NOT_SUPPORTED = DeviceError.FUNCTION_CODE_NOT_SUPPORTED.getCode();
    private static final Date TIMESTAMP = new Date(1597945886662L);
    
    @Test
    public void test_normalReturnTextIsIncluded() {
        var rtn = new Return();

        rtn.setDeviceID(DEVICE_ID);
        rtn.setStatus(SUCCESS);
        rtn.setResultString(
                "This string has multiple lines." 
                + "\nSee?" 
                + "\nHere's another.");
        rtn.setTimeStamp(TIMESTAMP);
        
        var cr = CommandResponse.of(RESPONSE_ID, rtn);
        
        assertEquals(List.of("This string has multiple lines.", "See?", "Here's another."), cr.getResults());
    }

    @Test
    public void test_errorReturnTextIsExcluded() {
        var rtn = new Return();

        rtn.setDeviceID(DEVICE_ID);
        rtn.setStatus(FUNCTION_CODE_NOT_SUPPORTED);
        rtn.setResultString(
                "This string has multiple lines." 
                + "\nSee?" 
                + "\nHere's another.");
        rtn.setTimeStamp(TIMESTAMP);
        
        var cr = CommandResponse.of(RESPONSE_ID, rtn);
        
        assertEquals(Collections.emptyList(), cr.getResults());
    }
}