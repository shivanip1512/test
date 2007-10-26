package com.cannontech.common.bulk.processor;

/**
 * Exception thrown when there is a problem when trying to process an object
 */
public class ProcessingException extends Exception {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
