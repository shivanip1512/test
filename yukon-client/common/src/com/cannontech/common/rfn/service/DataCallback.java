package com.cannontech.common.rfn.service;

import org.springframework.context.MessageSourceResolvable;

public interface DataCallback<D> extends Callback {
    
    /**
     * Handles the responses that indicate the request was not successful.
     */
    public void receivedDataError(MessageSourceResolvable message);
    
    /**
     * Handles the responses that contain the data requested.
     */
    public void receivedData(D data);
    
}