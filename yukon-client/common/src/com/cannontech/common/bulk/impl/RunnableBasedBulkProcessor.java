package com.cannontech.common.bulk.impl;

import java.util.Iterator;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.BulkProcessorCallback;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

public abstract class RunnableBasedBulkProcessor extends BulkProcessorBase {
    
    private Executor executor = null;

    @Required
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public <I, O> void bulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, 
            Processor<O> processor,
            BulkProcessorCallback<O> callback) {

        // Get the bulk processor runnable and run it in this thread (will
        // block)
        Runnable runnable = getBulkProcessorRunnable(iterator,
                                                     mapper, processor,
                                                     callback);
        runnable.run();
    }

    public <I, O> void backgroundBulkProcess(Iterator<I> iterator,
            ObjectMapper<I, O> mapper, Processor<O> processor,
            BulkProcessorCallback<O> callback) {


        // Get the bulk processor runnable and ask the ScheduledExecutor to run
        // it in the background
        Runnable runnable = getBulkProcessorRunnable(iterator,
                                                     mapper, processor,
                                                     callback);
        executor.execute(runnable);

    }
    
    protected abstract <I, O> Runnable getBulkProcessorRunnable(
            final Iterator<I> iterator, final ObjectMapper<I, O> mapper,
            final Processor<O> processor, final BulkProcessorCallback<O> callback);
    
}
