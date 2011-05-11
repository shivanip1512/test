package com.cannontech.core.dao;

/**
 * Exception to be used when remove an item in use which doesn't for some reason have a foreign
 * key constraint.  (Use of this class should be rare.)
 */
public class InUseException extends Exception {
    public InUseException() {
    }

    public InUseException(String message) {
        super(message);
    }

    public InUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
