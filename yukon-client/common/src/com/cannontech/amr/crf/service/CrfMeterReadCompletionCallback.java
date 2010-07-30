package com.cannontech.amr.crf.service;

import com.cannontech.amr.crf.message.CrfMeterReadingReplyType;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CrfMeterReadCompletionCallback {
	
	/**
	 * Method to keep track of received point values.
	 * @param PointValueHolder value
	 */
    public void receivedValue(PointValueHolder value);
    
    /**
     * Method to signal the that the read has completed.
     * Should be called once regardless of success or failure.
     */
    public void complete();
    
    /**
     * Method to keep track of errors that occur.
     * @param CrfMeterReadingReplyType reason
     */
    public void receivedError(CrfMeterReadingReplyType reason);
    
    /**
     * Method to signal that the initial response to the read request
     * was recieved. Status will indicate if the reciever expects to be 
     * able to read the meter or not. Should be called only once.
     * @param CrfMeterReadingReplyType status
     */
    public void receivedStatus(CrfMeterReadingReplyType status);
    
}