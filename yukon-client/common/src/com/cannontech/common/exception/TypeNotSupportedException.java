package com.cannontech.common.exception;

public class TypeNotSupportedException extends RuntimeException{
    
    public TypeNotSupportedException(String message) {
        super(message);
    }

    public TypeNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

}
