package com.cannontech.common.bulk.callbackResult;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.model.SimpleDevice;

public class ArchiveDataAnalysisCallbackResult extends BackgroundProcessBulkProcessorCallback<SimpleDevice> implements BackgroundProcessResultHolder {
    
    public ArchiveDataAnalysisCallbackResult(String resultsId, DeviceCollection deviceCollection) {
        super(BackgroundProcessTypeEnum.ARCHIVE_DATA_ANALYSIS, resultsId, (int)deviceCollection.getDeviceCount());
        this.backgroundProcessType = BackgroundProcessTypeEnum.ARCHIVE_DATA_ANALYSIS;
    }
    
    @Override
    public void processedObject(int rowNumber, SimpleDevice object) {
        super.processedObject(rowNumber, object);
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, SimpleDevice object, ProcessorCallbackException e) {
        super.receivedProcessingException(rowNumber, object, e);
    }
    
    
    @Override
    public boolean isSuccessDevicesSupported() {
        return true;
    }
    
    @Override
    public DeviceCollection getSuccessDeviceCollection() {
        return null;
    }
    
    @Override
    public boolean isFailureDevicesSupported() {
        return true;
    }
    
    @Override
    public DeviceCollection getFailureDeviceCollection() {
        return null;
    }
    
    @Override
    public boolean isFailureReasonsListSupported() {
        return true;
    }
    
    @Override
    public boolean isFailureFileSupported() {
        return false;
    }
    
}
