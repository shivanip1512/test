package com.cannontech.dr.rfn.service;

import com.cannontech.amr.rfn.service.RfnCallback;
import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastReplyType;

public interface RfnBroadcastCompletionCallback extends RfnCallback {

    /**
     * Method to track data returned from a successful request.  Could be anything,
     * many times this will be point data.  Users of this method should cast the object
     * appropriately if they intend to use it. Point data will be {@PointValueHolder}. 
     */
    public void receivedData(Object data);
    
    /**
     * Method to keep track of errors for the data response.
     */
    public void receivedError(RfnExpressComBroadcastReplyType replyType);

}