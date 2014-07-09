package com.cannontech.web.stars.dr.operator.hardware.service;

import java.util.Map;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;

public interface DeviceReadStrategy {
    
    /**
     * Will read the device details.
     */
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext);
    
    /**
     * Will get the type of strategy.
     */
    public HardwareStrategyType getType();
    
    /**
     * Will check if the given hardware type can be handled.
     */
    public boolean canHandle(HardwareType type);
        
    
}
