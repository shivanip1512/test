package com.cannontech.common.exception;

public class PasswordExpiredException extends RuntimeException {
    
    public PasswordExpiredException(String message) {
        super(message);
    }
    
    public PasswordExpiredException() {
        super();
    }
}
