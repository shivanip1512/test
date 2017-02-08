package com.cannontech.encryption;

public class CertificateGenerationFailedException extends Exception {
    
    public CertificateGenerationFailedException(String message) {
        super(message);
    }
    public CertificateGenerationFailedException(Exception e) {
        super(e);
    }
    public CertificateGenerationFailedException(String string, Throwable e) {
        super(string,e);
    }
}
