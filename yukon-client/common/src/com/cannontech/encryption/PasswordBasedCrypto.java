package com.cannontech.encryption;

public interface PasswordBasedCrypto {
    
    public byte[] encrypt(byte[] plainText) throws PasswordBasedCryptoException;
    public byte[] decrypt(byte[] encryptedText) throws PasswordBasedCryptoException, CryptoAuthenticationException;
    
}
