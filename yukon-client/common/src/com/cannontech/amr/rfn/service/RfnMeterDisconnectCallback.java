package com.cannontech.amr.rfn.service;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectReplyType;

public interface RfnMeterDisconnectCallback extends RfnMeterCallback {
    
    /**
     * Handles the response for a disconnect request.
     * @param replyType
     */
    public void receivedData(RfnMeterDisconnectReplyType replyType);

    /**
     * Handles the error code returned when we receive a reply type of FAILURE.
     * @param errorCode
     */
    public void receivedError(String errorCode);
    
}