package com.cannontech.dr.rfn.service;

import com.cannontech.amr.rfn.service.RfnCallback;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;

public interface RfnUnicastCallback extends RfnCallback {
    
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