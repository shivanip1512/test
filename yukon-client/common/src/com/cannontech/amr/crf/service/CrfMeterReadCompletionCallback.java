package com.cannontech.amr.crf.service;

import com.cannontech.amr.crf.message.CrfMeterReadingDataReplyType;
import com.cannontech.amr.crf.message.CrfMeterReadingReplyType;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CrfMeterReadCompletionCallback {
	
	/**
	 * Method to keep track of received point values.
	 * @param PointValueHolder value
	 */
    public void receivedData(PointValueHolder value);
    
    /**
     * Method to signal the that the read has completed.
     * Should be called once regardless of success or failure.
     */
    public void complete();
    
    /**
     * Method to keep track of errors for the data response.
     * @param CrfMeterReadingReplyType replyType
     */
    public void receivedDataError(CrfMeterReadingDataReplyType replyType);
    
    /**
     * Method to signal that the initial response to the read request
     * was received. Status will indicate if the receiver expects to be 
     * able to read the meter or not. Should be called only once.
     * @param CrfMeterReadingReplyType replyType
     */
    public void receivedStatus(CrfMeterReadingReplyType replyType);

    /**
     * Method to keep track of errors for the status response.
     * @param replyType
     */
    public void receivedStatusError(CrfMeterReadingReplyType replyType);

    /**
     * Method to keep track of processing exceptions that occur.
     * @param message
     */
    public void processingExceptionOccured(String message);
    
}