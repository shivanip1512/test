package com.cannontech.web.stars.dr.operator.hardware.service;

import java.util.Map;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.user.YukonUserContext;

public interface HardwareService {
    
    /**
     * Will read the device details.
     */
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext); 
          
}
