package com.cannontech.messaging.serialization;

public class SerializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);        
    }
}
