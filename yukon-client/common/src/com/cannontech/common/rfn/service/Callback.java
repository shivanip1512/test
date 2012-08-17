package com.cannontech.common.rfn.service;

import org.springframework.context.MessageSourceResolvable;

public interface Callback {

    /**
     * Method to keep track of processing exceptions that occur.
     * @param message
     */
    public void processingExceptionOccured(MessageSourceResolvable message);
    
    /**
     * Method to signal the that the request has completed.
     * Should be called once regardless of success or failure.
     */
    public void complete();
    
}