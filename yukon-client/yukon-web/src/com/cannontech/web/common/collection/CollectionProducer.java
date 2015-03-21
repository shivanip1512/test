package com.cannontech.web.common.collection;

public interface CollectionProducer<N, T> extends CollectionFactory<T> {
    
    /**
     * Method used to get the type of collection that this factory will produce
     * @return Type of collection
     */
    N getSupportedType();
    
}