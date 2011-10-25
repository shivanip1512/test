package com.cannontech.amr.rfn.service;

import org.springframework.context.MessageSourceResolvable;

public interface RfnMeterDisconnectCallback {
    
    /**
     * Method to keep track of processing exceptions that occur.
     * @param message
     */
    public void processingExceptionOccured(MessageSourceResolvable message);
    
    /**
     * Method to signal the that the disconnect has completed.
     * Should be called once regardless of success or failure.
     */
    public void complete();
    
    /**
     * Handles the successful response for a disconnect request.
     * @param replyType
     */
    public void receivedSuccess();
    
    /**
     * Handles the errors for the response of a disconnect request.
     * @param replyType
     */
    public void receivedError(MessageSourceResolvable message);
    

}