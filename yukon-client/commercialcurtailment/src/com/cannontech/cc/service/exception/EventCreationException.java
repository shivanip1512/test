package com.cannontech.cc.service.exception;

public class EventCreationException extends Exception {

    public EventCreationException(String message) {
        super(message);
    }

    public EventCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
