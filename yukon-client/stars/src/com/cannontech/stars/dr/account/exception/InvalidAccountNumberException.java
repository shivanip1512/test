package com.cannontech.stars.dr.account.exception;

public class InvalidAccountNumberException extends IllegalArgumentException {
    
    public InvalidAccountNumberException(String message) {
        super(message);
    }

    public InvalidAccountNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
