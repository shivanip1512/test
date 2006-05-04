package com.cannontech.web.exceptions;

public class BadConfigurationException extends RuntimeException {

    public BadConfigurationException(String message) {
        super(message);
    }

    public BadConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
