package com.cannontech.cc.service.exception;

public class NoSuchStrategyException extends RuntimeException {

    public NoSuchStrategyException(String message) {
        super(message);
    }

    public NoSuchStrategyException(String message, Throwable cause) {
        super(message, cause);
    }

}
