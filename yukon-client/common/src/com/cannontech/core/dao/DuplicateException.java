package com.cannontech.core.dao;

/**
 * Exception to be used when an attempt to save a duplicate item is made 
 */
public class DuplicateException extends RuntimeException {
    
    public DuplicateException() {
        
    }
    
    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
