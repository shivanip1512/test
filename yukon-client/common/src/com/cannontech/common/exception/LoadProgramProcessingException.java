package com.cannontech.common.exception;

public class LoadProgramProcessingException extends RuntimeException {

    public LoadProgramProcessingException(String message) {
        super(message);
    }

    public LoadProgramProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
