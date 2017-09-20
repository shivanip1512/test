package com.cannontech.web.stars.dr.operator.hardware.service;

import java.util.Map;

import com.cannontech.user.YukonUserContext;

public interface HardwareShedRestoreLoadService {

    /**
     * Will shed load on a relay for a device.
     */
    Map<String, Object> shedLoad(int deviceId, int relay, int duration, YukonUserContext userContext);
    /**
     * Will restore the load on a relay for a device.
     */
    Map<String, Object> restoreLoad(int inventoryId, int relay, YukonUserContext userContext);

}
