package com.cannontech.common.bulk.callbackResult;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
/**
 * Callback that handles response messages for data streaming config operations.
 */
public interface DataStreamingConfigCallback {
    
    /**
     * Handle a success response, with the reported configuration.
     */
    void receivedConfigReport(SimpleDevice device, ReportedDataStreamingConfig config);
    
    /**
     * Handle a failure response, with the reported error.
     */
    void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error);
    
    /**
     * Called when the operation is complete.
     */
    void complete();
    
    /**
     * Called when the operation is cancelled.
     */
    void cancel();
}
