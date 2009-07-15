package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class AssignConfigCallbackResult extends BackgroundProcessBulkProcessorCallback<YukonDevice> implements BackgroundProcessResultHolder {

    private DeviceCollection deviceCollection;
    private StoredDeviceGroup successGroup;
    private StoredDeviceGroup processingExceptionGroup;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
    public AssignConfigCallbackResult(String resultsId, 
                                    DeviceCollection deviceCollection, 
                                    StoredDeviceGroup successGroup, 
                                    StoredDeviceGroup processingExceptionGroup, 
                                    DeviceGroupMemberEditorDao deviceGroupMemberEditorDao,
                                    DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        
        super(BackgroundProcessTypeEnum.ASSIGN_CONFIG, resultsId, (int)deviceCollection.getDeviceCount());
    
        this.deviceCollection = deviceCollection;
        this.successGroup = successGroup;
        this.processingExceptionGroup = processingExceptionGroup;
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    
    // callback overrides
    @Override
    public void processedObject(int rowNumber, YukonDevice object) {
        super.processedObject(rowNumber, object);
        deviceGroupMemberEditorDao.addDevices(successGroup, object);
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, YukonDevice object, ProcessorCallbackException e) {
        super.receivedProcessingException(rowNumber, object, e);
        deviceGroupMemberEditorDao.addDevices(processingExceptionGroup, object);
    }
    
    // results overrides
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
    
    @Override
    public boolean isBulkFieldListingSupported() {
        return false;
    }
    @Override
    public List<BulkFieldColumnHeader> getBulkFieldColumnHeaders() {
        throw new UnsupportedOperationException();
    }
}