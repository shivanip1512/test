package com.cannontech.common.bulk.callbackResult;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
/**
 * Callback that handles response messages for data streaming config operations.
 */
public interface DataStreamingConfigCallback {
    
    /**
     * Handle a success response, with an optional reported configuration.
     */
    void receivedConfigSuccess(SimpleDevice device, ReportedDataStreamingConfig config);
    
    /**
     * Handle a failure response, with the reported error, and store the reported config if not null.
     */
    void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error, ReportedDataStreamingConfig config);
    
    /**
     * Called when the operation is complete.
     */
    void complete();
   
    default boolean isComplete() {
        return false;
    }

    void processingExceptionOccurred(String reason);
}
