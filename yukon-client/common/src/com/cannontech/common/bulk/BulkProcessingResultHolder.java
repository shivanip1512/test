package com.cannontech.common.bulk;

import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;

/**
 * Interface which allows access to the results of a bulk processing operation
 */
public interface BulkProcessingResultHolder<T> {

    // EXCEPTION LISTS
    public List<ObjectMappingException> getMappingExceptionList();
    public List<ProcessingException> getProcessingExceptionList();
    
    // EXCEPTION ROW NUMBER MAPS
    public Map<Integer, ObjectMappingException> getMappingExceptionRowNumberMap();
    public Map<Integer, ProcessingException> getProcessingExceptionRowNumberMap();

    
    // COUNT GETTERS
    public int getSuccessCount();
    public int getProcessingExceptionCount();
    public int getMappingExceptionCount();
    

    // STATUS
    public boolean isComplete();
    public boolean isSuccessfull();

    public boolean isProcessingFailed();

    public Exception getFailedException();

    public String getFailedMessage();
    
    
    // SUCCESS AND FAIL (processing) OBJECTS
    public List<T> getSuccessObjects();
    public List<T> getProcesingExceptionObjects();

}