package com.cannontech.common.device.commands;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CommandCompletionCallback<T> {
	
	/**
	 * Methods to keep track of received values and errors.
	 */
    public void receivedValue(T command, PointValueHolder value);
    
    public void receivedIntermediateError(T command, DeviceErrorDescription error);
    
    public void receivedLastError(T command, DeviceErrorDescription error);
    
    public void receivedIntermediateResultString(T command, String value);
    
    public void receivedLastResultString(T command, String value);
    
    /**
     * Called when after the process has completed.
     */
    public void complete();
    
    /**
     * Called when a request is made to cancel the command(s).
     */
    public void cancel();
    
    /**
     * Called when an exception occurs during the processing.
     * @param reason
     */
    public void processingExceptionOccured(String reason);
}