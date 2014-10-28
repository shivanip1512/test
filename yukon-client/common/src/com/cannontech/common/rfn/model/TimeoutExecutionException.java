package com.cannontech.common.rfn.model;

import java.util.concurrent.ExecutionException;

public class TimeoutExecutionException extends ExecutionException {

    public TimeoutExecutionException() {
        super("Timeout waiting for response");
    }
    
}