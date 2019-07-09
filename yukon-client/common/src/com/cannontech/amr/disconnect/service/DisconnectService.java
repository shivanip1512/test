package com.cannontech.amr.disconnect.service;

import java.util.List;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectMeterResult;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface DisconnectService {

    /**
     * Returns true if the devices can be armed.
     */
    boolean supportsArm(List<SimpleDevice> meters);

    /**
     * Returns true if at least one of the devices supports disconnect.
     * Supports disconnect means the device has integrated disconnect OR disconnect collar configured.
     */
    boolean supportsDisconnect(List<SimpleDevice> meters);

    /**
     * Returns true if at least one of the devices supports disconnect.
     * Supports disconnect means the device has integrated disconnect OR
     * when includeNotConfigured is true, device supports disconnect collar BUT it is NOT configured.
     * when includeNotConfigured is false, device supports disconnect collar AND it IS configured.
     * 
     * @param meters
     * @return
     */
    boolean supportsDisconnect(List<SimpleDevice> meters, boolean includeNotConfigured);

    /**
     * Returns the list of devices that support disconnect.
     * Supports disconnect means the device has integrated disconnect OR 
     * device supports disconnect collar AND it IS configured. This is the equivalent of 
     * {@link DisconnectService#supportsDisconnect(List)}
     * 
     * @param meters
     * @return
     */
    List<SimpleDevice> filter(List<SimpleDevice> meters);
    
    /**
     * Executes connect, disconnect or arm commands and waits for the result.
     * Use {@link DisconnectService#supportsDisconnect()} to verify that device is supported prior to calling
     * this method.
     * 
     * @throws UnsupportedOperationException if the device doesn't support disconnect.
     */
    DisconnectMeterResult execute(DisconnectCommand command, DeviceRequestType type, YukonMeter meter,
            LiteYukonUser user);

    CollectionActionResult execute(DisconnectCommand command, DeviceCollection deviceCollection,
            SimpleCallback<CollectionActionResult> callback, YukonUserContext context);

    
}
