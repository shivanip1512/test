package com.cannontech.common.bulk.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.DeviceGroupAddingBulkProcessorCallback;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.TimedOperation;

public class BulkOperationCallbackResults<I> extends DeviceGroupAddingBulkProcessorCallback<I> implements Completable, TimedOperation {

    protected DeviceGroupCollectionHelper deviceGroupCollectionHelper = null;
    protected List<BulkFieldColumnHeader> bulkFieldColumnHeaders = null;

    private BulkFileInfo bulkFileInfo = null;
    private String resultsId = "";
    
    protected Date startTime = null;
    protected Date stopTime = null;
    
    protected BulkOperationTypeEnum bulkOperationType = null;
    
    public BulkOperationCallbackResults(StoredDeviceGroup successGroup, StoredDeviceGroup processingExceptionGroup, DeviceGroupMemberEditorDao deviceGroupMemberEditorDao, DeviceGroupCollectionHelper deviceGroupCollectionHelper, List<BulkFieldColumnHeader> bulkFieldColumnHeaders, BulkOperationTypeEnum bulkOperationType) {
        
        super(successGroup, processingExceptionGroup, deviceGroupMemberEditorDao);
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
        this.startTime = new Date();
        this.bulkFieldColumnHeaders = bulkFieldColumnHeaders;
        this.bulkOperationType = bulkOperationType;
    }
    
    public DeviceCollection getSuccessDeviceCollection() {
        return deviceGroupCollectionHelper.buildDeviceCollection(this.successGroup);
    }
    
    public DeviceCollection getProcessingExceptionDeviceCollection() {
        return deviceGroupCollectionHelper.buildDeviceCollection(this.processingExceptionGroup);
    }
    
    // FIELDS
    public void setBulkFieldColumnHeaders(
            List<BulkFieldColumnHeader> bulkFieldColumnHeaders) {
        this.bulkFieldColumnHeaders = bulkFieldColumnHeaders;
    }
    
    public List<BulkFieldColumnHeader> getBulkFieldColumnHeaders() {
        return bulkFieldColumnHeaders;
    }
    
    public BulkOperationTypeEnum getBulkOperationType() {
        return bulkOperationType;
    }
    
    public boolean isImport() {
        return this.bulkOperationType.equals(BulkOperationTypeEnum.IMPORT);
    }
    
    public boolean isUpdate() {
        return this.bulkOperationType.equals(BulkOperationTypeEnum.UPDATE);
    }
    
    public boolean isMassChange() {
        return this.bulkOperationType.equals(BulkOperationTypeEnum.MASS_CHANGE);
    }
    
    public boolean isMassDelete() {
        return this.bulkOperationType.equals(BulkOperationTypeEnum.MASS_DELETE);
    }
    
    public boolean isChangeDeviceType() {
        return this.bulkOperationType.equals(BulkOperationTypeEnum.CHANGE_DEVICE_TYPE);
    }
    
    // TIME
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
    
    public void setResultsId(String resultsId) {
        this.resultsId = resultsId;
    }
    public String getResultsId() {
        return resultsId;
    }
    
    public void setBulkFileInfo(BulkFileInfo bulkFileInfo) {
        this.bulkFileInfo = bulkFileInfo;
    }
    public BulkFileInfo getBulkFileInfo() {
        return bulkFileInfo;
    }
    
    // DeviceGroupAddingBulkProcessorCallback
    @Override
    public void processingSucceeded() {
        this.stopTime = new Date();
        super.processingSucceeded();
    }
    
    @Override
    public void processingFailed(Exception e) {
        this.stopTime = new Date();
        super.processingFailed(e);
    }
    
    
    
    
}
