package com.cannontech.common.bulk.callbackResult;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.database.data.lite.LiteYukonUser;
/**
 * Callback that handles response messages for data streaming config operations.
 */
public interface DataStreamingConfigCallback {
    
    /**
     * Handle a success response, with the reported configuration.
     */
    void receivedConfigReport(SimpleDevice device, ReportedDataStreamingConfig config);
    
    /**
     * Handle a failure response, with the reported error, and store the reported config if not null.
     */
    void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error, ReportedDataStreamingConfig config);
    
    /**
     * Called when the operation is complete.
     */
    void complete();
   
    boolean isComplete();

    void processingExceptionOccured(String reason);

    /**
     * Called when the operation is cancelled.
     */

    void cancel(LiteYukonUser user);
}
