package com.cannontech.dr.eatonCloud.model.v1;

/**
 * Exception thrown when there is a communication error with PX White, or the response has a bad http status.
 * Errors are null, use status to build an error to display to user.
 */
public class EatonCloudCommunicationExceptionV1 extends RuntimeException {
    private final String DEFAULT_ERROR = "A communication error has occurred. Please see logs for more details";
    private final EatonCloudErrorV1 errors;
    
    public EatonCloudCommunicationExceptionV1() {
        this(null);
    }
    
    public EatonCloudCommunicationExceptionV1(EatonCloudErrorV1 errors) {
        this.errors = errors;
    }
    
    public EatonCloudErrorV1 getErrorMessage() {
        return errors;
    }
 
    @Override
    public String getMessage() {
        if(errors != null &&  errors.getMessage() != null) {
            return errors.getMessage();
        }
        return DEFAULT_ERROR;
    }
}
