package com.cannontech.common.bulk;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.util.ObjectMapper;

public class TranslatingBulkProcessorCallback<I,O> implements BulkProcessorCallback<I> {
    
    private final ObjectMapper<I,O> mapper;
    private final BulkProcessorCallback<O> delegate;

    public TranslatingBulkProcessorCallback(BulkProcessorCallback<O> delegate, ObjectMapper<I,O> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
    }

    @Override
    public void processedObject(int rowNumber, I object) {
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
    public void receivedObjectMappingException(int rowNumber,
            ObjectMappingException e) {
        delegate.receivedObjectMappingException(rowNumber, e);
    }

    @Override
    public void receivedProcessingException(int rowNumber, I object,
            ProcessingException e) {
        delegate.receivedProcessingException(rowNumber, mapper.map(object), e);
    }

}
