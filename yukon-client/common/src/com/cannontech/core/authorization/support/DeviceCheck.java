package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Interface used to check a device to see if it matches certain critieria
 */
public interface DeviceCheck {

    public abstract boolean checkDevice(LiteYukonPAObject device);

}