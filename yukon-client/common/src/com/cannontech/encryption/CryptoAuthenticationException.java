package com.cannontech.encryption;

public class CryptoAuthenticationException extends Exception {
    public CryptoAuthenticationException(String message) {
        super(message);
    }
    public CryptoAuthenticationException(Exception e) {
        super(e);
    }
    public CryptoAuthenticationException(String string, Throwable e) {
        super(string,e);
    }
}