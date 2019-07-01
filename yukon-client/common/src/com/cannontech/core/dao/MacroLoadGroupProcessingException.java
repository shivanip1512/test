package com.cannontech.core.dao;

public class MacroLoadGroupProcessingException extends RuntimeException {

    public MacroLoadGroupProcessingException(String message) {
        super(message);
    }

    public MacroLoadGroupProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}