package com.cannontech.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PasswordException() {
    }

    public PasswordException(String message) {
        super(message);
    }

}
