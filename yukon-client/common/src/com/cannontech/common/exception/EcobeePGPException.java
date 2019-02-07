package com.cannontech.common.exception;

public class EcobeePGPException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EcobeePGPException(String message) {
        super(message);
    }

    public EcobeePGPException(String message, Throwable cause) {
        super(message, cause);
    }
}
