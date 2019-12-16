package com.cannontech.web.dev.error;

public class RelayStateInjectionException extends Exception {

    public RelayStateInjectionException(Throwable cause) {
        super(cause);
    }
    
    public RelayStateInjectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RelayStateInjectionException(String message) {
        super(message);
    }
}
