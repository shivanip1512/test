package com.cannontech.encryption;

public class PasswordBasedCryptoException extends Exception {
    public PasswordBasedCryptoException(String message) {
        super(message);
    }
    public PasswordBasedCryptoException(Exception e) {
        super(e);
    }
    public PasswordBasedCryptoException(String string, Throwable e) {
        super(string,e);
    }
}
