package com.cannontech.common.bulk;

import com.cannontech.common.bulk.processor.ProcessorCallbackException;


/**
 * Interface whose methods are called as a bulk processing operation processes
 */
public interface BulkProcessorCallback<I,O> {

    
    /**
     * Method called when an object mapping exception occurs during processing
     * @param rowNumber 
     * @param e - Exception that occured
     */
    //public void receivedObjectMappingException(int rowNumber, ObjectMappingException e);

    /**
     * Method called when a processing exception occurs during processing
     * @param e - Exception that occured
     */
    public void receivedProcessingException(int rowNumber, I object, ProcessorCallbackException e);

    /**
     * Method called each time an object has been processed - is called even if
     * there was an exception
     */
    public void processedObject(int rowNumber, O out);

    /**
     * Method called when object processing is complete
     */
    public void processingSucceeded();

    /**
     * Method called when object processing fails
     */
    public void processingFailed(Exception e);

}