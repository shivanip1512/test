package com.cannontech.web.api.token;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
    }

    public AuthenticationException(String e) {
        super(e);
    }
}
