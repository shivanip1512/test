package com.cannontech.amr.phaseDetect.data;

public class UndefinedPhaseException extends RuntimeException {
    public UndefinedPhaseException(String message) {
        super(message);
    }

    public UndefinedPhaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedPhaseException() {
    }
}