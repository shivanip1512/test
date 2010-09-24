package com.cannontech.amr.rfn.service;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;

public interface RfnMeterDisconnectCallback extends RfnMeterCallback{
    
    /**
     * Handles the initial response for a disconnect request.
     * @param replyType
     */
    public void receivedInitialReply(RfnMeterDisconnectInitialReplyType replyType);
    
    /**
     * Handles the errors for the initial response of a disconnect request.
     * @param replyType
     */
    public void receivedInitialError(RfnMeterDisconnectInitialReplyType replyType);
    
    /**
     * Handles the confirmation response for a disconnect request.
     * @param replyType
     */
    public void receivedConfirmationReply(RfnMeterDisconnectConfirmationReplyType replyType);
    
    /**
     * Handles the errors for the confirmation response of a disconnect request.
     * @param replyType
     */
    public void receivedConfirmationError(RfnMeterDisconnectConfirmationReplyType replyType);

}