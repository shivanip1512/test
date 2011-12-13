package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.google.common.collect.Lists;

public class TranslationImportCallbackResult extends CollectingBulkProcessorCallback<String[], String[]>  implements BackgroundProcessResultHolder {
    private BackgroundProcessTypeEnum backgroundProcessType;
    private String resultsId = "";
    private int totalItems = 0;
    
    private List<String> headers = Lists.newArrayList();
    private List<String[]> importRows = Lists.newArrayList();
    private List<Integer> failedRows = Lists.newArrayList();
    private List<String> log = Lists.newArrayList();;
    private int logIndex = 0;
    private MessageSourceAccessor messageSourceAccessor;
    
    public TranslationImportCallbackResult(String resultsId, List<String> headers, List<String[]> importRows, MessageSourceAccessor messageSourceAccessor) {
        this(BackgroundProcessTypeEnum.IMPORT_FDR_TRANSLATION, resultsId, importRows.size(), messageSourceAccessor);
        this.headers = headers;
        this.importRows = importRows;
        this.backgroundProcessType = BackgroundProcessTypeEnum.IMPORT_FDR_TRANSLATION;
        this.messageSourceAccessor = messageSourceAccessor;
    }
    
    public TranslationImportCallbackResult(BackgroundProcessTypeEnum backgroundProcessType, String resultsId, int totalItems, MessageSourceAccessor messageSourceAccessor) {
        this.backgroundProcessType = backgroundProcessType;
        this.resultsId = resultsId;
        this.totalItems = totalItems;
    }
    
    public List<String> getHeaders() {
        return headers;
    }
    
    public List<Integer> getFailedRowNumbers() {
        return failedRows;
    }
    
    public List<String> getLog() {
        return log;
    }
    
    public void setBackgroundProcessType(BackgroundProcessTypeEnum backgroundProcessType) {
        this.backgroundProcessType = backgroundProcessType;
    }
    public BackgroundProcessTypeEnum getBackgroundProcessType() {
        return backgroundProcessType;
    }
    
    public void setResultsId(String resultsId) {
        this.resultsId = resultsId;
    }
    public String getResultsId() {
        return resultsId;
    }
    
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
    public int getTotalItems() {
        return totalItems;
    }
    
    @Override
    public void processedObject(int rowNumber, String[] row) {
        super.processedObject(rowNumber, row);
        String logString = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.operationSuccessful", rowNumber+1);
        log.add(logString);
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, String[] row, ProcessorCallbackException exception) {
        super.receivedProcessingException(rowNumber, row, exception);
        String logString = messageSourceAccessor.getMessage("yukon.web.modules.amr.fdrTranslationManagement.operationFailed", rowNumber+1, exception.getMessage());
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
    
    /**
     * Resets the log index to zero, so getNextLogLine() and
     * getNewLogLines() will return values starting at the beginning
     * of the list next time they are called.
     */
    public boolean resetLog() {
        logIndex = 0;
        return true;
    }
    
    /**
     * Returns a single log line per call, remembering the current 
     * position in the log for subsequent calls. Call resetLog() to reset
     * the index and begin returning lines from the beginning of the log
     * again. 
     */
    public String getNextLogLine() {
        String logLine = null;
        try {
            logLine = log.get(logIndex);
            logIndex++;
        } catch(IndexOutOfBoundsException e) {
            //index is past the last line. return null
        }
        
        return logLine;
    }
    
    /**
     * Returns all unread log lines in a List, based on the position
     * of the log index. Call resetLog() to reset the index and begin
     * returning lines from the beginning of the log again.
     */
    public List<String> getNewLogLines() {
        List<String> logLines = Lists.newArrayList();
        String logLine = getNextLogLine();
        while(logLine != null) {
            logLines.add(logLine);
            logLine = getNextLogLine();
        }
        return logLines;
    }
    
    public int getLogIndex() {
        return logIndex;
    }
    
    public List<String[]> getImportRows() {
        return importRows;
    }
}
