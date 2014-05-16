package com.cannontech.amr.rfn.service;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.common.rfn.service.Callback;
import com.cannontech.core.dynamic.PointValueQualityHolder;


public interface RfnMeterDisconnectCallback extends Callback {
    
    /**
     * Handles the successful response for a disconnect request.
     * @param state the state return when doing a 'QUERY' command.
     */
    public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData);
    
    /**
     * Handles the errors for the response of a disconnect request.
     */
    public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state);
    

}