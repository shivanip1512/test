package com.cannontech.common.bulk.callbackResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;

/**
 * A callback result class for data streaming configuration and data streaming disable operations.
 */
//TODO This callback will need to be fleshed-out. It has to:
//1. Store results of the operation to be shown on the progress page
//2. Update the database when we receive confirmation of a successful configuration from Porter?
public class DataStreamingConfigCallbackResult implements BackgroundProcessResultHolder, DataStreamingConfigCallback {
    
    private DeviceCollection unsupportedDeviceCollection;
    private DeviceCollection cancelledDeviceCollection;
    
    private static final Logger log = YukonLogManager.getLogger(DataStreamingConfigCallbackResult.class);
    
    @Override
    public void receivedConfigReport(SimpleDevice device, ReportedDataStreamingConfig config) {
        log.info("Received config report. Device = " + device + ", config = " + config);
    }

    @Override
    public void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error) {
        log.info("Received config error. Device = " + device + ", error = " + error);
    }

    @Override
    public void complete() {
        log.info("Data streaming callback complete.");
    }

    @Override
    public void cancel() {
        log.info("Data streaming callback canceled.");
    }

    @Override
    public BackgroundProcessTypeEnum getBackgroundProcessType() {
        return null;
    }

    @Override
    public List<ProcessorCallbackException> getProcessingExceptionList() {
        return null;
    }

    @Override
    public Map<Integer, ProcessorCallbackException> getProcessingExceptionRowNumberMap() {
        return null;
    }

    @Override
    public int getSuccessCount() {
        return 0;
    }

    @Override
    public int getProcessingExceptionCount() {
        return 0;
    }

    @Override
    public int getTotalItems() {
        return 0;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isSuccessfull() {
        return false;
    }

    @Override
    public boolean isProcessingFailed() {
        return false;
    }

    @Override
    public Exception getFailedException() {
        return null;
    }

    @Override
    public Date getStartTime() {
        return null;
    }

    @Override
    public Date getStopTime() {
        return null;
    }

    @Override
    public boolean isSuccessDevicesSupported() {
        return false;
    }

    @Override
    public DeviceCollection getSuccessDeviceCollection() {
        return null;
    }

    @Override
    public boolean isFailureDevicesSupported() {
        return false;
    }

    @Override
    public DeviceCollection getFailureDeviceCollection() {
        return null;
    }

    @Override
    public boolean isFailureReasonsListSupported() {
        return false;
    }

    @Override
    public boolean isFailureFileSupported() {
        return false;
    }

    public DeviceCollection getUnsupportedDeviceCollection() {
        return unsupportedDeviceCollection;
    }

    public void setUnsupportedDeviceCollection(DeviceCollection unsupportedDeviceCollection) {
        this.unsupportedDeviceCollection = unsupportedDeviceCollection;
    }

    public DeviceCollection getCancelledDeviceCollection() {
        return cancelledDeviceCollection;
    }

    public void setCancelledDeviceCollection(DeviceCollection cancelledDeviceCollection) {
        this.cancelledDeviceCollection = cancelledDeviceCollection;
    }

}
