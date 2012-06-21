package com.cannontech.amr.rfn.service;

public interface RfnCallback {
    
    /**
     * Method to keep track of processing exceptions that occur.
     * @param message
     */
    public void processingExceptionOccured(String message);
    
    /**
     * Method to signal the that the read has completed.
     * Should be called once regardless of success or failure.
     */
    public void complete();
    
}