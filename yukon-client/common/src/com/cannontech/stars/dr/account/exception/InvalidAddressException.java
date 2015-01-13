package com.cannontech.stars.dr.account.exception;

public class InvalidAddressException extends IllegalArgumentException {

    public InvalidAddressException(String message) {
        super(message);
    }

    public InvalidAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
