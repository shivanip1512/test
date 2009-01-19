package com.cannontech.stars.dr.account.exception;

public class InvalidSubstationNameException extends IllegalArgumentException {
    
    public InvalidSubstationNameException(String message) {
        super(message);
    }

    public InvalidSubstationNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
