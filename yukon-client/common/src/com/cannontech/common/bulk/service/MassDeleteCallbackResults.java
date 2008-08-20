package com.cannontech.common.bulk.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class MassDeleteCallbackResults extends MassChangeCallbackResults {

    private long initialDeviceCount = 0;
    
    public MassDeleteCallbackResults(StoredDeviceGroup successGroup, StoredDeviceGroup processingExceptionGroup, DeviceGroupMemberEditorDao deviceGroupMemberEditorDao, DeviceGroupCollectionHelper deviceGroupCollectionHelper, List<BulkFieldColumnHeader> bulkFieldColumnHeaders, BulkOperationTypeEnum bulkOperationType, long initialDeviceCount) {

        super(successGroup, processingExceptionGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper, bulkFieldColumnHeaders, bulkOperationType);
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
        this.startTime = new Date();
        this.bulkFieldColumnHeaders = bulkFieldColumnHeaders;
        this.bulkOperationType = bulkOperationType;
        this.initialDeviceCount = initialDeviceCount;
    }
    
    
    // Mass deletes won't be able to add devices to a group on success because they are gone!
    // So we'll need to override that default behavior, however, we still want to add the 
    // 'dead' devices to the success map because it is used to count progress
    @Override
    public void processedObject(int rowNumber, YukonDevice object) {
        this.successObjectRowNumberMap.put(rowNumber, object);
    }
    
    public long getInitialDeviceCount() {
        return initialDeviceCount;
    }
}