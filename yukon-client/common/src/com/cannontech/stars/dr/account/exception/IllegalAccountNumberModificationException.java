package com.cannontech.stars.dr.account.exception;

public class IllegalAccountNumberModificationException extends IllegalArgumentException {

    public IllegalAccountNumberModificationException(String message) {
        super(message);
    }

    public IllegalAccountNumberModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
