package com.cannontech.common.rfn.service;

import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAck;

public interface RfnGatewayUpdateCallback {

    /**
     * Called if an exception occurs while processing the gateway update request.
     * @param e the exception that occurred.
     */
    public void handleException(Exception e);

    /**
     * Called when Yukon times out waiting for NM's reply to the gateway update request.
     */
    public void handleTimeout();

    /**
     * Called when NM replies to Yukon's gateway update request.
     * @param reply NM's reply to the gateway update request.
     */
    public void handleReply(RfnGatewayUpgradeRequestAck reply);
    
    /**
     * Guaranteed to be called last whether or not there were errors.
     */
    public void complete();
}
