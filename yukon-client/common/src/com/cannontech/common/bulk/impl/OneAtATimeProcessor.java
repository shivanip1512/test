package com.cannontech.common.bulk.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BulkProcessorCallback;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

/**
 * Bulk Processor implementation which processes items one at a time
 */
public class OneAtATimeProcessor extends RunnableBasedBulkProcessor implements BulkProcessor {

    private Logger log = YukonLogManager.getLogger(OneAtATimeProcessor.class);



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
    protected <I, O> Runnable getBulkProcessorRunnable(
            final Iterator<I> iterator, final ObjectMapper<I, O> mapper,
            final Processor<? super O> processor, final BulkProcessorCallback<I,O> callback) {

        return new Runnable() {
            public void run() {
                try {
                    int rowNumber = 0;
                    while (iterator.hasNext()) {

                        I in = iterator.next();
                        O out = null;
                        
                        try {
                            out = mapper.map(in);
                            processor.process(out);
                            callback.processedObject(rowNumber, out);

                        } catch (ObjectMappingException e) {
                            log.info("mapping exception, row " + rowNumber + ": " + in, e);
                            callback.receivedProcessingException(rowNumber, in, e);
                        } catch (ProcessingException e) {
                            log.info("processing exception, row " + rowNumber + ": " + in, e);
                            callback.receivedProcessingException(rowNumber, in, e);
                        }
                        
                        rowNumber++;
                    }
                    callback.processingSucceeded();
                } catch (Exception e) {
                    log.warn("Bulk Processing failed", e);
                    callback.processingFailed(e);
                }
                finally {
                    if (iterator instanceof Closeable) {
                        try {
                            ((Closeable)iterator).close();
                        } catch (IOException e) {
                            ;
                        }
                    }
                }
            }
        };
    }

}
