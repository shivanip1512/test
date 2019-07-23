package com.cannontech.common.exception;

public class LMObjectDeletionFailureException extends RuntimeException {

    public LMObjectDeletionFailureException(String message) {
        super(message);
    }

    public LMObjectDeletionFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
