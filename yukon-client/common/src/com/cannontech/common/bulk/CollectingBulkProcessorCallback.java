package com.cannontech.common.bulk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;

/**
 * Class which serves as a callback and result holder for bulk processing
 */
public class CollectingBulkProcessorCallback<T> implements BulkProcessorCallback<T>, BulkProcessingResultHolder<T> {

    private boolean complete = false;
    private Exception failedException = null;

    // map of row number to success object
    private Map<Integer, T> successObjectRowNumberMap = new LinkedHashMap<Integer, T>();
    
    // maps of row number to exception
    private Map<Integer, ObjectMappingException> mappingExceptionRowNumberMap = new LinkedHashMap<Integer, ObjectMappingException>();
    private Map<Integer, ProcessingException> processingExceptionRowNumberMap = new LinkedHashMap<Integer, ProcessingException>();
    
    // map of row number to the object that failed to process
    private Map<Integer, T> processingExceptionObjectRowNumberMap = new HashMap<Integer, T>();
    
    
    // CALL BACK METHODS
    //----------------------------------------------------------------------------------------------
    public void receivedObjectMappingException(int rowNumber, ObjectMappingException e) {
        
        this.mappingExceptionRowNumberMap.put(rowNumber, e);
    }

    public void receivedProcessingException(int rowNumber, T object, ProcessingException e) {
        
        this.processingExceptionRowNumberMap.put(rowNumber, e);
        this.processingExceptionObjectRowNumberMap.put(rowNumber, object);
    }

    public void processedObject(int rowNumber, T object) {
        
        this.successObjectRowNumberMap.put(rowNumber, object);
    }
    
    public void processingSucceeded() {
        this.complete = true;
    }
    
    public boolean isSuccessfull() {
        return this.complete && this.failedException == null;
    }
    
    // is processing completely blows up, this is the exception
    public void processingFailed(Exception e) {
        this.complete = true;
        failedException = e;
    }
    
    
    
    // RESULTS HOLDER METHODS
    //----------------------------------------------------------------------------------------------
    
    // EXCEPTION LISTS
    public List<ObjectMappingException> getMappingExceptionList() {
        return new ArrayList<ObjectMappingException>(this.mappingExceptionRowNumberMap.values());
    }
    
    public List<ProcessingException> getProcessingExceptionList() {
        return new ArrayList<ProcessingException>(this.processingExceptionRowNumberMap.values());
    }
    
    
    // EXCEPTION ROW NUMBER MAPS
    public Map<Integer, ObjectMappingException> getMappingExceptionRowNumberMap() {
        return this.mappingExceptionRowNumberMap;
    }
    
    public Map<Integer, ProcessingException> getProcessingExceptionRowNumberMap() {
        return this.processingExceptionRowNumberMap;
    }

    
    // COUNT GETTERS
    public int getSuccessCount() {
        return this.successObjectRowNumberMap.size();
    }
    
    public int getProcessingExceptionCount() {
        return this.processingExceptionRowNumberMap.size();
    }
    
    public int getMappingExceptionCount() {
        return this.mappingExceptionRowNumberMap.size();
    }

    
    // STATUS
    public boolean isComplete() {
        return this.complete;
    }

    public boolean isProcessingFailed() {
        return failedException != null;
    }

    public Exception getFailedException() {
        return failedException;
    }

    public String getFailedMessage() {
        return failedException.getMessage();
    }

    
    // SUCCESS AND FAIL (processing) OBJECTS
    public List<T> getSuccessObjects() {
        return new ArrayList<T>(this.successObjectRowNumberMap.values());
    }

    public List<T> getProcesingExceptionObjects() {
        return new ArrayList<T>(this.processingExceptionObjectRowNumberMap.values());
    }

}
