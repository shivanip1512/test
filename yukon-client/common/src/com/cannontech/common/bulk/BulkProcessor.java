package com.cannontech.common.bulk;

import java.util.Iterator;

import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

/**
 * Interface to initiate bulk processing
 */
public interface BulkProcessor {

    /**
     * Method to bulk process objects. This method will block until all bulk
     * processing is completed.
     * @param <I> - Type of object to take as input
     * @param iterator - Iterator of input objects
     * @param processor - Processor to process objects
     * @return A bulk processing results object
     */
    public <I> BulkProcessingResultHolder<I,I> bulkProcess(Iterator<I> iterator,
            Processor<I> processor);

    /**
     * Method to bulk process objects. This method will block until all bulk
     * processing is completed.
     * @param <I> - Type of object to take as input
     * @param <O> - Type of object to convert input type to and process
     * @param iterator - Iterator of input objects
     * @param mapper - Mapper used to convert input type to process type
     * @param processor - Processor to process objects
     * @return A bulk processing results object
     */
    public <I, O> BulkProcessingResultHolder<I,O> bulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, Processor<O> processor);
    
    public <I, O> void bulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, 
            Processor<O> processor,
            BulkProcessorCallback<I,O> callback);

    /**
     * Method to bulk process objects. This method will return immediately and
     * the bulk processing will run in the background.
     * @param <I> - Type of object to take as input
     * @param iterator - Iterator of input objects
     * @param processor - Processor to process objects
     * @param callback - Callback that will be called as the bulk processing
     *            runs in the background
     */
    public <I> void backgroundBulkProcess(Iterator<I> iterator,
            Processor<I> processor, BulkProcessorCallback<I,I> callback);

    /**
     * Method to bulk process objects. This method will return immediately and
     * the bulk processing will run in the background.
     * @param <I> - Type of object to take as input
     * @param <O> - Type of object to convert input type to and process
     * @param iterator - Iterator of input objects
     * @param mapper - Mapper used to convert input type to process type
     * @param processor - Processor to process objects
     * @param callback - Callback that will be called as the bulk processing
     *            runs in the background
     */
    public <I, O> void backgroundBulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, Processor<O> processor,
            BulkProcessorCallback<I,O> callback);

}
