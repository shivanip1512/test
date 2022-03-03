package com.cannontech.common.exception;

public class ServiceCommunicationFailedException extends Exception {

    private static final long serialVersionUID = -4748776123756277386L;

    public ServiceCommunicationFailedException(Throwable cause) {
        super(cause);
    }
}
