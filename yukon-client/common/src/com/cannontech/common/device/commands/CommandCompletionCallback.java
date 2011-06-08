package com.cannontech.common.device.commands;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CommandCompletionCallback<T> {
	
	/**
	 * Methods to keep track of received values and errors.
	 */
    public void receivedValue(T command, PointValueHolder value);
    
    public void receivedIntermediateError(T command, SpecificDeviceErrorDescription error);
    
    public void receivedLastError(T command, SpecificDeviceErrorDescription error);
    
    public void receivedIntermediateResultString(T command, String value);
    
    /**
     * Either this or receivedLastError will get called for each command exactly once, 
     * unless a processingExceptionOccured happens, in which case it may not get called at all.
     * @param command
     * @param value
     */
    public void receivedLastResultString(T command, String value);
    
    /**
     * Called when after the process has completed. Guaranteed to be called exactly once.
     */
    public void complete();
    
    /**
     * Called when a request is made to cancel the command(s).
     */
    public void cancel();
    
    /**
     * Called when an exception occurs during the processing. May get called multiple times before complete().
     * @param reason
     */
    public void processingExceptionOccured(String reason);
}