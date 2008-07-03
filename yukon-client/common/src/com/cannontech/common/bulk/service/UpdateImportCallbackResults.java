package com.cannontech.common.bulk.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class UpdateImportCallbackResults extends BulkOperationCallbackResults<String[]> {

    public UpdateImportCallbackResults(StoredDeviceGroup successGroup, StoredDeviceGroup processingExceptionGroup, DeviceGroupMemberEditorDao deviceGroupMemberEditorDao, DeviceGroupCollectionHelper deviceGroupCollectionHelper, List<BulkFieldColumnHeader> bulkFieldColumnHeaders, BulkOperationTypeEnum bulkOperationType) {
        
        super(successGroup, processingExceptionGroup, deviceGroupMemberEditorDao, deviceGroupCollectionHelper, bulkFieldColumnHeaders, bulkOperationType);
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
        this.startTime = new Date();
        this.bulkFieldColumnHeaders = bulkFieldColumnHeaders;
        this.bulkOperationType = bulkOperationType;
    }
}
