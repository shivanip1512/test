package com.cannontech.core.dao;

public class DaoNotFoundException extends RuntimeException 
{
    public DaoNotFoundException(String message) {
        super(message);
    }

    public DaoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
