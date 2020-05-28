package com.cannontech.common.exception;

public class TypeNotSupportedExcpetion extends RuntimeException{
    
    public TypeNotSupportedExcpetion(String message) {
        super(message);
    }

    public TypeNotSupportedExcpetion(String message, Throwable cause) {
        super(message, cause);
    }

}
