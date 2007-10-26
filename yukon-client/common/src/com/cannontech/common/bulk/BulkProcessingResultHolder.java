package com.cannontech.common.bulk;

import java.util.List;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;

/**
 * Interface which allows access to the results of a bulk processing operation
 */
public interface BulkProcessingResultHolder {

    public List<ObjectMappingException> getMappingExceptionList();

    public List<ProcessingException> getProcessingExceptionList();

    /**
     * Method to get the total number of objects processed regardless of
     * processing or mapping errors
     * @return Total number of objects processed
     */
    public int getTotalObjectsProcessedCount();

    /**
     * Method to get the total number of objects processed excluding objects
     * that had processing or mapping errors
     * @return Number of objects successfully processed
     */
    public int getSuccessfulObjectsProcessedCount();

    /**
     * Method to get the total number of objects that were unsuccessfully
     * processed because of processing or mapping errors
     * @return Number of objects unsuccessfully processed
     */
    public int getUnsuccessfulObjectsProcessedCount();

    public boolean isComplete();

    public boolean isProcessingFailed();

    public Exception getFailedException();

    public String getFailedMessage();

}