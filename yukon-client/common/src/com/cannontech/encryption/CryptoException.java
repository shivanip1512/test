package com.cannontech.encryption;

public class CryptoException extends Exception {
    public CryptoException(String message) {
        super(message);
    }
    public CryptoException(Exception e) {
        super(e);
    }
    public CryptoException(String string, Throwable e) {
        super(string,e);
    }
}