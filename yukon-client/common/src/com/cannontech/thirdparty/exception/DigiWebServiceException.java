package com.cannontech.thirdparty.exception;

public class DigiWebServiceException extends RuntimeException {
    
    public DigiWebServiceException(String message) {
        super(message);
    }

    public DigiWebServiceException(Exception e) {
        super(e);
    }
    
}