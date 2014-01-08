package com.cannontech.messaging.connection.transport;


public class HandShakeTimeoutException extends TransportException {
    private static final long serialVersionUID = 1L;

    public HandShakeTimeoutException(String message) {
        super(message);
    }

    public HandShakeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandShakeTimeoutException(Throwable cause) {
        super(cause);
    }
}
