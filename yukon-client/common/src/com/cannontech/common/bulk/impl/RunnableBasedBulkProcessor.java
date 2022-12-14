package com.cannontech.common.bulk.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.BulkProcessorCallback;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

public abstract class RunnableBasedBulkProcessor extends BulkProcessorBase {
    private Logger log = YukonLogManager.getLogger(RunnableBasedBulkProcessor.class);
    
    private Executor executor = null;

    @Required
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public <I, O> void bulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, 
            Processor<? super O> processor,
            BulkProcessorCallback<I,O> callback) {

        if (log.isDebugEnabled()) {
            log.debug("bulkProcess called with " + mapper + " and " + processor + " on " + this.getClass().getSimpleName());
        }
        
        callback.processingStarted(new Date());
        
        // Get the bulk processor runnable and run it in this thread (will
        // block)
        Runnable runnable = getBulkProcessorRunnable(iterator,
                                                     mapper, processor,
                                                     callback);
        runnable.run();
    }

    public <I, O> void backgroundBulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, Processor<? super O> processor,
            BulkProcessorCallback<I,O> callback) {

        if (log.isDebugEnabled()) {
            log.debug("bulkProcess called with " + mapper + " and " + processor + " on " + this.getClass().getSimpleName());
        }
        
        callback.processingStarted(new Date());
        
        // Get the bulk processor runnable and ask the ScheduledExecutor to run
        // it in the background
        Runnable runnable = getBulkProcessorRunnable(iterator,
                                                     mapper, processor,
                                                     callback);
        executor.execute(runnable);

    }
    
    protected abstract <I, O> Runnable getBulkProcessorRunnable(
            final Iterator<I> iterator, final ObjectMapper<I, O> mapper,
            final Processor<? super O> processor, final BulkProcessorCallback<I,O> callback);
    
}
