package com.cannontech.amr.disconnect.service;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.CollectionActionStrategyCompletionCallback;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dynamic.PointValueHolder;

public interface DisconnectCallback extends CollectionActionStrategyCompletionCallback {

    /**
     * This method should be called when it is known that connect/disconnect/arm command failed for this
     * device
     */
    void failed(SimpleDevice device, SpecificDeviceErrorDescription error);

    /**
     * This method should be called if an error occurred that will
     * prevent the command from being sent to any devices (example: no porter connection)
     */
    void processingExceptionOccured(String reason);

    void success(DisconnectCommand state, SimpleDevice device, PointValueHolder value);

    default CollectionActionResult getResult() {
        return null;
    }
}
