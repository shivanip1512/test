package com.cannontech.cc.dao;

public class UnknownParameterException extends Exception {

    public UnknownParameterException(String message) {
        super(message);
    }

    public UnknownParameterException(String message, Throwable cause) {
        super(message, cause);
    }

}
