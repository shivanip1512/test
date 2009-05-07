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
     * A process may be marked completed due to a cancel() or a processingExceptionOccured().
     */
    public void complete();
    
    /**
     * Called when a request is made to cancel the command(s).
     * The executor does not call this until all commands have been written, and after the cancel command has been sent and responded to.
     * After a cancel(), the complete() method is called.
     */
    public void cancel();
    
    /**
     * Called when an exception occurs during the processing.
     * After a processingExceptionOccured(), the complete() method is called.
     * @param reason
     */
    public void processingExceptionOccured(String reason);
}