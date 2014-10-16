package com.cannontech.common.rfn.service;

import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponse;

public interface RfnGatewayUpgradeCallback {

    /**
     * Called if an exception occurs while processing the gateway upgrade request.
     * @param e the exception that occurred.
     */
    public void handleException(Exception e);

    /**
     * Called when Yukon times out waiting for NM's reply to the gateway upgrade request.
     */
    public void handleTimeout();

    /**
     * Called when NM replies to Yukon's gateway upgrade request.
     * @param reply NM's reply to the gateway upgrade request.
     */
    public void handleReply(RfnGatewayUpgradeResponse reply);
    
    /**
     * Guaranteed to be called last whether or not there were errors.
     */
    public void complete();
}
