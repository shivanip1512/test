package com.cannontech.amr.disconnect.service;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectMeterResult;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.user.YukonUserContext;

public interface DisconnectService {

    /**
     * Executes connect, disconnect or arm commands
     */
    DisconnectResult execute(DisconnectCommand command, DeviceCollection deviceCollection,
            SimpleCallback<DisconnectResult> callback, YukonUserContext userContext);

    /**
     * Returns result for the key provided.
     */
    DisconnectResult getResult(String key);

    /**
     * Attempts to cancel the command sent.
     */
    void cancel(String key, YukonUserContext userContext, DisconnectCommand command);

    /**
     * Returns the list of completed and pending results
     */
    Iterable<DisconnectResult> getResults();

    /**
     * Returns true if the devices can be armed.
     */
    boolean supportsArm(Iterable<SimpleDevice> meters);

    /**
     * Returns true if at least one of the devices supports disconnect.
     * Supports disconnect means the device has integrated disconnect OR disconnect collar configured.
     */
    boolean supportsDisconnect(Iterable<SimpleDevice> meters);

    /**
     * Returns true if at least one of the devices supports disconnect.
     * Supports disconnect means the device has integrated disconnect OR
     * when includeNotConfigured is true, device supports disconnect collar BUT it is NOT configured.
     * when includeNotConfigured is false, device supports disconnect collar AND it IS configured.
     * 
     * @param meters
     * @return
     */
    boolean supportsDisconnect(Iterable<SimpleDevice> meters, boolean includeNotConfigured);

    /**
     * Executes connect, disconnect or arm commands and waits for the result.
     * Use {@link DisconnectService#supportsDisconnect()} to verify that device is supported prior to calling
     * this method.
     * 
     * @@ -52,12 +63,4 @@
     * @throws UnsupportedOperationException if the device doesn't support disconnect.
     */
    DisconnectMeterResult execute(DisconnectCommand command, DeviceRequestType type, YukonMeter meter,
            YukonUserContext userContext);

}
