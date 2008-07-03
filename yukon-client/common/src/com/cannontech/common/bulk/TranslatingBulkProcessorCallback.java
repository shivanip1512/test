package com.cannontech.common.bulk;

import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.util.ObjectMapper;

public class TranslatingBulkProcessorCallback<I,O,F> implements BulkProcessorCallback<I,O> {
    
    private final ObjectMapper<O,F> mapper;
    private final BulkProcessorCallback<I,F> delegate;

    public TranslatingBulkProcessorCallback(BulkProcessorCallback<I,F> delegate, ObjectMapper<O,F> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
    }

    @Override
    public void processedObject(int rowNumber, O object) {
        delegate.processedObject(rowNumber, mapper.map(object));
    }

    @Override
    public void processingFailed(Exception e) {
        delegate.processingFailed(e);
    }

    @Override
    public void processingSucceeded() {
        delegate.processingSucceeded();
    }

    @Override
    public void receivedProcessingException(int rowNumber, I object,
            ProcessorCallbackException e) {
        delegate.receivedProcessingException(rowNumber, object, e);
    }

}
