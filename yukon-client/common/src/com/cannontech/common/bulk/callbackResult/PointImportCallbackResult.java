package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.google.common.collect.Lists;

/**
 * Combined callback and results object for point import background processes.
 */
public class PointImportCallbackResult extends LoggingCallbackResult<ImportRow, ImportRow> {
    private BackgroundProcessTypeEnum backgroundProcessType = BackgroundProcessTypeEnum.IMPORT_POINT;
    private MessageSourceAccessor messageSourceAccessor;
    private String resultsId = "";
    private ImportData data;
    private List<Integer> failedRows = Lists.newArrayList();
    private ToolsEventLogService toolsEventLogService;
    
    public PointImportCallbackResult(String resultsId, ImportData data, MessageSourceAccessor messageSourceAccessor,
            ToolsEventLogService toolsEventLogService) {
        this.resultsId = resultsId;
        this.data = data;
        this.messageSourceAccessor = messageSourceAccessor;
        this.toolsEventLogService = toolsEventLogService;
    }
    
    @Override
    public void processedObject(int rowNumber, ImportRow row) {
        super.processedObject(rowNumber, row);
        String logString = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.pointImport.operationSuccessful", rowNumber+1);
        log.add(logString);
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, ImportRow row, ProcessorCallbackException exception) {
        super.receivedProcessingException(rowNumber, row, exception);
        String logString = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.pointImport.operationFailed", rowNumber+1, exception.getMessage());
        log.add(logString);
        failedRows.add(rowNumber);
    }
    
    public ImportData getImportData() {
        return data;
    }
    
    public List<Integer> getFailedRowNumbers() {
        return failedRows;
    }
    
    public String getResultsId() {
        return resultsId;
    }
    
    @Override
    public BackgroundProcessTypeEnum getBackgroundProcessType() {
        return backgroundProcessType;
    }

    @Override
    public int getTotalItems() {
        return data.size();
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
        return false;
    }

    @Override
    public boolean isFailureFileSupported() {
        return false;
    }

    @Override
    public void processingSucceeded() {
        super.processingSucceeded();
        toolsEventLogService.importCompleted(data.getImportType(), data.getOriginalFileName(), getSuccessCount(),
            getTotalItems() - getSuccessCount());
    }

    @Override
    public void processingFailed(Exception e) {
        super.processingFailed(e);
        toolsEventLogService.importCompleted(data.getImportType(), data.getOriginalFileName(), getSuccessCount(),
            getTotalItems() - getSuccessCount());
    }
}
