package com.cannontech.web.spring.parameters.exceptions;

public class InvalidPagingParametersException extends RuntimeException {

    public InvalidPagingParametersException(String message) {
        super(message);
    }

    public InvalidPagingParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
