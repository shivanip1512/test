package com.cannontech.web.stars.dr.operator.hardware.service;

import java.util.Map;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.user.YukonUserContext;

public interface HardwareReadNowStrategy {
    
    /**
     * The data read is dependent on the implementation.
     * @return A Map<String, Object> containing a "success" key with a boolean
     *         value and a "message" key containing localize text of success or error messages.
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
