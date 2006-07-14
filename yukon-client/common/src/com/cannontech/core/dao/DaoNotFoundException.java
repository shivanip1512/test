package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteYukonUser;

public class DaoNotFoundException extends RuntimeException 
{
    public DaoNotFoundException(String message) {
        super(message);
    }

    public DaoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
