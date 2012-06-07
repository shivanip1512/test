package com.cannontech.stars.dr.account.exception;

import com.cannontech.core.dao.DuplicateException;

public class AccountNumberUnavailableException extends DuplicateException 
{
    public AccountNumberUnavailableException(String message) {
        super(message);
    }

    public AccountNumberUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
