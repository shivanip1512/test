package com.cannontech.web.stars.dr.operator.hardware.service;

import java.util.Map;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.user.YukonUserContext;

public interface HardwareShedLoadStrategy {

    /**
     * The shed load is dependent on the implementation.
     * @return A Map<String, Object> containing a "success" key with a boolean
     *         value and a "message" key containing localize text of success or
     *         error messages.
     */
    Map<String, Object> shedLoad(int deviceId, int relay, int duration, String serialNo,
            YukonUserContext userContext);

    /**
     * Will get the type of strategy.
     */
    HardwareStrategyType getType();

    /**
     * Will check if the given hardware type can be handled.
     */
    boolean canHandle(HardwareType type);

}
