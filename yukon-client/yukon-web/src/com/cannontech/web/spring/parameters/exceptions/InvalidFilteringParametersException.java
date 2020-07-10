package com.cannontech.web.spring.parameters.exceptions;

public class InvalidFilteringParametersException extends RuntimeException {

    public InvalidFilteringParametersException(String message) {
        super(message);
    }

    public InvalidFilteringParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
