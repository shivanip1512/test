package com.cannontech.core.dao;


public class UserNameUnavailableException extends DuplicateException 
{
    public UserNameUnavailableException(String message) {
        super(message);
    }

    public UserNameUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
