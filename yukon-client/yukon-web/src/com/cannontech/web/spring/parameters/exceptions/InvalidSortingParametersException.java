package com.cannontech.web.spring.parameters.exceptions;

public class InvalidSortingParametersException extends RuntimeException {

    public InvalidSortingParametersException(String message) {
        super(message);
    }

    public InvalidSortingParametersException(String message, Throwable cause) {
        super(message, cause);
    }

}
