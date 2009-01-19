package com.cannontech.stars.dr.account.exception;

public class InvalidLoginGroupException extends IllegalArgumentException {
    
    public InvalidLoginGroupException(String message) {
        super(message);
    }

    public InvalidLoginGroupException(String message, Throwable cause) {
        super(message, cause);
    }
}
