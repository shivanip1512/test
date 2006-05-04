package com.cannontech.cc.service.exception;

public class EventModificationException extends Exception {

    public EventModificationException(String message) {
        super(message);
    }

    public EventModificationException(String message, Throwable cause) {
        super(message, cause);
    }

}
