package com.cannontech.amr.disconnect.service;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dynamic.PointValueHolder;

public interface DisconnectCallback {
    
    /**
     * This method should be called when it is known that connect/disconnect/arm command failed for this device
     */
    void failed(SimpleDevice device, SpecificDeviceErrorDescription error);
    
    /**
     * This method should be called when it is known that all the requests are completed
     */
    void complete(StrategyType strategy);
    
    /**
     * This method should be called if an error occurred that will
     * prevent the command from being sent to any devices (example: no porter connection)
     */
    void processingExceptionOccured(String reason);
   
    void success(DisconnectCommand state, SimpleDevice device, PointValueHolder value);
    
    default CollectionActionResult getResult() {
        return null;
    }

    default void canceled(SimpleDevice device) {

    }
}
