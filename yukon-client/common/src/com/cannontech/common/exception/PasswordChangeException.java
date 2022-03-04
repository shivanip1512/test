package com.cannontech.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordChangeException extends RuntimeException {

    public PasswordChangeException() {
    }

    public PasswordChangeException(String message) {
        super(message);
    }

}
