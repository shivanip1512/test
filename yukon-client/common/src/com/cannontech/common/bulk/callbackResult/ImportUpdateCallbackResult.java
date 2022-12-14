package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.bulk.service.BulkFileInfo;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.ToolsEventLogService;

public class ImportUpdateCallbackResult extends BackgroundProcessBulkProcessorCallback<String[]>
        implements BulkFieldBackgroupProcessResultHolder {

    private List<BulkFieldColumnHeader> bulkFieldColumnHeaders;
    private BulkFileInfo bulkFileInfo;
    private StoredDeviceGroup successGroup;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private ToolsEventLogService toolsEventLogService;

    public ImportUpdateCallbackResult(BackgroundProcessTypeEnum backgroundProcessType,
            List<BulkFieldColumnHeader> bulkFieldColumnHeaders, BulkFileInfo bulkFileInfo, String resultsId,
            StoredDeviceGroup successGroup, DeviceGroupMemberEditorDao deviceGroupMemberEditorDao,
            DeviceGroupCollectionHelper deviceGroupCollectionHelper, ToolsEventLogService toolsEventLogService) {

        super(backgroundProcessType, resultsId, bulkFileInfo.getDataCount());

        this.bulkFieldColumnHeaders = bulkFieldColumnHeaders;
        this.bulkFileInfo = bulkFileInfo;
        this.successGroup = successGroup;
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
        this.toolsEventLogService = toolsEventLogService;
    }

    public BulkFileInfo getBulkFileInfo() {
        return bulkFileInfo;
    }

    // callback overrides
    @Override
    public void processedObject(int rowNumber, SimpleDevice object) {
        super.processedObject(rowNumber, object);
        deviceGroupMemberEditorDao.addDevices(successGroup, object);
    }

    @Override
    public void receivedProcessingException(int rowNumber, String[] object, ProcessorCallbackException e) {
        super.receivedProcessingException(rowNumber, object, e);
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
        return false;
    }

    @Override
    public DeviceCollection getFailureDeviceCollection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFailureReasonsListSupported() {
        return true;
    }

    @Override
    public boolean isFailureFileSupported() {
        return true;
    }

    @Override
    public List<BulkFieldColumnHeader> getBulkFieldColumnHeaders() {
        return bulkFieldColumnHeaders;
    }

    @Override
    public void processingSucceeded() {
        super.processingSucceeded();
        toolsEventLogService.importCompleted(bulkFileInfo.getImportType(), bulkFileInfo.getOriginalFilename(),
            getSuccessCount(), getTotalItems() - getSuccessCount());
    }

    @Override
    public void processingFailed(Exception e) {
        super.processingFailed(e);
        toolsEventLogService.importCompleted(bulkFileInfo.getImportType(), bulkFileInfo.getOriginalFilename(),
            getSuccessCount(), getTotalItems() - getSuccessCount());
    }
}