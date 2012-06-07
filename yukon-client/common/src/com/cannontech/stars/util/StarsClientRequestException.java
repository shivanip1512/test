package com.cannontech.stars.util;

/**
 * This Stars Runtime Exception is used to catch any client request errors.
 * @author mmalekar
 */
public class StarsClientRequestException extends RuntimeException {

    public StarsClientRequestException() {
        super();
    }

    public StarsClientRequestException(String message) {
        super(message);
    }

    public StarsClientRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
