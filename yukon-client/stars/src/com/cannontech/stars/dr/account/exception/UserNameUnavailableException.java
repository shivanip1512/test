package com.cannontech.stars.dr.account.exception;

import com.cannontech.core.dao.DuplicateException;

public class UserNameUnavailableException extends DuplicateException 
{
    public UserNameUnavailableException(String message) {
        super(message);
    }

    public UserNameUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
