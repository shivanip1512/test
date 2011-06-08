package com.cannontech.common.bulk.callbackResult;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;

public class ArchiveDataAnalysisCallbackResult extends BackgroundProcessBulkProcessorCallback<SimpleDevice> implements BackgroundProcessResultHolder {
    private StoredDeviceGroup successGroup;
    private StoredDeviceGroup processingExceptionGroup;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
    public ArchiveDataAnalysisCallbackResult(String resultsId, 
                                             DeviceCollection deviceCollection,
                                             StoredDeviceGroup successGroup, 
                                             StoredDeviceGroup processingExceptionGroup, 
                                             DeviceGroupMemberEditorDao deviceGroupMemberEditorDao,
                                             DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        
        super(BackgroundProcessTypeEnum.ARCHIVE_DATA_ANALYSIS, resultsId, (int)deviceCollection.getDeviceCount());
        
        this.backgroundProcessType = BackgroundProcessTypeEnum.ARCHIVE_DATA_ANALYSIS;
        this.successGroup = successGroup;
        this.processingExceptionGroup = processingExceptionGroup;
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Override
    public void processedObject(int rowNumber, SimpleDevice object) {
        super.processedObject(rowNumber, object);
        deviceGroupMemberEditorDao.addDevices(successGroup, object);
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, SimpleDevice object, ProcessorCallbackException e) {
        super.receivedProcessingException(rowNumber, object, e);
        deviceGroupMemberEditorDao.addDevices(processingExceptionGroup, object);
    }
    
    
    @Override
    public boolean isSuccessDevicesSupported() {
        return true;
    }
    
    @Override
    public DeviceCollection getSuccessDeviceCollection() {
        return deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
    }
    
    @Override
    public boolean isFailureDevicesSupported() {
        return true;
    }
    @Override
    public DeviceCollection getFailureDeviceCollection() {
        return deviceGroupCollectionHelper.buildDeviceCollection(processingExceptionGroup);
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
