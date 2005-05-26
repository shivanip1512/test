package com.cannontech.notif.outputs;

public class OutputException extends Exception {
    private static final long serialVersionUID = 1L;

    public OutputException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutputException(String message) {
        super(message);
    }

}
