package com.cannontech.common.bulk.callbackResult;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisService;
import com.cannontech.common.device.model.SimpleDevice;

public class ArchiveDataAnalysisCallbackResult extends BackgroundProcessBulkProcessorCallback<SimpleDevice> implements BackgroundProcessResultHolder {
    private Logger log = YukonLogManager.getLogger(ArchiveDataAnalysisService.class);
    private long numberOfDevicesToProcess;
    private long numberOfDevicesProcessed = 0;
    private Instant startTime;
    
    public ArchiveDataAnalysisCallbackResult(String resultsId, DeviceCollection deviceCollection) {
        super(BackgroundProcessTypeEnum.ARCHIVE_DATA_ANALYSIS, resultsId, (int)deviceCollection.getDeviceCount());
        this.backgroundProcessType = BackgroundProcessTypeEnum.ARCHIVE_DATA_ANALYSIS;
        numberOfDevicesToProcess = deviceCollection.getDeviceCount();
        startTime = new Instant();
    }
    
    @Override
    public void processedObject(int rowNumber, SimpleDevice object) {
        super.processedObject(rowNumber, object);
        addToCount();
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, SimpleDevice object, ProcessorCallbackException e) {
        super.receivedProcessingException(rowNumber, object, e);
        addToCount();
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
        return true;
    }
    
    @Override
    public boolean isFailureFileSupported() {
        return false;
    }
    
    private void addToCount() {
        numberOfDevicesProcessed++;
        if(numberOfDevicesProcessed == numberOfDevicesToProcess) {
            long seconds = new Duration(startTime, new Instant()).getStandardSeconds();
            log.info("Archive data analysis complete. Processed " + numberOfDevicesProcessed + " devices in " + seconds + " seconds.");
        }
    }
}
