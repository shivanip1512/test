package com.cannontech.messaging.connection.transport;

import com.cannontech.messaging.connection.ConnectionException;

public class TransportException extends ConnectionException {
    private static final long serialVersionUID = 1L;

    public TransportException(String message) {
        super(message);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransportException(Throwable cause) {
        super(cause);
    }
}
