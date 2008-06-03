package com.cannontech.common.bulk;

import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class DeviceGroupAddingBulkProcessorCallback extends CollectingBulkProcessorCallback<YukonDevice> {
    
    protected StoredDeviceGroup successGroup = null;
    protected StoredDeviceGroup processingExceptionGroup = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    
    public DeviceGroupAddingBulkProcessorCallback(StoredDeviceGroup successGroup, StoredDeviceGroup processingExceptionGroup, DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.successGroup = successGroup;
        this.processingExceptionGroup = processingExceptionGroup;
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    
    // ADD DEVICE TO GROUP
    @Override
    public void receivedProcessingException(int rowNumber, YukonDevice object, ProcessingException e) {
        
        super.receivedProcessingException(rowNumber, object, e);
        deviceGroupMemberEditorDao.addDevices(processingExceptionGroup, object);
    }

    @Override
    public void processedObject(int rowNumber, YukonDevice object) {
        
        super.processedObject(rowNumber, object);
        deviceGroupMemberEditorDao.addDevices(successGroup, object);
    }

}
