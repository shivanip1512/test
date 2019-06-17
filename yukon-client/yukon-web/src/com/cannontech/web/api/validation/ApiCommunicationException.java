package com.cannontech.web.api.validation;

public class ApiCommunicationException extends RuntimeException{

    public ApiCommunicationException() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public ApiCommunicationException(String message) {
        super(message);
    }

    public ApiCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
