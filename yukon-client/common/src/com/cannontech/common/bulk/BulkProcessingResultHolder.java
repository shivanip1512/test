package com.cannontech.common.bulk;

import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.processor.ProcessorCallbackException;

/**
 * Interface which allows access to the results of a bulk processing operation
 */
public interface BulkProcessingResultHolder<I,O> {

    // EXCEPTION LISTS
    public List<ProcessorCallbackException> getProcessingExceptionList();
    
    // EXCEPTION ROW NUMBER MAPS
    public Map<Integer, ProcessorCallbackException> getProcessingExceptionRowNumberMap();

    
    // COUNT GETTERS
    public int getSuccessCount();
    public int getProcessingExceptionCount();
    

    // STATUS
    public boolean isComplete();
    public boolean isSuccessfull();

    public boolean isProcessingFailed();

    public Exception getFailedException();

    public String getFailedMessage();
    
    
    // SUCCESS AND FAIL (processing) OBJECTS
    public List<O> getSuccessObjects();
    public List<I> getProcesingExceptionObjects();

}