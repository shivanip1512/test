package com.cannontech.common.bulk.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.BulkProcessorCallback;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

/**
 * Bulk Processor implementation which processes items one at a time
 */
public class TransactionPerItemProcessor extends RunnableBasedBulkProcessor implements BulkProcessor {
    private Logger log = YukonLogManager.getLogger(TransactionPerItemProcessor.class);
    private TransactionTemplate transactionTemplate;
    
    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
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
    protected <I, O> Runnable getBulkProcessorRunnable(
            final Iterator<I> iterator, final ObjectMapper<I, O> mapper,
            final Processor<O> processor, final BulkProcessorCallback<O> callback) {

        return new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                try {
                    int rowNumber = 0;
                    while (iterator.hasNext()) {
                        final int thisRow = rowNumber++;
                        final I in = iterator.next();
                        O out = null;
                        try {
                            out = (O) transactionTemplate.execute(new TransactionCallback() {
                                public Object doInTransaction(TransactionStatus status) {
                                    O out = mapper.map(in);
                                    processor.process(out);
                                    callback.processedObject(thisRow, out);
                                    return out;
                                }
                            });

                        } catch (ObjectMappingException e) {
                            callback.receivedObjectMappingException(thisRow, e);
                        } catch (ProcessingException e) {
                            out = mapper.map(in);
                            callback.receivedProcessingException(thisRow, out, e);
                        }
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
