package com.cannontech.common.rfn.model;

public class NetworkManagerCommunicationException extends Exception {
    
    public NetworkManagerCommunicationException(String message) {
        super(message);
    }
    
    public NetworkManagerCommunicationException(String message, Exception cause) {
        super(message, cause);
    }
}
