package com.cannontech.common.exception;

public class DeletionFailureException extends RuntimeException {

    public DeletionFailureException(String message) {
        super(message);
    }

    public DeletionFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
