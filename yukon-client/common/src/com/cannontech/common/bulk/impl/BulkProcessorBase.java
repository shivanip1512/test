package com.cannontech.common.bulk.impl;

import java.util.Iterator;

import com.cannontech.common.bulk.BulkProcessingResultHolder;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.BulkProcessorCallback;
import com.cannontech.common.bulk.CollectingBulkProcessorCallback;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

public abstract class BulkProcessorBase implements BulkProcessor {

    public <I> BulkProcessingResultHolder<I,I> bulkProcess(Iterator<I> iterator, Processor<I> processor) {

        // Use a pass through mapper
        ObjectMapper<I, I> passThroughMapper = new PassThroughMapper<I>();

        return bulkProcess(iterator, passThroughMapper, processor);
    }
    
    @Override
    public <I, O> BulkProcessingResultHolder<I,O> bulkProcess(
            Iterator<I> iterator, ObjectMapper<I, O> mapper,
            Processor<O> processor) {
        
        CollectingBulkProcessorCallback<I,O> callback = new CollectingBulkProcessorCallback<I,O>();
        
        bulkProcess(iterator, mapper, processor, callback);
        
        return callback;
    }

    public <I> void backgroundBulkProcess(Iterator<I> iterator, Processor<I> processor,
            BulkProcessorCallback<I,I> callback) {

        // Use a pass through mapper
        ObjectMapper<I, I> passThroughMapper = new PassThroughMapper<I>();

        backgroundBulkProcess(iterator, passThroughMapper, processor, callback);
    }
}
