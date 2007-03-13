package com.cannontech.tools.email;


public class EmailException extends RuntimeException {
    public EmailException(Exception e) {
        super(e);
    }
    public EmailException(String message) {
        super(message);
    }
}
