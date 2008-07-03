package com.cannontech.common.bulk;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class DeviceGroupAddingBulkProcessorCallback<I> extends CollectingBulkProcessorCallback<I,YukonDevice> {
    
    protected StoredDeviceGroup successGroup = null;
    protected StoredDeviceGroup processingExceptionGroup = null;
    protected DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    
    public DeviceGroupAddingBulkProcessorCallback(StoredDeviceGroup successGroup, StoredDeviceGroup processingExceptionGroup, DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.successGroup = successGroup;
        this.processingExceptionGroup = processingExceptionGroup;
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    
    @Override
    public void processedObject(int rowNumber, YukonDevice object) {
        
        super.processedObject(rowNumber, object);
        deviceGroupMemberEditorDao.addDevices(successGroup, object);
    }

}
