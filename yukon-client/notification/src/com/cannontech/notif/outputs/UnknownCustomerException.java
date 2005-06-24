package com.cannontech.notif.outputs;

public class UnknownCustomerException extends Exception {

    public UnknownCustomerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownCustomerException(String message) {
        super(message);
    }
    

}
