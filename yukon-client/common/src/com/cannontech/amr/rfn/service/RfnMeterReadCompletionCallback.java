package com.cannontech.amr.rfn.service;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.core.dynamic.PointValueHolder;

public interface RfnMeterReadCompletionCallback extends RfnCallback {
	
	/**
	 * Method to keep track of received point values.
	 * @param PointValueHolder value
	 */
    public void receivedData(PointValueHolder value);
    
    /**
     * Method to keep track of errors for the data response.
     * @param RfnMeterReadingReplyType replyType
     */
    public void receivedDataError(RfnMeterReadingDataReplyType replyType);
    
    /**
     * Method to signal that the initial response to the read request
     * was received. Status will indicate if the receiver expects to be 
     * able to read the meter or not. Should be called only once.
     * @param RfnMeterReadingReplyType replyType
     */
    public void receivedStatus(RfnMeterReadingReplyType replyType);

    /**
     * Method to keep track of errors for the status response.
     * @param replyType
     */
    public void receivedStatusError(RfnMeterReadingReplyType replyType);

}