package com.cannontech.dr.eatonCloud.model.v1;

import com.google.gson.GsonBuilder;

/**
 * Exception thrown when there is a communication error with PX White, or the response has a bad http status.
 * Errors are null, use status to build an error to display to user.
 */
public class EatonCloudCommunicationExceptionV1 extends RuntimeException {
    private String DEFAULT_ERROR = "A communication error has occurred. Please see logs for more details";
    private EatonCloudErrorV1 errors;
    private long requestIdentifier;

    public EatonCloudCommunicationExceptionV1() {
    }
    
    public EatonCloudCommunicationExceptionV1(EatonCloudErrorV1 errors) {
        this.errors = errors;
    }

    public EatonCloudCommunicationExceptionV1(Throwable t) {
        super(t);
    }
    
    public EatonCloudCommunicationExceptionV1(Throwable t, long requestIdentifier) {
        super(t);
        this.setRequestIdentifier(requestIdentifier);
    }

    public EatonCloudErrorV1 getErrorMessage() {
        return errors;
    }

    // used for UI display
    public String getDisplayMessage() {
        if (errors != null && errors.getMessage() != null) {
            return errors.getMessage();
        }
        return DEFAULT_ERROR;
    }

    public void setRequestIdentifier(long requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    @Override
    public String getMessage() {
        String errorWithJson = getRequestIdentifier() + getDisplayMessage();
        if (errors != null && errors.getMessage() != null) {
            return errorWithJson + System.lineSeparator()
                    + new GsonBuilder().setPrettyPrinting().create().toJson(getErrorMessage());
        }
        return errorWithJson;
    }
    
    private String getRequestIdentifier() {
        return requestIdentifier == 0L ? "" : "EC[" + requestIdentifier + "] ";
    }
}
