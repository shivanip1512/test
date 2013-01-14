package com.cannontech.amr.phaseDetect.service;

public class PhaseDetectCancelledException extends RuntimeException {
    private final static String DEFAULT_MSG = "phase detection process was cancelled";

    public PhaseDetectCancelledException() {
        super(DEFAULT_MSG);
    }

    public PhaseDetectCancelledException(String message) {
        super(message);
    }

    public PhaseDetectCancelledException(String message, Throwable cause) {
        super(message, cause);
    }
}
