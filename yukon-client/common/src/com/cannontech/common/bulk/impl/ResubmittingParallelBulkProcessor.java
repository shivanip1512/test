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
 * Each row will be processed in its own transaction. This is called a parallel 
 * resubmitting processor because before each item has been processed, a new Runnable 
 * is submitted to the executor to process the next item. This allows multiple items 
 * from the same bulk process to process concurrently (up to the number of threads in the
 * executors pool, which is two as of this writing) while at the same time multiple bulk 
 * processes will complete on an equal footing for processing time.
 * 
 * In addition, this processor makes use of the delayThrottleCalculator to determine
 * when the resubmitted task should be scheduled to run. Because the same instance of
 * the delayThrottleCaclulator is shared between all processes using this class, it
 * effects the aggregate rate that items will be processed. Note, if the set delay is 
 * greater than the time it takes to process one item, this class will behave similarly
 * to the ResubmittingBulkProcessor. If the delay is less than the time it takes to 
 * process one item, the overall processing rate will be constrained by the number of 
 * threads assigned to the executor.
 */
public class ResubmittingParallelBulkProcessor extends BulkProcessorBase {
    private Logger log = YukonLogManager.getLogger(ResubmittingParallelBulkProcessor.class);
    
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
                    final I in;
                    synchronized (iterator) {
                        in = iterator.next();
                        // handle next
                        if (iterator.hasNext()) {
                            int nextRow = thisRow + 1;
                            Runnable nextRunnable = getBulkProcessorRunnable(iterator, mapper, processor, callback, nextRow);
                            long nextDelay = delayThrottleCalculator.getNextDelay(TimeUnit.MILLISECONDS);
                            log.debug("Scheduling next runtime in " + nextDelay + "ms of row " + nextRow);
                            executor.schedule(nextRunnable, nextDelay, TimeUnit.MILLISECONDS);
                        } else {
                            log.debug("Iterator is exhausted");
                            if (iterator instanceof Closeable) {
                                try {
                                    ((Closeable)iterator).close();
                                } catch (IOException e) {
                                    ;
                                }
                            }
                        }
                    }
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
                    callback.processingSucceeded();

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
