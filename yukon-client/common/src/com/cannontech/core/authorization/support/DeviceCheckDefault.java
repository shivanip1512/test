package com.cannontech.core.authorization.support;

import java.util.Collections;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Default implemenation class for CommandDeviceCheck. Returns true for all
 * devices that are not excluded by the exclude list.
 */
public class DeviceCheckDefault implements DeviceCheck {

    /**
     * List of DeviceChecks that should be excluded from DeviceCheckDefault. If
     * a device matches any of the checks in this list, checkDevice will return
     * false for DeviceCheckDefault
     */
    List<DeviceCheck> excludeList = Collections.emptyList();

    public void setExcludeList(List<DeviceCheck> excludeList) {
        this.excludeList = excludeList;
    }

    public boolean checkDevice(LiteYukonPAObject device) {

        for (DeviceCheck excludeCheck : excludeList) {
            if (excludeCheck.checkDevice(device)) {
                return false;
            }
        }
        return true;
    }

}
