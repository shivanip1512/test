package com.cannontech.common.rfn.model;

public class GatewayUpdateException extends Exception {
    public GatewayUpdateException(String message) {
        super(message);
    }
    
    public GatewayUpdateException(String message, Exception cause) {
        super(message, cause);
    }
}