package com.cannontech.common.bulk.impl;

import java.util.Date;
import java.util.Iterator;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BulkProcessorCallback;
import com.cannontech.common.bulk.callbackResult.CollectingBulkProcessorCallback;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

public abstract class BulkProcessorBase implements BulkProcessor {

    public <I> BulkProcessorCallback<I,I> bulkProcess(Iterator<I> iterator, Processor<? super I> processor) {

        // Use a pass through mapper
        ObjectMapper<I, I> passThroughMapper = new PassThroughMapper<I>();

        return bulkProcess(iterator, passThroughMapper, processor);
    }
    
    @Override
    public <I, O> BulkProcessorCallback<I,O> bulkProcess(
            Iterator<I> iterator, ObjectMapper<I, O> mapper,
            Processor<? super O> processor) {
        
        CollectingBulkProcessorCallback<I,O> callback = new CollectingBulkProcessorCallback<I,O>();
        
        bulkProcess(iterator, mapper, processor, callback);
        
        return callback;
    }

    public <I> void backgroundBulkProcess(Iterator<I> iterator, Processor<? super I> processor,
            BulkProcessorCallback<I,I> callback) {

        // Use a pass through mapper
        ObjectMapper<I, I> passThroughMapper = new PassThroughMapper<I>();

        callback.processingStarted(new Date());
        backgroundBulkProcess(iterator, passThroughMapper, processor, callback);
    }
}
