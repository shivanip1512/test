package com.cannontech.notif.voice;

public class UnknownCallTokenException extends Exception {
    public UnknownCallTokenException(String token) {
        super("Unable to retreive call for token " + token);
    }
}