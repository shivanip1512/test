package com.cannontech.thirdparty.digi.exception;


public class DigiNotConfiguredException extends RuntimeException {
    
    public DigiNotConfiguredException(String message) {
        super(message);
    }

    public DigiNotConfiguredException(Exception e) {
        super(e);
    }

    public DigiNotConfiguredException(String string, Throwable e) {
        super(string, e);
    }
    
}