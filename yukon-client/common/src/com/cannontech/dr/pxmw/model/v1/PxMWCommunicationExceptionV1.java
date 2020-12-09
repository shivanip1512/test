package com.cannontech.dr.pxmw.model.v1;

/**
 * Exception thrown when there is a communication error with PX White, or the response has a bad http status.
 * Errors are null, use status to build an error to display to user.
 */
public class PxMWCommunicationExceptionV1 extends RuntimeException {
    private final int status;
    private final PxMWErrorsV1 errors;
    
    public PxMWCommunicationExceptionV1(int status) {
        this(status, null);
    }
    
    public PxMWCommunicationExceptionV1(int status, PxMWErrorsV1 errors) {
        this.status = status;
        this.errors = errors;
    }
    
    public int getStatus() {
        return status;
    }
    
    public PxMWErrorsV1 getErrorMessage() {
        return errors;
    }
}
