package com.cannontech.common.bulk;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;

/**
 * Interface whose methods are called as a bulk processing operation processes
 */
public interface BulkProcessorCallback {

    /**
     * Method called when an object mapping exception occurs during processing
     * @param e - Exception that occured
     */
    public void receivedObjectMappingException(ObjectMappingException e);

    /**
     * Method called when a processing exception occurs during processing
     * @param e - Exception that occured
     */
    public void receivedProcessingException(ProcessingException e);

    /**
     * Method called each time an object has been processed - is called even if
     * there was an exception
     */
    public void processedObject();

    /**
     * Method called when object processing is complete
     */
    public void processingComplete();

    /**
     * Method called when object processing fails
     */
    public void processingFailed(Exception e);

}