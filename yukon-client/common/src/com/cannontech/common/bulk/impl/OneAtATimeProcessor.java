package com.cannontech.common.bulk.impl;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessingResultHolder;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.BulkProcessorCallback;
import com.cannontech.common.bulk.CollectingBulkProcessorCallback;
import com.cannontech.common.bulk.mapper.IgnoreMappingException;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.ScheduledExecutor;

/**
 * Bulk Processor implementation which processes items one at a time
 */
public class OneAtATimeProcessor implements BulkProcessor {

    private Logger log = YukonLogManager.getLogger(OneAtATimeProcessor.class);

    private ScheduledExecutor scheduledExecutor = null;

    public void setScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }

    public <I, O> BulkProcessingResultHolder bulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, Processor<O> processor) {

        CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();

        // Get the bulk processor runnable and run it in this thread (will
        // block)
        Runnable runnable = getBulkProcessorRunnable(iterator, mapper, processor, callback);
        runnable.run();

        return callback;
    }

    public <I, O> void backgroundBulkProcess(Iterator<I> iterator, ObjectMapper<I, O> mapper,
            Processor<O> processor, BulkProcessorCallback callback) {

        // Get the bulk processor runnable and ask the ScheduledExecutor to run
        // it in the background
        Runnable runnable = getBulkProcessorRunnable(iterator, mapper, processor, callback);
        scheduledExecutor.execute(runnable);

    }

    /**
     * Helper method to get a runnable that will do bulk processing
     * @param <I> - Type of object to take as input
     * @param <O> - Type of object to convert input type to and process
     * @param iterator - Iterator of input objects
     * @param mapper - Mapper used to convert input type to process type
     * @param processor - Processor to process objects
     * @param callback - Callback that will be called as the bulk processing
     *            runs
     * @return The runnable
     */
    private <I, O> Runnable getBulkProcessorRunnable(final Iterator<I> iterator,
            final ObjectMapper<I, O> mapper, final Processor<O> processor,
            final BulkProcessorCallback callback) {

        return new Runnable() {

            public void run() {

                try {
                    while (iterator.hasNext()) {
                        try {

                            I in = iterator.next();
                            O out = mapper.map(in);
                            processor.process(out);
                            callback.processedObject();

                        } catch (IgnoreMappingException e) {
                            // do nothing - ignore this object
                        } catch (ObjectMappingException e) {
                            callback.receivedObjectMappingException(e);
                            callback.processedObject();
                        } catch (ProcessingException e) {
                            callback.receivedProcessingException(e);
                            callback.processedObject();
                        }
                    }
                    callback.processingComplete();
                } catch (Exception e) {
                    log.warn("Bulk Processing failed", e);
                    callback.processingFailed(e);
                    callback.processingComplete();
                }
            }
        };
    }

}
