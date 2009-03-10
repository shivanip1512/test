package com.cannontech.jobs.support;

public class JobManagerException extends RuntimeException {

    public JobManagerException(String message) {
        super(message);
    }

    public JobManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
