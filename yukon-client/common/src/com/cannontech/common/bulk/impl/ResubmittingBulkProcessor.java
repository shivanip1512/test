package com.cannontech.common.bulk.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessorCallback;
import com.cannontech.common.bulk.DelayThrottleCalculator;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.ScheduledExecutor;

/**
 * This BulkProcessor makes use of the "globalScheduledExecutor" to do its processing. 
 * Each row will be processed in its own transaction. This is called a resubmitting
 * processor because after each item has been processed, a new Runnable is submitted
 * to the executor to process the next item. This allows multiple concurrently running 
 * bulk processes to complete on an equal footing for processing time, doesn't require
 * a large amount of memory, and prevents a couple large/slow processes from preventing 
 * a smaller/faster process from sneaking in and finishing first.
 * 
 * In addition, this processor makes use of the delayThrottleCalculator to determine
 * when the resubmitted task should be scheduled to run. Because the same instance of
 * the delayThrottleCaclulator is shared between all processes using this class, it
 * effects the aggregate rate that items will be processed. 
 * 
 * (For example, when doing a bulk import, no matter how many bulk imports are running
 * in parallel, a device will only be added (by default) every 500ms.)
 * 
 */
public class ResubmittingBulkProcessor extends BulkProcessorBase {
    private Logger log = YukonLogManager.getLogger(ResubmittingBulkProcessor.class);
    
    private ScheduledExecutor executor = null;
    private TransactionOperations transactionTemplate = null;
    private DelayThrottleCalculator delayThrottleCalculator = null;
    
    @Autowired
    public void setDelayThrottleCalculator(@Qualifier("bulkImport") DelayThrottleCalculator delayThrottleCalculator) {
        this.delayThrottleCalculator = delayThrottleCalculator;
    }
    
    @Autowired
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Required
    public void setExecutor(ScheduledExecutor executor) {
        this.executor = executor;
    }
    
    public <I, O> void bulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, 
            Processor<O> processor,
            BulkProcessorCallback<I,O> callback) {

        throw new UnsupportedOperationException("This processor only supports background processing");
    }

    public <I, O> void backgroundBulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, Processor<O> processor,
            BulkProcessorCallback<I,O> callback) {

        if (log.isDebugEnabled()) {
            log.debug("bulkProcess called with " + mapper + " and " + processor);
        }
        
        // Get the bulk processor runnable and ask the ScheduledExecutor to run
        // it in the background
        Runnable runnable = getBulkProcessorRunnable(iterator,
                                                     mapper, processor,
                                                     callback, 0);
        
        long nextDelay = delayThrottleCalculator.getNextDelay(TimeUnit.MILLISECONDS);
        log.debug("Scheduling first runtime in " + nextDelay + "ms");
        executor.schedule(runnable, nextDelay, TimeUnit.MILLISECONDS);

    }
    
    private <I, O> Runnable getBulkProcessorRunnable(final Iterator<I> iterator,
            final ObjectMapper<I, O> mapper, final Processor<O> processor,
            final BulkProcessorCallback<I,O> callback, final int thisRow) {
        return new Runnable() {
            public void run() {
                try {
                    final I in = iterator.next();
                    try {
                        transactionTemplate.execute(new TransactionCallback() {
                            public Object doInTransaction(TransactionStatus status) {
                                O out = mapper.map(in);
                                processor.process(out);
                                callback.processedObject(thisRow, out);
                                return out;
                            }
                        });

                    } catch (ObjectMappingException e) {
                        callback.receivedProcessingException(thisRow, in, e);
                    } catch (ProcessingException e) {
                        callback.receivedProcessingException(thisRow, in, e);
                    }

                    // handle next
                    if (iterator.hasNext()) {
                        int nextRow = thisRow + 1;
                        Runnable nextRunnable = getBulkProcessorRunnable(iterator, mapper, processor, callback, nextRow);
                        long nextDelay = delayThrottleCalculator.getNextDelay(TimeUnit.MILLISECONDS);
                        log.debug("Scheduling next runtime in " + nextDelay + "ms of row " + nextRow);
                        executor.schedule(nextRunnable, nextDelay, TimeUnit.MILLISECONDS);
                    } else {
                        log.debug("Iterator is exhausted");
                        callback.processingSucceeded();
                        if (iterator instanceof Closeable) {
                            try {
                                ((Closeable)iterator).close();
                            } catch (IOException e) {
                                ;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Bulk Processing failed", e);
                    callback.processingFailed(e);
                    if (iterator instanceof Closeable) {
                        try {
                            ((Closeable)iterator).close();
                        } catch (IOException e2) {
                            ;
                        }
                    }
                }
            }
        };

    }

}
