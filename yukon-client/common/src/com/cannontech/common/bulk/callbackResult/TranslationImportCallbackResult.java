package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.google.common.collect.Lists;

public class TranslationImportCallbackResult extends CollectingBulkProcessorCallback<String[], String[]>  implements BackgroundProcessResultHolder {
    protected BackgroundProcessTypeEnum backgroundProcessType;
    protected String resultsId = "";
    protected int totalItems = 0;
    
    private List<String> headers = Lists.newArrayList();
    private List<String[]> importRows = Lists.newArrayList();
    private List<Integer> failedRows = Lists.newArrayList();
    private List<String> log = Lists.newArrayList();;
    private int logIndex = 0;
    
    public TranslationImportCallbackResult(BackgroundProcessTypeEnum backgroundProcessType, String resultsId, int totalItems) {
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
    
    //////////
    
    public TranslationImportCallbackResult(String resultsId, List<String> headers, List<String[]> importRows) {
        this(BackgroundProcessTypeEnum.IMPORT_FDR_TRANSLATION, resultsId, importRows.size());
        this.headers = headers;
        this.importRows = importRows;
        this.backgroundProcessType = BackgroundProcessTypeEnum.IMPORT_FDR_TRANSLATION;
    }
    
    @Override
    public void processedObject(int rowNumber, String[] row) {
        super.processedObject(rowNumber, row);
        //TODO: improve, i18n
        log.add("Operation successful.");
    }
    
    @Override
    public void receivedProcessingException(int rowNumber, String[] row, ProcessorCallbackException exception) {
        super.receivedProcessingException(rowNumber, row, exception);
        //TODO improve, i18n
        String logString = "Operation failed. " + exception.getMessage();
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
    
    public boolean resetLog() {
        logIndex = 0;
        return true;
    }
    
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
    
    public List<String[]> getImportRows() {
        return importRows;
    }
}
