package com.cannontech.thirdparty.digi.exception;


public class DigiWebServiceException extends RuntimeException {
    
    public DigiWebServiceException(String message) {
        super(message);
    }

    public DigiWebServiceException(Exception e) {
        super(e);
    }

    public DigiWebServiceException(String string, Throwable e) {
        super(string, e);
    }
    
}