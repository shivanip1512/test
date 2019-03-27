package com.cannontech.dr.itron.service;

public class ItronEventNotFoundException extends ItronCommunicationException {

    public ItronEventNotFoundException(String message) {
        super(message);
    }

    public ItronEventNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
