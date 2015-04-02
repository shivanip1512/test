package com.cannontech.amr.disconnect.service;

import org.joda.time.Instant;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.Cancelable;
import com.cannontech.common.util.Completable;

public interface DisconnectCallback extends Completable, Cancelable{
    
    /**
     * This method should be called when it is known that the device is connected.
     */
    void connected(SimpleDevice device, Instant instant);

    /**
     * This method should be called when it is known that the device is armed.
     */
    void armed(SimpleDevice device, Instant instant);

    /**
     * This method should be called when it is known that the device is disconnected.
     */
    void disconnected(SimpleDevice device, Instant instant);

    /**
     * This method should be called when it is known the command was not sent to the device (RF) or
     * the cancel command was sent to the device (PLC) and device did not respond as connected or
     * disconnected after 10 minutes.
     */
    void canceled(SimpleDevice device);

    /**
     * This method should be called when it is known that connect/disconnect/arm command failed for this device
     */
    void failed(SimpleDevice device, SpecificDeviceErrorDescription error);
    
    /**
     * This method should be called when it is known that all the requests are completed
     */
    void complete();
    
    /**
     * This method should be called if user canceled
     */
    void cancel();
    
    /**
     * This method should be called if an error occurred that will
     * prevent the command from being sent to any devices (example: no porter connection)
     */
    void processingExceptionOccured(String reason);
    
}
