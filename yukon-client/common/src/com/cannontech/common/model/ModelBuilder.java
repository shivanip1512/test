package com.cannontech.common.model;

public interface ModelBuilder <T> {
    
    /**
     * Build model object .
     */
    public T overwriteWith(T object);

}
