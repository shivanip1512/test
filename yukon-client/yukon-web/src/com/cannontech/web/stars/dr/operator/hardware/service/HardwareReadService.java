package com.cannontech.web.stars.dr.operator.hardware.service;

import java.util.Map;

import com.cannontech.user.YukonUserContext;

public interface HardwareReadService {
    
    /**
     * Will read the device details.
     */
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext); 
          
}
