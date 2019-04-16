package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.google.common.collect.Lists;

public class TranslationImportCallbackResult extends LoggingCallbackResult<String[], String[]> {
    private BackgroundProcessTypeEnum backgroundProcessType;
    private String resultsId = "";
    
    private List<String> headers = Lists.newArrayList();
    private List<String[]> importRows = Lists.newArrayList();
    private List<Integer> failedRows = Lists.newArrayList();
    private MessageSourceAccessor messageSourceAccessor;
    private ToolsEventLogService toolsEventLogService;
    private String originalFileName;
    
    public TranslationImportCallbackResult(String resultsId, List<String> headers, List<String[]> importRows,
            MessageSourceAccessor messageSourceAccessor, ToolsEventLogService toolsEventLogService,
            String originalFileName) {
        this.headers = headers;
        this.importRows = importRows;
        this.backgroundProcessType = BackgroundProcessTypeEnum.IMPORT_FDR_TRANSLATION;
        this.messageSourceAccessor = messageSourceAccessor;
        this.resultsId = resultsId;
        this.toolsEventLogService = toolsEventLogService;
        this.originalFileName = originalFileName;
    }
    
    public String getResultsId() {
        return resultsId;
    }
    
    public List<String> getHeaders() {
        return headers;
    }
    
    public List<Integer> getFailedRowNumbers() {
        return failedRows;
    }
    
    public BackgroundProcessTypeEnum getBackgroundProcessType() {
        return backgroundProcessType;
    }
    
    public int getTotalItems() {
        return importRows.size();
    }
    
    @Override
    public void processedObject(int rowNumber, String[] row) {
        super.processedObject(rowNumber, row);
        String logString = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.operationSuccessful", rowNumber+1);
        log.add(logString);
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, String[] row, ProcessorCallbackException exception) {
        super.receivedProcessingException(rowNumber, row, exception);
        String logString = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.fdrTranslationManagement.operationFailed", rowNumber+1, exception.getMessage());
        log.add(logString);
        failedRows.add(rowNumber);
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
    
    public List<String[]> getImportRows() {
        return importRows;
    }

    @Override
    public void processingSucceeded() {
        super.processingSucceeded();
        String logString = messageSourceAccessor.getMessage("yukon.web.menu.vv.fdrTranslations");
        toolsEventLogService.importCompleted(logString, originalFileName, getSuccessCount(),
            getTotalItems() - getSuccessCount());
    }

    @Override
    public void processingFailed(Exception e) {
        super.processingFailed(e);
        String logString = messageSourceAccessor.getMessage("yukon.web.menu.vv.fdrTranslations");
        toolsEventLogService.importCompleted(logString, originalFileName, getSuccessCount(),
            getTotalItems() - getSuccessCount());
    }
}
