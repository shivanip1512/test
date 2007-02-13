package com.cannontech.common.exception;

public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException() {
        super("No user found in the session");
    }
}
