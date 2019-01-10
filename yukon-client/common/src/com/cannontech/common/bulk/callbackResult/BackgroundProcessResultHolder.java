package com.cannontech.common.bulk.callbackResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.util.Completable;

/**
 * Interface which allows access to the results of a bulk processing operation
 */
public interface BackgroundProcessResultHolder extends Completable {

	public BackgroundProcessTypeEnum getBackgroundProcessType();
	
    // EXCEPTION LISTS
    public List<ProcessorCallbackException> getProcessingExceptionList();
    
    // EXCEPTION ROW NUMBER MAPS
    public Map<Integer, ProcessorCallbackException> getProcessingExceptionRowNumberMap();

    // COUNT GETTERS
    public int getSuccessCount();
    public int getProcessingExceptionCount();

    // STATUS
    public int getTotalItems();
    public boolean isComplete();
    public boolean isSuccessfull();
    public boolean isProcessingFailed();
    public Exception getFailedException();
    public Date getStartTime();
    public Date getStopTime();
    
    // RESULT FEATURES
    
    // success objects
    public boolean isSuccessDevicesSupported();
    public DeviceCollection getSuccessDeviceCollection();
    
    // failure objects
    public boolean isFailureDevicesSupported();
    public DeviceCollection getFailureDeviceCollection();
    
    // failure reasons
    public boolean isFailureReasonsListSupported();
    
    // failure file
    public boolean isFailureFileSupported();

    /**
     * This method is used for logging an event. Override this method wherever logging is required.
     */
    default void logEvent() {

    }

}