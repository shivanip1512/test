package com.cannontech.dr.itron.service;

public class ItronCommunicationException extends RuntimeException {

    public ItronCommunicationException(String message) {
        super(message);
    }

    public ItronCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}