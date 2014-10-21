package com.cannontech.common.rfn.model;

public class GatewayCertificateException extends Exception {
    public GatewayCertificateException(String message) {
        super(message);
    }
    
    public GatewayCertificateException(String message, Exception cause) {
        super(message, cause);
    }
}
