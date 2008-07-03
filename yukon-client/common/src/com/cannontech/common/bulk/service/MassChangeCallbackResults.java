package com.cannontech.common.bulk.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class MassChangeCallbackResults extends BulkOperationCallbackResults<YukonDevice> {

    public MassChangeCallbackResults(StoredDeviceGroup successGroup, StoredDeviceGroup processingExceptionGroup, DeviceGroupMemberEditorDao deviceGroupMemberEditorDao, DeviceGroupCollectionHelper deviceGroupCollectionHelper, List<BulkFieldColumnHeader> bulkFieldColumnHeaders, BulkOperationTypeEnum bulkOperationType) {

        super(successGroup, processingExceptionGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper, bulkFieldColumnHeaders, bulkOperationType);
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
        this.startTime = new Date();
        this.bulkFieldColumnHeaders = bulkFieldColumnHeaders;
        this.bulkOperationType = bulkOperationType;
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, YukonDevice object, ProcessorCallbackException e) {
        
        super.receivedProcessingException(rowNumber, object, e);
        deviceGroupMemberEditorDao.addDevices(processingExceptionGroup, object);
    }
}
