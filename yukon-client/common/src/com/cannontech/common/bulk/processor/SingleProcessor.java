package com.cannontech.common.bulk.processor;

import java.util.Collection;

public abstract class SingleProcessor<T> implements Processor<T> {
    
    public void process(Collection<T> objectCollection) throws ProcessingException {
        for (T object : objectCollection) {
            process(object);
        }
    }
}

