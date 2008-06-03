package com.cannontech.common.bulk.processor;

import java.util.Collections;
import java.util.Set;

public abstract class CollectionProcessor<T> implements Processor<T> {
    
    public void process(T object) throws ProcessingException {
        Set<T> collection = Collections.singleton(object);
        process(collection);
    }
}

