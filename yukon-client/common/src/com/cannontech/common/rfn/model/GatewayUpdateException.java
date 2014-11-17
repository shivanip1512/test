package com.cannontech.common.rfn.model;

import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;

public class GatewayUpdateException extends Exception {
    
    private final GatewayUpdateResult failureReason;
    
    public GatewayUpdateException(String message, GatewayUpdateResult failureReason) {
        super(message);
        this.failureReason = failureReason;
    }
    
    public GatewayUpdateException(String message, GatewayUpdateResult failureReason, Exception cause) {
        super(message, cause);
        this.failureReason = failureReason;
    }
    
    public GatewayUpdateResult getReason() {
        return failureReason;
    }
}