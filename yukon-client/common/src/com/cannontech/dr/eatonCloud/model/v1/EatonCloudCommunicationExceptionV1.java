package com.cannontech.dr.eatonCloud.model.v1;

/**
 * Exception thrown when there is a communication error with PX White, or the response has a bad http status.
 * Errors are null, use status to build an error to display to user.
 */
public class EatonCloudCommunicationExceptionV1 extends RuntimeException {
    private final int status;
    private final EatonCloudErrorV1 errors;
    
    public EatonCloudCommunicationExceptionV1(int status) {
        this(status, null);
    }
    
    public EatonCloudCommunicationExceptionV1(int status, EatonCloudErrorV1 errors) {
        this.status = status;
        this.errors = errors;
    }
    
    public int getStatus() {
        return status;
    }
    
    public EatonCloudErrorV1 getErrorMessage() {
        return errors;
    }
}
