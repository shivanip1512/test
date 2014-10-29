package com.cannontech.common.rfn.model;

public class NmCommunicationException extends Exception {
    
    public NmCommunicationException(String message) {
        super(message);
    }
    
    public NmCommunicationException(String message, Exception cause) {
        super(message, cause);
    }
}
