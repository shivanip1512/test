package com.cannontech.common.bulk.impl;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.BulkProcessorCallback;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.ScheduledExecutor;

public abstract class RunnableBasedBulkProcessor extends BulkProcessorBase {
    
    private ScheduledExecutor scheduledExecutor = null;

    @Required
    public void setScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
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
        scheduledExecutor.execute(runnable);

    }
    
    protected abstract <I, O> Runnable getBulkProcessorRunnable(
            final Iterator<I> iterator, final ObjectMapper<I, O> mapper,
            final Processor<O> processor, final BulkProcessorCallback<O> callback);
    
}
