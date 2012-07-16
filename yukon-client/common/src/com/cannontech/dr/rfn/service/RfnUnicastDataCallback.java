package com.cannontech.dr.rfn.service;

import com.cannontech.amr.rfn.service.RfnCallback;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;

public interface RfnUnicastDataCallback extends RfnCallback {

    /**
     * Method to track data returned from a successful request.  Could be anything,
     * many times this will be point data.  Users of this method should cast the object
     * appropriately if they intend to use it. Point data will be {@PointValueHolder}. 
     */
    public void receivedData(Object data);
    
    /**
     * Method to keep track of errors for the data response.
     */
    public void receivedDataError(RfnExpressComUnicastDataReplyType replyType);
    
    /**
     * Method to signal that the initial response to the read request
     * was received. Status will indicate if the receiver expects to be 
     * able to read the meter or not. Should be called only once.
     */
    public void receivedStatus(RfnExpressComUnicastReplyType replyType);

    /**
     * Method to keep track of errors for the status response.
     * @param replyType
     */
    public void receivedStatusError(RfnExpressComUnicastReplyType replyType);
    
}