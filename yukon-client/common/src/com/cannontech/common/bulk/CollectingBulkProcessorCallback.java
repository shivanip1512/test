package com.cannontech.common.bulk;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;

/**
 * Class which serves as a callback and result holder for bulk processing
 */
public class CollectingBulkProcessorCallback implements BulkProcessorCallback,
        BulkProcessingResultHolder {

    private int resultsProcessed = 0;
    private boolean complete = false;
    private Exception failedException = null;

    private List<ObjectMappingException> mappingExceptionList = new ArrayList<ObjectMappingException>();
    private List<ProcessingException> processingExceptionList = new ArrayList<ProcessingException>();

    public List<ObjectMappingException> getMappingExceptionList() {
        return mappingExceptionList;
    }

    public void setMappingExceptionList(List<ObjectMappingException> mappingExceptionList) {
        this.mappingExceptionList = mappingExceptionList;
    }

    public List<ProcessingException> getProcessingExceptionList() {
        return processingExceptionList;
    }

    public void setProcessingExceptionList(List<ProcessingException> processingExceptionList) {
        this.processingExceptionList = processingExceptionList;
    }

    public void receivedObjectMappingException(ObjectMappingException e) {
        this.mappingExceptionList.add(e);
    }

    public void receivedProcessingException(ProcessingException e) {
        this.processingExceptionList.add(e);
    }

    public void processedObject() {
        this.resultsProcessed++;
    }

    public int getTotalObjectsProcessedCount() {
        return this.resultsProcessed;
    }

    public int getSuccessfulObjectsProcessedCount() {
        return this.resultsProcessed - this.mappingExceptionList.size() - this.processingExceptionList.size();
    }

    public int getUnsuccessfulObjectsProcessedCount() {
        return this.mappingExceptionList.size() + this.processingExceptionList.size();
    }

    public void processingComplete() {
        this.complete = true;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public boolean isProcessingFailed() {
        return failedException != null;
    }

    public void processingFailed(Exception e) {
        failedException = e;
    }

    public Exception getFailedException() {
        return failedException;
    }

    public String getFailedMessage() {
        return failedException.getMessage();
    }

}
