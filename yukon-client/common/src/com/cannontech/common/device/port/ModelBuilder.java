package com.cannontech.common.device.port;

public interface ModelBuilder <T> {
    
    /**
     * Build model object .
     */
    public T buildModel(T object);

}
