package com.cannontech.web.tools.commander.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.message.porter.message.Return;

public class CommandResponseTest {
    
    private static final int responseId = 198;
    private static final int deviceId = 99;
    private static final Date timestamp = new Date(1597945886662L);
    
    @Test
    public void test_normalReturnTextIsIncluded() {
        var rtn = new Return();

        rtn.setDeviceID(deviceId);
        rtn.setStatus(DeviceError.SUCCESS.getCode());
        rtn.setResultString(
                "This string has multiple lines." 
                + "\nSee?" 
                + "\nHere's another.");
        rtn.setTimeStamp(timestamp);
        
        var cr = CommandResponse.of(responseId, rtn);
        
        assertEquals(List.of("This string has multiple lines.", "See?", "Here's another."), cr.getResults());
    }

    @Test
    public void test_errorReturnTextIsExcluded() {
        var rtn = new Return();

        rtn.setDeviceID(deviceId);
        rtn.setStatus(DeviceError.FUNCTION_CODE_NOT_SUPPORTED.getCode());
        rtn.setResultString(
                "This string has multiple lines." 
                + "\nSee?" 
                + "\nHere's another.");
        rtn.setTimeStamp(timestamp);
        
        var cr = CommandResponse.of(responseId, rtn);
        
        assertEquals(Collections.emptyList(), cr.getResults());
    }
}