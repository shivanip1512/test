package com.cannontech.common.bulk.callbackResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.util.Completable;

/**
 * Class which serves as a callback and result holder for bulk processing
 */
public class CollectingBulkProcessorCallback<I,O> implements BulkProcessorCallback<I,O>, Completable {

	protected boolean complete = false;
	protected Exception failedException = null;
	protected Date startTime;
	protected Date stopTime;

    // map of row number to success object
    protected Map<Integer, O> successObjectRowNumberMap = new LinkedHashMap<Integer, O>();
    
    // maps of row number to exception
    protected Map<Integer, ProcessorCallbackException> processingExceptionRowNumberMap = new LinkedHashMap<Integer, ProcessorCallbackException>();
    
    // map of row number to the object that failed to process
    protected Map<Integer, I> processingExceptionObjectRowNumberMap = new LinkedHashMap<Integer, I>();
    
    
    // CALL BACK METHODS
    //----------------------------------------------------------------------------------------------
    public void receivedProcessingException(int rowNumber, I object, ProcessorCallbackException e) {
        
        this.processingExceptionRowNumberMap.put(rowNumber, e);
        this.processingExceptionObjectRowNumberMap.put(rowNumber, object);
    }

    public void processedObject(int rowNumber, O object) {
        
        this.successObjectRowNumberMap.put(rowNumber, object);
    }
    
    public void processingSucceeded() {
    	
    	this.stopTime = new Date();
        this.complete = true;
    }
    
    // is processing completely blows up, this is the exception
    public void processingFailed(Exception e) {
    	
    	this.stopTime = new Date();
        this.complete = true;
        failedException = e;
    }
    
	public void processingStarted(Date startDateTime) {
		
		this.startTime = startDateTime;
	}
    
    
    
    // RESULTS HOLDER METHODS
    //----------------------------------------------------------------------------------------------
    
	// EXCEPTION LISTS
    public List<ProcessorCallbackException> getProcessingExceptionList() {
        return new ArrayList<ProcessorCallbackException>(this.processingExceptionRowNumberMap.values());
    }
    
    
    // EXCEPTION ROW NUMBER MAPS
    public Map<Integer, ProcessorCallbackException> getProcessingExceptionRowNumberMap() {
        return this.processingExceptionRowNumberMap;
    }
    
    // COUNT GETTERS
    public int getSuccessCount() {
        return this.successObjectRowNumberMap.size();
    }
    
    public int getProcessingExceptionCount() {
        return this.processingExceptionRowNumberMap.size();
    }
    
    
    // STATUS
    public boolean isComplete() {
        return this.complete;
    }

    public boolean isSuccessfull() {
        return isComplete() && !isProcessingFailed();
    }
    
    public boolean isProcessingFailed() {
        return failedException != null;
    }

    public Exception getFailedException() {
        return failedException;
    }
    
    public Date getStartTime() {
		return startTime;
	}
    
    public Date getStopTime() {
		return stopTime;
	}
    
    
    // SUCCESS AND FAIL (processing) OBJECTS
    public List<O> getSuccessObjects() {
        return new ArrayList<O>(this.successObjectRowNumberMap.values());
    }

    public List<I> getProcesingExceptionObjects() {
        return new ArrayList<I>(this.processingExceptionObjectRowNumberMap.values());
    }
}
