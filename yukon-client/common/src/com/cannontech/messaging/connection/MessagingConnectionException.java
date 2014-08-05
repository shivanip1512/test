package com.cannontech.messaging.connection;

public class MessagingConnectionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MessagingConnectionException(String message) {
        super(message);
    }

    public MessagingConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessagingConnectionException(Throwable cause) {
        super(cause);
    }
}
